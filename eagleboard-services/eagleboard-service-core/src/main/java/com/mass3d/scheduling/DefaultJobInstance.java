package com.mass3d.scheduling;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.leader.election.LeaderManager;
import com.mass3d.message.MessageService;
import com.mass3d.system.util.Clock;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.scheduling.JobInstance" )
public class DefaultJobInstance 
    implements JobInstance
{
    private static final Log log = LogFactory.getLog( DefaultJobInstance.class );
    
    private static final String NOT_LEADER_SKIP_LOG = "Not a leader, skipping job with jobType:%s and name:%s";
    
    public void execute( JobConfiguration jobConfiguration, SchedulingManager schedulingManager,
        MessageService messageService, LeaderManager leaderManager )
    {
        if ( !jobConfiguration.isEnabled() )
        {
            return;
        }
        
        if ( jobConfiguration.isLeaderOnlyJob() && !leaderManager.isLeader() )
        {
            log.debug(
                String.format( NOT_LEADER_SKIP_LOG, jobConfiguration.getJobType(), jobConfiguration.getName() ) );
            return;
        }
        
        final Clock clock = new Clock().startClock();
        try
        {
            if ( jobConfiguration.isInMemoryJob() )
            {
                executeJob( jobConfiguration, schedulingManager, clock );
            }
            else if ( !schedulingManager.isJobConfigurationRunning( jobConfiguration ) )
            {
                jobConfiguration.setJobStatus( JobStatus.RUNNING );
                schedulingManager.jobConfigurationStarted( jobConfiguration );
                jobConfiguration.setNextExecutionTime( null );

                executeJob( jobConfiguration, schedulingManager, clock );

                jobConfiguration.setLastExecutedStatus( JobStatus.COMPLETED );
            }
            else
            {
                log.error(
                    "Job '" + jobConfiguration.getName() + "' failed, jobtype '" + jobConfiguration.getJobType() +
                        "' is already running." );

                messageService.sendSystemErrorNotification(
                    "Job '" + jobConfiguration.getName() + "' failed, jobtype '" + jobConfiguration.getJobType() +
                        "' is already running.",
                    new Exception( "Job '" + jobConfiguration.getName() + "' failed" ) );

                jobConfiguration.setLastExecutedStatus( JobStatus.FAILED );
            }
        }
        catch ( Exception ex )
        {
            messageService.sendSystemErrorNotification( "Job '" + jobConfiguration.getName() + "' failed", ex );
            log.error( "Job '" + jobConfiguration.getName() + "' failed", ex );

            jobConfiguration.setLastExecutedStatus( JobStatus.FAILED );
        }
        finally
        {
            setFinishingStatus( clock, schedulingManager, jobConfiguration );
        }
    }

    /**
     * Set status properties of job after finish. If the job was executed manually and the job is disabled we want
     * to set the status back to DISABLED.
     *
     * @param clock Clock for keeping track of time usage
     * @param schedulingManager reference to scheduling manager
     * @param jobConfiguration the job configuration
     */
    private void setFinishingStatus( Clock clock, SchedulingManager schedulingManager, JobConfiguration jobConfiguration )
    {
        if ( jobConfiguration.isInMemoryJob() )
        {
            return;
        }

        if ( !jobConfiguration.isContinuousExecution() )
        {
            jobConfiguration.setJobStatus( JobStatus.SCHEDULED );
        }

        if ( !jobConfiguration.isEnabled() )
        {
            jobConfiguration.setJobStatus( JobStatus.DISABLED );
        }

        jobConfiguration.setNextExecutionTime( null );
        jobConfiguration.setLastExecuted( new Date() );
        jobConfiguration.setLastRuntimeExecution( clock.time() );

        schedulingManager.jobConfigurationFinished( jobConfiguration );
    }

    /**
     * Method which calls the execute method in the job. The job will run in this thread and finish, either with success
     * or with an exception.
     *
     * @param jobConfiguration the configuration to execute
     * @param schedulingManager a reference to the scheduling manager
     * @param clock refers to start time
     * @throws Exception if the job fails
     */
    private void executeJob( JobConfiguration jobConfiguration, SchedulingManager schedulingManager,
        Clock clock )
        throws Exception
    {
        log.debug( "Job '" + jobConfiguration.getName() + "' started" );

        schedulingManager.getJob( jobConfiguration.getJobType() ).execute( jobConfiguration );

        log.debug( "Job '" + jobConfiguration.getName() + "' executed successfully. Time used: " + clock.time() );
    }
}
