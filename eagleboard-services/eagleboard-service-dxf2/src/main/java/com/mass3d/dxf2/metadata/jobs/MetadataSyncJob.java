package com.mass3d.dxf2.metadata.jobs;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.dxf2.metadata.MetadataImportParams;
import com.mass3d.dxf2.metadata.sync.MetadataSyncParams;
import com.mass3d.dxf2.metadata.sync.MetadataSyncPostProcessor;
import com.mass3d.dxf2.metadata.sync.MetadataSyncPreProcessor;
import com.mass3d.dxf2.metadata.sync.MetadataSyncService;
import com.mass3d.dxf2.metadata.sync.MetadataSyncSummary;
import com.mass3d.dxf2.metadata.sync.exception.DhisVersionMismatchException;
import com.mass3d.dxf2.metadata.sync.exception.MetadataSyncServiceException;
import com.mass3d.dxf2.synch.AvailabilityStatus;
import com.mass3d.dxf2.synch.SynchronizationManager;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.metadata.version.MetadataVersion;
import com.mass3d.scheduling.AbstractJob;
import com.mass3d.scheduling.JobConfiguration;
import com.mass3d.scheduling.JobType;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * This is the runnable that takes care of the Metadata Synchronization.
 * Leverages Spring RetryTemplate to exhibit retries. The retries are configurable
 * through the dhis.conf.
 *
 */
@Component( "metadataSyncJob" )
public class MetadataSyncJob
    extends AbstractJob
{
    public static String VERSION_KEY = "version";
    public static String DATA_PUSH_SUMMARY = "dataPushSummary";
    public static String EVENT_PUSH_SUMMARY = "eventPushSummary";
    public static String TRACKER_PUSH_SUMMARY = "trackerPushSummary";
    public static String GET_METADATAVERSION = "getMetadataVersion";
    public static String GET_METADATAVERSIONSLIST = "getMetadataVersionsList";
    public static String METADATA_SYNC = "metadataSync";
    public static String METADATA_SYNC_REPORT = "metadataSyncReport";
    public static String[] keys = { DATA_PUSH_SUMMARY, EVENT_PUSH_SUMMARY, GET_METADATAVERSION, GET_METADATAVERSIONSLIST, METADATA_SYNC, VERSION_KEY };

    private static final Log log = LogFactory.getLog( MetadataSyncJob.class );

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private SynchronizationManager synchronizationManager;

    @Autowired
    private MetadataSyncPreProcessor metadataSyncPreProcessor;

    @Autowired
    private MetadataSyncPostProcessor metadataSyncPostProcessor;

    @Autowired
    private MetadataSyncService metadataSyncService;

    @Autowired
    private MetadataRetryContext metadataRetryContext;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public JobType getJobType()
    {
        return JobType.META_DATA_SYNC;
    }

    @Override
    public void execute( JobConfiguration jobConfiguration )
    {
        log.info( "Metadata Sync cron Job started" );

        try
        {
            retryTemplate.execute( retryContext ->
                {
                    metadataRetryContext.setRetryContext( retryContext );
                    clearFailedVersionSettings();
                    runSyncTask( metadataRetryContext );
                    return null;
                }
                , retryContext ->
                {
                    log.info( "Metadata Sync failed! Sending mail to Admin" );
                    updateMetadataVersionFailureDetails( metadataRetryContext );
                    metadataSyncPostProcessor.sendFailureMailToAdmin( metadataRetryContext );
                    return null;
                } );
        }
        catch ( Exception e )
        {
            log.error( "Exception occurred while executing metadata sync todotask." + e.getMessage(), e );
        }
    }

    @Override
    public ErrorReport validate()
    {
        AvailabilityStatus isRemoteServerAvailable = synchronizationManager.isRemoteServerAvailable();

        if ( !isRemoteServerAvailable.isAvailable() )
        {
            return new ErrorReport( MetadataSyncJob.class, ErrorCode.E7010, isRemoteServerAvailable.getMessage() );
        }

        return super.validate();
    }

    public synchronized void runSyncTask( MetadataRetryContext context ) throws MetadataSyncServiceException, DhisVersionMismatchException
    {
        metadataSyncPreProcessor.setUp( context );

        // Todo Eagle commenting out
//        metadataSyncPreProcessor.handleAggregateDataPush( context );
//
//        metadataSyncPreProcessor.handleEventDataPush( context );
//        metadataSyncPreProcessor.handleTrackerDataPush( context );

        MetadataVersion metadataVersion = metadataSyncPreProcessor.handleCurrentMetadataVersion( context );

        List<MetadataVersion> metadataVersionList = metadataSyncPreProcessor.handleMetadataVersionsList( context, metadataVersion );

        if ( metadataVersionList != null )
        {
            for ( MetadataVersion dataVersion : metadataVersionList )
            {
                MetadataSyncParams syncParams = new MetadataSyncParams( new MetadataImportParams(), dataVersion );
                boolean isSyncRequired = metadataSyncService.isSyncRequired( syncParams );
                MetadataSyncSummary metadataSyncSummary = null;

                if ( isSyncRequired )
                {
                    metadataSyncSummary = handleMetadataSync( context, dataVersion );
                }
                else
                {
                    metadataSyncPostProcessor.handleVersionAlreadyExists( context, dataVersion );
                    break;
                }

                boolean abortStatus = metadataSyncPostProcessor.handleSyncNotificationsAndAbortStatus( metadataSyncSummary, context, dataVersion );

                if ( abortStatus )
                {
                    break;
                }

                clearFailedVersionSettings();
            }
        }

        log.info( "Metadata sync cron job ended " );
    }

    //----------------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------------

    private MetadataSyncSummary handleMetadataSync( MetadataRetryContext context, MetadataVersion dataVersion ) throws DhisVersionMismatchException
    {

        MetadataSyncParams syncParams = new MetadataSyncParams( new MetadataImportParams(), dataVersion );
        MetadataSyncSummary metadataSyncSummary = null;

        try
        {
            metadataSyncSummary = metadataSyncService.doMetadataSync( syncParams );
        }
        catch ( MetadataSyncServiceException e )
        {
            log.error( "Exception happened  while trying to do metadata sync  " + e.getMessage(), e );
            context.updateRetryContext( METADATA_SYNC, e.getMessage(), dataVersion );
            throw e;
        }
        catch ( DhisVersionMismatchException e )
        {
            context.updateRetryContext( METADATA_SYNC, e.getMessage(), dataVersion );
            throw e;
        }
        return metadataSyncSummary;

    }

    private void updateMetadataVersionFailureDetails( MetadataRetryContext retryContext )
    {
        Object version = retryContext.getRetryContext().getAttribute( VERSION_KEY );

        if ( version != null )
        {
            MetadataVersion metadataVersion = (MetadataVersion) version;
            systemSettingManager.saveSystemSetting( SettingKey.METADATA_FAILED_VERSION, metadataVersion.getName() );
            systemSettingManager.saveSystemSetting( SettingKey.METADATA_LAST_FAILED_TIME, new Date() );
        }
    }

    private void clearFailedVersionSettings()
    {
        systemSettingManager.deleteSystemSetting( SettingKey.METADATA_FAILED_VERSION );
        systemSettingManager.deleteSystemSetting( SettingKey.METADATA_LAST_FAILED_TIME );
    }
}
