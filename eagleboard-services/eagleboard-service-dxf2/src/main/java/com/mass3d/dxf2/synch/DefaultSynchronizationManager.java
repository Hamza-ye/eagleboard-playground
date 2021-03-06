package com.mass3d.dxf2.synch;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.dxf2.common.ImportSummaryResponseExtractor;
import com.mass3d.dxf2.importsummary.ImportCount;
import com.mass3d.dxf2.importsummary.ImportStatus;
import com.mass3d.dxf2.importsummary.ImportSummary;
import com.mass3d.dxf2.metadata.AtomicMode;
import com.mass3d.dxf2.metadata.Metadata;
import com.mass3d.dxf2.metadata.MetadataImportParams;
import com.mass3d.dxf2.metadata.MetadataImportService;
import com.mass3d.dxf2.metadata.feedback.ImportReport;
import com.mass3d.dxf2.sync.SyncEndpoint;
import com.mass3d.dxf2.sync.SyncUtils;
import com.mass3d.dxf2.webmessage.WebMessageParseException;
import com.mass3d.dxf2.webmessage.utils.WebMessageParseUtils;
import com.mass3d.render.DefaultRenderService;
import com.mass3d.render.RenderService;
import com.mass3d.schema.Schema;
import com.mass3d.schema.SchemaService;
import com.mass3d.schema.descriptors.MessageConversationSchemaDescriptor;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import com.mass3d.system.util.CodecUtils;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

@Component( "com.mass3d.dxf2.synch.SynchronizationManager" )
public class DefaultSynchronizationManager
    implements SynchronizationManager
{
    private static final Log log = LogFactory.getLog( DefaultSynchronizationManager.class );

    private static final String HEADER_AUTHORIZATION = "Authorization";

//    @Autowired
//    private DataValueSetService dataValueSetService;
//
//    @Autowired
//    private DataValueService dataValueService;

    @Autowired
    private MetadataImportService importService;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private EventService eventService;

    @Autowired
    private RenderService renderService;

    // -------------------------------------------------------------------------
    // SynchronizatonManager implementation
    // -------------------------------------------------------------------------

    @Override
    public AvailabilityStatus isRemoteServerAvailable()
    {
        return SyncUtils.isRemoteServerAvailable( systemSettingManager, restTemplate );
    }

    @Override
    public ImportSummary executeDataPush() throws WebMessageParseException
    {
        AvailabilityStatus availability = isRemoteServerAvailable();

        if ( !availability.isAvailable() )
        {
            log.info( "Aborting synch, server not available" );
            return null;
        }

        String url = systemSettingManager.getSystemSetting( SettingKey.REMOTE_INSTANCE_URL ) + SyncEndpoint.DATA_VALUE_SETS.getPath();
        String username = (String) systemSettingManager.getSystemSetting( SettingKey.REMOTE_INSTANCE_USERNAME );
        String password = (String) systemSettingManager.getSystemSetting( SettingKey.REMOTE_INSTANCE_PASSWORD );

        SystemInstance instance = new SystemInstance( url, username, password );

        return executeDataPush( instance );
    }

    /**
     * Executes a push of data values to the given remote instance.
     *
     * @param instance the remote system instance.
     * @return an ImportSummary.
     */
    private ImportSummary executeDataPush( SystemInstance instance ) throws WebMessageParseException
    {
        // ---------------------------------------------------------------------
        // Set time for last success to start of process to make data saved
        // subsequently part of next synch process without being ignored
        // ---------------------------------------------------------------------

        final Date startTime = new Date();
        final Date lastSuccessTime = getLastDataSynchSuccessFallback();
        final Date skipChangedBefore = (Date) systemSettingManager
            .getSystemSetting( SettingKey.SKIP_SYNCHRONIZATION_FOR_DATA_CHANGED_BEFORE );
        final Date lastUpdatedAfter = lastSuccessTime.after( skipChangedBefore ) ? lastSuccessTime : skipChangedBefore;
        // Todo Eagle DataValue related commented out
        final int objectsToSynchronize = 0; //dataValueService.getDataValueCountLastUpdatedAfter( lastUpdatedAfter, true );

        log.info( "DataValues last changed before " + skipChangedBefore + " will not be synchronized." );

        if ( objectsToSynchronize == 0 )
        {
            SyncUtils.setLastSyncSuccess( systemSettingManager, SettingKey.LAST_SUCCESSFUL_DATA_SYNC, startTime );
            log.debug( "Skipping data values push, no new or updated data values" );

            ImportCount importCount = new ImportCount( 0, 0, 0, 0 );
            return new ImportSummary( ImportStatus.SUCCESS, "No new or updated data values to push.", importCount );
        }

        log.info( "Values: " + objectsToSynchronize + " since last synchronization success: " + lastSuccessTime );

        log.info( "Remote server POST URL: " + instance.getUrl() );

        final RequestCallback requestCallback = request ->
        {
            request.getHeaders().setContentType( MediaType.APPLICATION_JSON );
            request.getHeaders().add( HEADER_AUTHORIZATION, CodecUtils
                .getBasicAuthString( instance.getUsername(), instance.getPassword() ) );

            // Todo Eagle Datavalue related commented out
//            dataValueSetService.writeDataValueSetJson( lastUpdatedAfter, request.getBody(), new IdSchemes() );
        };

        ResponseExtractor<ImportSummary> responseExtractor = new ImportSummaryResponseExtractor();
        ImportSummary summary = null;
        try
        {
            summary = restTemplate.execute( instance.getUrl(), HttpMethod.POST, requestCallback, responseExtractor );
        }
        catch ( HttpClientErrorException ex )
        {
            String responseBody = ex.getResponseBodyAsString();
            summary = WebMessageParseUtils.fromWebMessageResponse( responseBody, ImportSummary.class );
        }
        catch ( HttpServerErrorException ex )
        {
            String responseBody = ex.getResponseBodyAsString();
            log.error( "Internal error happened during event data push: " + responseBody, ex );
            throw ex;
        }
        catch ( ResourceAccessException ex )
        {
            log.error( "Exception during event data push: " + ex.getMessage(), ex );
            throw ex;
        }

        log.info( "Synch summary: " + summary );

        if ( summary != null && ImportStatus.SUCCESS.equals( summary.getStatus() ) )
        {
            setLastDataSynchSuccess( startTime );
            log.info( "Synch successful, setting last success time: " + startTime );
        }
        else
        {
            log.warn( "Sync failed: " + summary );
        }

        return summary;
    }

//    @Override
//    public ImportSummaries executeEventPush() throws WebMessageParseException
//    {
//        AvailabilityStatus availability = isRemoteServerAvailable();
//
//        if ( !availability.isAvailable() )
//        {
//            log.info( "Aborting synch, server not available" );
//            return null;
//        }
//
//        // ---------------------------------------------------------------------
//        // Set time for last success to start of process to make data saved
//        // subsequently part of next synch process without being ignored
//        // ---------------------------------------------------------------------
//
//        final Date startTime = new Date();
//        final Date lastSuccessTime = getLastEventSynchSuccessFallback();
//        final Date skipChangedBefore = (Date) systemSettingManager
//            .getSystemSetting( SettingKey.SKIP_SYNCHRONIZATION_FOR_DATA_CHANGED_BEFORE );
//        final Date lastUpdatedAfter = lastSuccessTime.after( skipChangedBefore ) ? lastSuccessTime : skipChangedBefore;
//        final int lastUpdatedEventsCount = eventService.getAnonymousEventValuesCountLastUpdatedAfter( lastUpdatedAfter );
//
//        log.info( "Events last changed before " + skipChangedBefore + " will not be synchronized." );
//
//        if ( lastUpdatedEventsCount == 0 )
//        {
//            SyncUtils.setLastSyncSuccess( systemSettingManager, SettingKey.LAST_SUCCESSFUL_EVENT_DATA_SYNC, startTime );
//            log.info( "Skipping events push, no new or updated events" );
//
//            ImportCount importCount = new ImportCount( 0, 0, 0, 0 );
//            ImportSummary importSummary = new ImportSummary( ImportStatus.SUCCESS, "No new or updated events to push.",
//                importCount );
//            return new ImportSummaries().addImportSummary( importSummary );
//        }
//
//        log.info( "Events: " + lastUpdatedEventsCount + " since last synchronization success: " + lastSuccessTime );
//
//        String url = systemSettingManager.getSystemSetting(
//            SettingKey.REMOTE_INSTANCE_URL ) + "/api/events";
//
//        log.info( "Remote server events POST URL: " + url );
//
//        final String username = (String) systemSettingManager.getSystemSetting( SettingKey.REMOTE_INSTANCE_USERNAME );
//        final String password = (String) systemSettingManager.getSystemSetting( SettingKey.REMOTE_INSTANCE_PASSWORD );
//
//        final RequestCallback requestCallback = new RequestCallback()
//        {
//            @Override
//            public void doWithRequest( ClientHttpRequest request )
//                throws IOException
//            {
//                request.getHeaders().setContentType( MediaType.APPLICATION_JSON );
//                request.getHeaders().add( HEADER_AUTHORIZATION, CodecUtils
//                    .getBasicAuthString( username, password ) );
//                Events result = eventService.getAnonymousEventValuesLastUpdatedAfter( lastSuccessTime );
//                renderService.toJson( request.getBody(), result );
//            }
//        };
//
//        ResponseExtractor<ImportSummaries> responseExtractor = new ImportSummariesResponseExtractor();
//        ImportSummaries summaries = null;
//        try
//        {
//            summaries = restTemplate.execute( url, HttpMethod.POST, requestCallback, responseExtractor );
//        }
//        catch ( HttpClientErrorException ex )
//        {
//            String responseBody = ex.getResponseBodyAsString();
//            summaries = WebMessageParseUtils.fromWebMessageResponse( responseBody, ImportSummaries.class );
//        }
//        catch ( HttpServerErrorException ex )
//        {
//            String responseBody = ex.getResponseBodyAsString();
//            log.error( "Internal error happened during event data push: " + responseBody, ex );
//            throw ex;
//        }
//        catch ( ResourceAccessException ex )
//        {
//            log.error( "Exception during event data push: " + ex.getMessage(), ex );
//            throw ex;
//        }
//
//        log.info( "Event synch summary: " + summaries );
//        boolean isError = false;
//
//        if ( summaries != null )
//        {
//
//            for ( ImportSummary summary : summaries.getImportSummaries() )
//            {
//                if ( ImportStatus.ERROR.equals( summary.getStatus() ) || ImportStatus.WARNING.equals( summary.getStatus() ) )
//                {
//                    isError = true;
//                    log.debug( "Sync failed: " + summaries );
//                    break;
//                }
//            }
//        }
//
//        if ( !isError )
//        {
//            setLastEventSynchSuccess( startTime );
//            log.info( "Synch successful, setting last success time: " + startTime );
//        }
//
//        return summaries;
//    }

    @Override
    public Date getLastDataSynchSuccess()
    {
        return (Date) systemSettingManager.getSystemSetting( SettingKey.LAST_SUCCESSFUL_DATA_SYNC );
    }

    @Override
    public Date getLastEventSynchSuccess()
    {
        return (Date) systemSettingManager.getSystemSetting( SettingKey.LAST_SUCCESSFUL_EVENT_DATA_SYNC );
    }

    @Override
    public ImportReport executeMetadataPull( String url )
    {
        User user = currentUserService.getCurrentUser();

        String userUid = user != null ? user.getUid() : null;

        log.info( String.format( "Metadata pull, url: %s, user: %s", url, userUid ) );

        String json = restTemplate.getForObject( url, String.class );

        Metadata metadata = null;

        try
        {
            metadata = DefaultRenderService.getJsonMapper().readValue( json, Metadata.class );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to parse remote JSON document", ex );
        }

        MetadataImportParams importParams = new MetadataImportParams();
        importParams.setSkipSharing( true );
        importParams.setAtomicMode( AtomicMode.NONE );

        //Remove MessageConversation schema
        List<Schema> metadataSchemas = schemaService.getMetadataSchemas().stream()
            .filter( schema -> !schema.getPlural().equals( MessageConversationSchemaDescriptor.PLURAL ) )
            .collect( Collectors.toList());

        importParams.addMetadata( metadataSchemas, metadata );

        return importService.importMetadata( importParams );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Gets the time of the last successful data synchronization operation. If not set,
     * the current date subtracted by three days is returned.
     */
    private Date getLastDataSynchSuccessFallback()
    {
        Date fallback = new DateTime().minusDays( 3 ).toDate();

        return (Date) systemSettingManager.getSystemSetting( SettingKey.LAST_SUCCESSFUL_DATA_SYNC, fallback );
    }

    /**
     * Gets the time of the last successful event synchronization operation. If not set,
     * the current date subtracted by three days is returned.
     */
    private Date getLastEventSynchSuccessFallback()
    {
        Date fallback = new DateTime().minusDays( 3 ).toDate();

        return (Date) systemSettingManager.getSystemSetting( SettingKey.LAST_SUCCESSFUL_EVENT_DATA_SYNC, fallback );
    }

    /**
     * Sets the time of the last successful data synchronization operation.
     */
    private void setLastDataSynchSuccess( Date time )
    {
        systemSettingManager.saveSystemSetting( SettingKey.LAST_SUCCESSFUL_DATA_SYNC, time );
    }

    /**
     * Sets the time of the last successful event synchronization operation.
     */
    private void setLastEventSynchSuccess( Date time )
    {
        systemSettingManager.saveSystemSetting( SettingKey.LAST_SUCCESSFUL_EVENT_DATA_SYNC, time );
    }
}
