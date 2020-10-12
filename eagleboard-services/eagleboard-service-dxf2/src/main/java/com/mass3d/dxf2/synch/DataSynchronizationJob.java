package com.mass3d.dxf2.synch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.dxf2.webmessage.WebMessageParseException;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.message.MessageService;
import com.mass3d.scheduling.AbstractJob;
import com.mass3d.scheduling.JobConfiguration;
import com.mass3d.scheduling.JobType;
import com.mass3d.system.notification.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component( "dataSyncJob" )
public class DataSynchronizationJob
    extends AbstractJob
{
    @Autowired
    private SynchronizationManager synchronizationManager;
    
    @Autowired
    private MessageService messageService;

    // // Todo Eagle commenting out Notifier notifier;
    @Autowired
    private Notifier notifier;

    private static final Log log = LogFactory.getLog( DataSynchronizationJob.class );

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public JobType getJobType()
    {
        return JobType.DATA_SYNC;
    }

    @Override
    public void execute( JobConfiguration jobConfiguration )
    {
        try
        {
            synchronizationManager.executeDataPush();
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( jobConfiguration, "Data synch failed: " + ex.getMessage() );
        }
        catch ( WebMessageParseException e )
        {
            log.error("Error while executing data sync todotask. "+ e.getMessage(), e );
        }

        try
        {
            // Todo Eagle commenting out
//            synchronizationManager.executeEventPush();
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( jobConfiguration, "Event synch failed: " + ex.getMessage() );
            
            messageService.sendSystemErrorNotification( "Event synch failed", ex );
        }
        // Todo Eagle commenting out
//        catch ( WebMessageParseException e )
//        {
//            log.error("Error while executing event sync todotask. "+ e.getMessage(), e );
//        }

        notifier.notify( jobConfiguration, "Data/Event synch successful" );
    }

    @Override
    public ErrorReport validate()
    {
        AvailabilityStatus isRemoteServerAvailable = synchronizationManager.isRemoteServerAvailable();

        if ( !isRemoteServerAvailable.isAvailable() )
        {
            return new ErrorReport( DataSynchronizationJob.class, ErrorCode.E7010, isRemoteServerAvailable.getMessage() );
        }

        return super.validate();
    }

}
