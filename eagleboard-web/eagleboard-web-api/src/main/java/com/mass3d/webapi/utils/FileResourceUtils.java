package com.mass3d.webapi.utils;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import com.mass3d.dxf2.webmessage.WebMessageException;
import com.mass3d.dxf2.webmessage.WebMessageUtils;
import com.mass3d.fileresource.FileResource;
import com.mass3d.fileresource.FileResourceDomain;
import com.mass3d.fileresource.FileResourceService;
import com.mass3d.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileResourceUtils
{
    @Autowired
    private FileResourceService fileResourceService;

    @Autowired
    private CurrentUserService currentUserService;

    /**
     * Transfers the given multipart file content to a local temporary file.
     * 
     * @param multipartFile the multipart file.
     * @return a temporary local file.
     * @throws IOException if the file content could not be transferred.
     */
    public static File toTempFile( MultipartFile multipartFile )
        throws IOException
    {
        File tmpFile = Files.createTempFile( "com.mass3d", ".tmp" ).toFile();
        tmpFile.deleteOnExit();
        multipartFile.transferTo( tmpFile );
        return tmpFile;
    }

    /**
     * Indicates whether the content type represented by the given string is a
     * valid, known content type.
     * 
     * @param contentType the content type string.
     * @return true if the content is valid, false if not.
     */
    public static boolean isValidContentType( String contentType )
    {
        try
        {
            MimeTypeUtils.parseMimeType( contentType );
        }
        catch ( InvalidMimeTypeException ignored )
        {
            return false;
        }

        return true;
    }

    /**
     *
     * Builds a {@link FileResource} from a {@link MultipartFile}.
     *
     * @param key the key to associate to the {@link FileResource}
     * @param file a {@link MultipartFile}
     * @param domain a {@link FileResourceDomain}
     * @return a valid {@link FileResource} populated with data from the provided
     *         file
     * @throws IOException if hashing fails
     *
     */
    public static FileResource build( String key, MultipartFile file, FileResourceDomain domain )
        throws IOException
    {
        return new FileResource( key, file.getName(), file.getContentType(), file.getSize(),
            ByteSource.wrap( file.getBytes() ).hash( Hashing.md5() ).toString(), domain );
    }

    public void configureFileResourceResponse( HttpServletResponse response, FileResource fileResource )
        throws WebMessageException
    {
        ByteSource content = fileResourceService.getFileResourceContent( fileResource );

        if ( content == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "The referenced file could not be found" ) );
        }

        // ---------------------------------------------------------------------
        // Attempt to build signed URL request for content and redirect
        // ---------------------------------------------------------------------

        URI signedGetUri = fileResourceService.getSignedGetFileResourceContentUri( fileResource.getUid() );

        if ( signedGetUri != null )
        {
            response.setStatus( HttpServletResponse.SC_TEMPORARY_REDIRECT );
            response.setHeader( HttpHeaders.LOCATION, signedGetUri.toASCIIString() );

            return;
        }

        // ---------------------------------------------------------------------
        // Build response and return
        // ---------------------------------------------------------------------

        response.setContentType( fileResource.getContentType() );
        response.setContentLength( new Long( fileResource.getContentLength() ).intValue() );
        response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileResource.getName() );

        // ---------------------------------------------------------------------
        // Request signing is not available, stream content back to client
        // ---------------------------------------------------------------------

        try (InputStream in = content.openStream())
        {
            IOUtils.copy( in, response.getOutputStream() );
        }
        catch ( IOException e )
        {
            throw new WebMessageException( WebMessageUtils.error( "Failed fetching the file from storage",
                "There was an exception when trying to fetch the file from the storage backend. "
                    + "Depending on the provider the root cause could be network or file system related." ) );
        }
    }

    public FileResource saveFile( MultipartFile file, FileResourceDomain domain )
        throws WebMessageException,
        IOException
    {
        String filename = StringUtils.defaultIfBlank( FilenameUtils.getName( file.getOriginalFilename() ),
            FileResource.DEFAULT_FILENAME );

        String contentType = file.getContentType();
        contentType = FileResourceUtils.isValidContentType( contentType ) ? contentType
            : FileResource.DEFAULT_CONTENT_TYPE;

        long contentLength = file.getSize();

        if ( contentLength <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Could not read file or file is empty." ) );
        }

        ByteSource bytes = new MultipartFileByteSource( file );

        String contentMd5 = bytes.hash( Hashing.md5() ).toString();

        FileResource fileResource = new FileResource( filename, contentType, contentLength, contentMd5,
            FileResourceDomain.DATA_VALUE );
        fileResource.setAssigned( false );
        fileResource.setCreated( new Date() );
        fileResource.setUser( currentUserService.getCurrentUser() );
        fileResource.setDomain( domain );

        File tmpFile = toTempFile( file );

        String uid = fileResourceService.saveFileResource( fileResource, tmpFile );

        if ( uid == null )
        {
            throw new WebMessageException( WebMessageUtils.error( "Saving the file failed." ) );
        }

        return fileResource;
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    private class MultipartFileByteSource
        extends
        ByteSource
    {
        private MultipartFile file;

        public MultipartFileByteSource( MultipartFile file )
        {
            this.file = file;
        }

        @Override
        public InputStream openStream()
            throws IOException
        {
            try
            {
                return file.getInputStream();
            }
            catch ( IOException ioe )
            {
                return new NullInputStream( 0 );
            }
        }
    }
}
