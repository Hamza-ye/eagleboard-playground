package com.mass3d.dxf2.metadata.version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.dxf2.common.HashCodeGenerator;
import com.mass3d.dxf2.metadata.MetadataExportParams;
import com.mass3d.dxf2.metadata.MetadataExportService;
import com.mass3d.dxf2.metadata.MetadataWrapper;
import com.mass3d.dxf2.metadata.systemsettings.MetadataSystemSettingService;
import com.mass3d.dxf2.metadata.version.exception.MetadataVersionServiceException;
import com.mass3d.keyjsonvalue.KeyJsonValue;
import com.mass3d.keyjsonvalue.KeyJsonValueService;
import com.mass3d.metadata.version.MetadataVersion;
import com.mass3d.metadata.version.MetadataVersionService;
import com.mass3d.metadata.version.MetadataVersionStore;
import com.mass3d.metadata.version.VersionType;
import com.mass3d.node.NodeService;
import com.mass3d.node.types.RootNode;
import com.mass3d.render.RenderService;
import com.mass3d.system.util.DateUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for the MetadataVersionService.
 *
 */
@Transactional
public class
DefaultMetadataVersionService
    implements MetadataVersionService
{
    private static final Log log = LogFactory.getLog( DefaultMetadataVersionService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private final MetadataVersionStore versionStore;
    private final MetadataExportService metadataExportService;
    private final KeyJsonValueService keyJsonValueService;
    private final NodeService nodeService;
    private final MetadataSystemSettingService metadataSystemSettingService;
    private final RenderService renderService;

    public DefaultMetadataVersionService( MetadataVersionStore metadataVersionStore,
        MetadataExportService metadataExportService, KeyJsonValueService keyJsonValueService,
        NodeService nodeService, MetadataSystemSettingService metadataSystemSettingService,
        RenderService renderService )
    {
        this.versionStore = metadataVersionStore;
        this.metadataExportService = metadataExportService;
        this.keyJsonValueService = keyJsonValueService;
        this.nodeService = nodeService;
        this.metadataSystemSettingService = metadataSystemSettingService;
        this.renderService = renderService;
    }

    // -------------------------------------------------------------------------
    // MetadataVersionService implementation
    // -------------------------------------------------------------------------

    @Override
    public Long addVersion( MetadataVersion version )
    {
        versionStore.save( version );

        return version.getId();
    }

    @Override
    public void updateVersion( MetadataVersion version )
    {
        versionStore.update( version );
    }

    @Override
    public void updateVersionName( int id, String name )
    {
        MetadataVersion version = getVersionById( id );

        if ( version != null )
        {
            version.setName( name );
            updateVersion( version );
        }
    }

    @Override
    public void deleteVersion( MetadataVersion version )
    {
        versionStore.delete( version );
    }

    @Override
    public MetadataVersion getVersionById( int id )
    {
        return versionStore.getVersionByKey( id );
    }

    @Override
    public MetadataVersion getVersionByName( String versionName )
    {
        return versionStore.getVersionByName( versionName );
    }

    @Override
    public List<MetadataVersion> getAllVersions()
    {
        return versionStore.getAll();
    }

    @Override
    public MetadataVersion getCurrentVersion()
    {
        try
        {
            return versionStore.getCurrentVersion();
        }
        catch ( Exception ex ) // Will have to catch Exception, as we want to throw a deterministic exception from this layer
        {
            log.error( ex.getMessage(), ex );
            throw new MetadataVersionServiceException( ex.getMessage(), ex );
        }
    }

    @Override
    public MetadataVersion getInitialVersion()
    {
        try
        {
            return versionStore.getInitialVersion();
        }
        catch ( Exception ex )
        {
            log.error( ex.getMessage(), ex );
            throw new MetadataVersionServiceException( ex.getMessage(), ex );
        }
    }

    @Override
    public List<MetadataVersion> getAllVersionsInBetween( Date startDate, Date endDate )
    {
        return versionStore.getAllVersionsInBetween( startDate, endDate );
    }

    @Override
    public Date getCreatedDate( String versionName )
    {
        MetadataVersion version = getVersionByName( versionName );
        return (version == null ? null : version.getCreated());
    }

    /**
     * This method is taking care of 3 steps:
     * 1. Generating a metadata snapshot (using the ExportService)
     * 2. Saving that snapshot to the DataStore
     * 3. Creating the actual MetadataVersion entry.
     */
    @Override
    public synchronized boolean saveVersion( VersionType versionType )
    {
        MetadataVersion currentVersion = getCurrentVersion();
        String versionName = MetadataVersionNameGenerator.getNextVersionName( currentVersion );

        Date minDate;

        if ( currentVersion == null )
        {
            minDate = null;
        }
        else
        {
            minDate = currentVersion.getCreated();
        }

        //1. Get export of metadata
        ByteArrayOutputStream os = getMetadataExport( minDate );

        //2. Save the metadata snapshot in DHIS Data Store
        String value = getBodyAsString( StandardCharsets.UTF_8, os );
        createMetadataVersionInDataStore( versionName, value );

        //3. Create an entry for the MetadataVersion
        MetadataVersion version = new MetadataVersion();
        version.setName( versionName );
        version.setCreated( new Date() );
        version.setType( versionType );
        try
        {
            String hashCode = HashCodeGenerator.getHashCode( value );
            version.setHashCode( hashCode );
        }
        catch ( NoSuchAlgorithmException e )
        {
            String message = "Exception occurred while generating MetadataVersion HashCode " + e.getMessage();
            log.error( message, e );
            throw new MetadataVersionServiceException( message, e );
        }

        try
        {
            addVersion( version );
            metadataSystemSettingService.setSystemMetadataVersion( version.getName() );
        }
        catch ( Exception ex )
        {
            String message = "Exception occurred while saving a new MetadataVersion " + ex.getMessage();
            log.error( message, ex );
            throw new MetadataVersionServiceException( message, ex );
        }

        return true;
    }

    @Override
    public String getVersionData( String versionName )
    {
        KeyJsonValue keyJsonValue = keyJsonValueService.getKeyJsonValue( MetadataVersionService.METADATASTORE, versionName );

        if ( keyJsonValue != null )
        {
            try
            {
                return renderService.fromJson( keyJsonValue.getValue(), MetadataWrapper.class ).getMetadata();
            }
            catch ( IOException e )
            {
                log.error( "Exception occurred while deserializing metadata.", e );
            }
        }

        return null;
    }

    @Override
    public void createMetadataVersionInDataStore( String versionName, String versionSnapshot )
    {
        if ( StringUtils.isEmpty( versionSnapshot ) )
        {
            throw new MetadataVersionServiceException( "The Metadata Snapshot is null while trying to create a Metadata Version entry in DataStore." );
        }

        KeyJsonValue keyJsonValue = new KeyJsonValue();
        keyJsonValue.setKey( versionName );
        keyJsonValue.setNamespace( MetadataVersionService.METADATASTORE );

        //MetadataWrapper is used to avoid Metadata keys reordering by jsonb (jsonb does not preserve keys order)
        keyJsonValue.setValue( renderService.toJsonAsString( new MetadataWrapper( versionSnapshot ) ) );

        try
        {
            keyJsonValueService.addKeyJsonValue( keyJsonValue );

        }
        catch ( Exception ex )
        {
            String message = "Exception occurred while saving the Metadata snapshot in Data Store" + ex.getMessage();
            log.error( message, ex );
            throw new MetadataVersionServiceException( message, ex );
        }
    }

    @Override
    public void deleteMetadataVersionInDataStore( String nameSpaceKey )
    {
        KeyJsonValue keyJsonValue = keyJsonValueService.getKeyJsonValue( MetadataVersionService.METADATASTORE, nameSpaceKey );

        try
        {
            keyJsonValueService.deleteKeyJsonValue( keyJsonValue );
        }
        catch ( Exception ex )
        {
            String message = "Exception occurred while trying to delete the metadata snapshot in Data Store" + ex.getMessage();
            log.error( message, ex );
            throw new MetadataVersionServiceException( message, ex );
        }
    }

    @Override
    public boolean isMetadataPassingIntegrity( MetadataVersion version, String versionSnapshot )
    {
        String metadataVersionHashCode = null;

        if ( version == null || versionSnapshot == null )
        {
            throw new MetadataVersionServiceException( "Version/Version Snapshot can't be null" );
        }

        try
        {
            metadataVersionHashCode = HashCodeGenerator.getHashCode( versionSnapshot );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new MetadataVersionServiceException( "Algorithm to hash metadata is not found in the system", e );
        }

        return (metadataVersionHashCode.equals( version.getHashCode() ));
    }

    //--------------------------------------------------------------------------
    // Private methods
    //--------------------------------------------------------------------------

    /**
     * Generates the metadata export based on the created date of the current version.
     */
    private ByteArrayOutputStream getMetadataExport( Date minDate )
    {
        ByteArrayOutputStream os = null;

        try
        {
            MetadataExportParams exportParams = new MetadataExportParams();

            if ( minDate != null )
            {
                List<String> defaultFilterList = new ArrayList<>();
                defaultFilterList.add( "lastUpdated:gte:" + DateUtils.getLongGmtDateString( minDate ) );
                exportParams.setDefaultFilter( defaultFilterList );
                metadataExportService.validate( exportParams );
            }

            os = new ByteArrayOutputStream( 1024 );
            RootNode metadata = metadataExportService.getMetadataAsNode( exportParams );
            nodeService.serialize( metadata, "application/json", os );
        }
        catch ( Exception ex ) //We have to catch the "Exception" object as no specific exception on the contract.
        {
            String message = "Exception occurred while exporting metadata for capturing a metadata version" + ex.getMessage();
            log.error( message, ex );
            throw new MetadataVersionServiceException( message, ex );
        }

        return os;
    }

    private String getBodyAsString( Charset charset, ByteArrayOutputStream os )
    {
        if ( os != null )
        {

            byte[] bytes = os.toByteArray();

            try
            {
                return new String( bytes, charset.name() );
            }
            catch ( UnsupportedEncodingException ex )
            {
               log.error("Exception occurred while trying to convert ByteArray to String. ", ex );
            }
        }

        return null;
    }
}
