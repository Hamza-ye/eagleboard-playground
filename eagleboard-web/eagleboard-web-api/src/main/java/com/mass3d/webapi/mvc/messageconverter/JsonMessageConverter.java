package com.mass3d.webapi.mvc.messageconverter;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.mass3d.common.Compression;
import com.mass3d.node.NodeService;
import com.mass3d.node.types.RootNode;
import com.mass3d.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class JsonMessageConverter extends AbstractHttpMessageConverter<RootNode>
{
    public static final ImmutableList<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json" ) )
        .build();

    public static final ImmutableList<MediaType> GZIP_SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json+gzip" ) )
        .build();

    public static final ImmutableList<MediaType> ZIP_SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json+zip" ) )
        .build();

    @Autowired
    private NodeService nodeService;

    private Compression compression;

    public JsonMessageConverter( Compression compression )
    {
        this.compression = compression;

        switch ( this.compression )
        {
            case NONE:
                setSupportedMediaTypes( SUPPORTED_MEDIA_TYPES );
                break;
            case GZIP:
                setSupportedMediaTypes( GZIP_SUPPORTED_MEDIA_TYPES );
                break;
            case ZIP:
                setSupportedMediaTypes( ZIP_SUPPORTED_MEDIA_TYPES );
        }
    }

    @Override
    protected boolean supports( Class<?> clazz )
    {
        return RootNode.class.equals( clazz );
    }

    @Override
    protected boolean canRead( MediaType mediaType )
    {
        return false;
    }

    @Override
    protected RootNode readInternal( Class<? extends RootNode> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException
    {
        return null;
    }

    @Override
    protected void writeInternal( RootNode rootNode, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException
    {
        if ( Compression.GZIP == compression )
        {
            if ( !outputMessage.getHeaders().getFirst( ContextUtils.HEADER_CONTENT_DISPOSITION  ).contains( "attachment" ) )
            {
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_DISPOSITION, "attachment; filename=metadata.json.gz" );
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            }

            GZIPOutputStream outputStream = new GZIPOutputStream( outputMessage.getBody() );
            nodeService.serialize( rootNode, "application/json", outputStream );
            outputStream.close();
        }
        else if ( Compression.ZIP == compression )
        {
            if ( !outputMessage.getHeaders().getFirst( ContextUtils.HEADER_CONTENT_DISPOSITION  ).contains( "attachment" ) )
            {
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_DISPOSITION, "attachment; filename=metadata.json.zip" );
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            }

            ZipOutputStream outputStream = new ZipOutputStream( outputMessage.getBody() );
            outputStream.putNextEntry( new ZipEntry( "metadata.json" ) );
            nodeService.serialize( rootNode, "application/json", outputStream );
            outputStream.close();
        }
        else
        {
            nodeService.serialize( rootNode, "application/json", outputMessage.getBody() );
            outputMessage.getBody().close();
        }
    }
}
