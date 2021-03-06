package com.mass3d.startup;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mass3d.scheduling.JobStatus.FAILED;
import static com.mass3d.scheduling.JobStatus.SCHEDULED;
import static com.mass3d.scheduling.JobType.CREDENTIALS_EXPIRY_ALERT;
import static com.mass3d.scheduling.JobType.DATA_SET_NOTIFICATION;
import static com.mass3d.scheduling.JobType.DATA_STATISTICS;
import static com.mass3d.scheduling.JobType.FILE_RESOURCE_CLEANUP;
import static com.mass3d.scheduling.JobType.KAFKA_TRACKER;
import static com.mass3d.scheduling.JobType.LEADER_ELECTION;
import static com.mass3d.scheduling.JobType.REMOVE_EXPIRED_RESERVED_VALUES;
import static com.mass3d.scheduling.JobType.VALIDATION_RESULTS_NOTIFICATION;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.message.MessageService;
import com.mass3d.scheduling.JobConfiguration;
import com.mass3d.scheduling.JobConfigurationService;
import com.mass3d.scheduling.JobStatus;
import com.mass3d.scheduling.JobType;
import com.mass3d.scheduling.SchedulingManager;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import com.mass3d.system.SystemService;
import com.mass3d.system.startup.AbstractStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Reschedule old jobs and execute jobs which were scheduled when the server was
 * not running.
 *
 */
public class SchedulerStart extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( SchedulerStart.class );

    private final String CRON_HOURLY = "0 0 * ? * *";
    private final String CRON_DAILY_2AM = "0 0 2 ? * *";
    private final String CRON_DAILY_7AM = "0 0 7 ? * *";
    private final String LEADER_JOB_CRON_FORMAT = "0 0/%s * * * *";
    private final String DEFAULT_FILE_RESOURCE_CLEANUP_UID = "pd6O228pqr0";
    private final String DEFAULT_FILE_RESOURCE_CLEANUP = "File resource clean up";
    private final String DEFAULT_DATA_STATISTICS_UID = "BFa3jDsbtdO";
    private final String DEFAULT_DATA_STATISTICS = "Data statistics";
    private final String DEFAULT_VALIDATION_RESULTS_NOTIFICATION_UID = "Js3vHn2AVuG";
    private final String DEFAULT_VALIDATION_RESULTS_NOTIFICATION = "Validation result notification";
    private final String DEFAULT_CREDENTIALS_EXPIRY_ALERT_UID = "sHMedQF7VYa";
    private final String DEFAULT_CREDENTIALS_EXPIRY_ALERT = "Credentials expiry alert";
    private final String DEFAULT_DATA_SET_NOTIFICATION_UID = "YvAwAmrqAtN";
    private final String DEFAULT_DATA_SET_NOTIFICATION = "Dataset notification";
    private final String DEFAULT_REMOVE_EXPIRED_RESERVED_VALUES_UID = "uwWCT2BMmlq";
    private final String DEFAULT_REMOVE_EXPIRED_RESERVED_VALUES = "Remove expired reserved values";
    private final String DEFAULT_KAFKA_TRACKER = "Kafka Tracker Consume";
    private final String DEFAULT_KAFKA_TRACKER_UID = "ntWCT2BMalb";
    private final String DEFAULT_LEADER_ELECTION_UID = "MoUd5BTQ3lY";
    private final String DEFAULT_LEADER_ELECTION = "Leader election in cluster";

//    @Autowired
    private SystemSettingManager systemSettingManager;

    private String redisEnabled;

    private String leaderElectionTime;

    private JobConfigurationService jobConfigurationService;

    @Autowired
    private SystemService systemService;

    private SchedulingManager schedulingManager;
    private MessageService messageService;

    public SchedulerStart( SystemSettingManager systemSettingManager, String redisEnabled, String leaderElectionTime,
        JobConfigurationService jobConfigurationService, SchedulingManager schedulingManager,
        MessageService messageService )
    {
        checkNotNull( systemSettingManager );
        checkNotNull( jobConfigurationService );
        checkNotNull( schedulingManager );
        checkNotNull( messageService );
        checkNotNull( leaderElectionTime );
        checkNotNull( redisEnabled );

        this.systemSettingManager = systemSettingManager;
        this.redisEnabled = redisEnabled;
        this.leaderElectionTime = leaderElectionTime;
        this.jobConfigurationService = jobConfigurationService;
        this.schedulingManager = schedulingManager;
        this.messageService = messageService;
    }
    public void setRedisEnabled( String redisEnabled )
    {
        this.redisEnabled = redisEnabled;
    }

    public void setLeaderElectionTime( String leaderElectionTime )
    {
        this.leaderElectionTime = leaderElectionTime;
    }

    @Override
    public void execute()
        throws Exception
    {
        Date now = new Date();
        List<String> unexecutedJobs = new ArrayList<>();

        List<JobConfiguration> jobConfigurations = jobConfigurationService.getAllJobConfigurations();
        addDefaultJobs( jobConfigurations );

        jobConfigurations.forEach( (jobConfig -> {
            if ( jobConfig.isEnabled() )
            {
                Date oldExecutionTime = jobConfig.getNextExecutionTime();

                jobConfig.setNextExecutionTime( null );
                jobConfig.setJobStatus( SCHEDULED );
                jobConfigurationService.updateJobConfiguration( jobConfig );

                if ( jobConfig.getLastExecutedStatus() == FAILED
                    || (!jobConfig.isContinuousExecution() && oldExecutionTime != null && oldExecutionTime.compareTo( now ) < 0) )
                {
                    unexecutedJobs.add( "\nJob [" + jobConfig.getUid() + ", " + jobConfig.getName()
                        + "] has status failed or was scheduled in server downtime. Actual execution time was supposed to be: "
                        + oldExecutionTime );
                }

                if ( JobType.KAFKA_TRACKER == jobConfig.getJobType() && !systemService.getSystemInfo().isKafka() )
                {
                    log.info( "Kafka is not enabled, disabling scheduled Kafka job." );
                }
                else
                {
                    schedulingManager.scheduleJob( jobConfig );
                }
            }
        }) );

        if ( unexecutedJobs.size() > 0 )
        {
            StringBuilder jobs = new StringBuilder();

            for ( String unexecutedJob : unexecutedJobs )
            {
                jobs.append( unexecutedJob ).append( "\n" );
            }

            messageService.sendSystemErrorNotification( "Scheduler startup",
                new Exception( "Scheduler started with one or more unexecuted jobs:\n" + jobs ) );
        }
    }

    private void addDefaultJobs( List<JobConfiguration> jobConfigurations )
    {
        log.info( "Setting up default jobs." );
        if ( verifyNoJobExist( DEFAULT_FILE_RESOURCE_CLEANUP, jobConfigurations ) )
        {
            JobConfiguration fileResourceCleanUp = new JobConfiguration( DEFAULT_FILE_RESOURCE_CLEANUP,
                FILE_RESOURCE_CLEANUP, CRON_DAILY_2AM, null, false, true );
            fileResourceCleanUp.setUid( DEFAULT_FILE_RESOURCE_CLEANUP_UID );
            fileResourceCleanUp.setLeaderOnlyJob( true );
            addAndScheduleJob( fileResourceCleanUp );
        }

        if ( verifyNoJobExist( DEFAULT_DATA_STATISTICS, jobConfigurations ) )
        {
            JobConfiguration dataStatistics = new JobConfiguration( DEFAULT_DATA_STATISTICS, DATA_STATISTICS,
                CRON_DAILY_2AM, null, false, true );
            portJob( systemSettingManager, dataStatistics, SettingKey.LAST_SUCCESSFUL_DATA_STATISTICS );
            dataStatistics.setLeaderOnlyJob( true );
            dataStatistics.setUid( DEFAULT_DATA_STATISTICS_UID );
            addAndScheduleJob( dataStatistics );
        }

        if ( verifyNoJobExist( DEFAULT_VALIDATION_RESULTS_NOTIFICATION, jobConfigurations ) )
        {
            JobConfiguration validationResultNotification = new JobConfiguration( DEFAULT_VALIDATION_RESULTS_NOTIFICATION,
                VALIDATION_RESULTS_NOTIFICATION, CRON_DAILY_7AM, null, false, true );
            validationResultNotification.setLeaderOnlyJob( true );
            validationResultNotification.setUid( DEFAULT_VALIDATION_RESULTS_NOTIFICATION_UID );
            addAndScheduleJob( validationResultNotification );
        }

        if ( verifyNoJobExist( DEFAULT_CREDENTIALS_EXPIRY_ALERT, jobConfigurations ) )
        {
            JobConfiguration credentialsExpiryAlert = new JobConfiguration( DEFAULT_CREDENTIALS_EXPIRY_ALERT,
                CREDENTIALS_EXPIRY_ALERT, CRON_DAILY_2AM, null, false, true );
            credentialsExpiryAlert.setLeaderOnlyJob( true );
            credentialsExpiryAlert.setUid( DEFAULT_CREDENTIALS_EXPIRY_ALERT_UID );
            addAndScheduleJob( credentialsExpiryAlert );
        }

        if ( verifyNoJobExist( DEFAULT_DATA_SET_NOTIFICATION, jobConfigurations ) )
        {
            JobConfiguration dataSetNotification = new JobConfiguration( DEFAULT_DATA_SET_NOTIFICATION,
                DATA_SET_NOTIFICATION, CRON_DAILY_2AM, null, false, true );
            dataSetNotification.setLeaderOnlyJob( true );
            dataSetNotification.setUid( DEFAULT_DATA_SET_NOTIFICATION_UID );
            addAndScheduleJob( dataSetNotification );
        }

        if ( verifyNoJobExist( DEFAULT_REMOVE_EXPIRED_RESERVED_VALUES, jobConfigurations ) )
        {
            JobConfiguration removeExpiredReservedValues = new JobConfiguration( DEFAULT_REMOVE_EXPIRED_RESERVED_VALUES,
                REMOVE_EXPIRED_RESERVED_VALUES, CRON_HOURLY, null, false, true );
            removeExpiredReservedValues.setLeaderOnlyJob( true );
            removeExpiredReservedValues.setUid( DEFAULT_REMOVE_EXPIRED_RESERVED_VALUES_UID );
            addAndScheduleJob( removeExpiredReservedValues );
        }

        if ( verifyNoJobExist( DEFAULT_KAFKA_TRACKER, jobConfigurations ) )
        {
            JobConfiguration kafkaTracker = new JobConfiguration( DEFAULT_KAFKA_TRACKER,
                KAFKA_TRACKER, "* * * * * ?", null, true, true );
            kafkaTracker.setUid( DEFAULT_KAFKA_TRACKER_UID );
            addAndScheduleJob( kafkaTracker );
        }

        if ( verifyNoJobExist( DEFAULT_LEADER_ELECTION, jobConfigurations ) && "true".equalsIgnoreCase( redisEnabled ) )
        {
            JobConfiguration leaderElectionJobConfiguration = new JobConfiguration( DEFAULT_LEADER_ELECTION,
                LEADER_ELECTION, String.format( LEADER_JOB_CRON_FORMAT, leaderElectionTime ), null, false, true );
            leaderElectionJobConfiguration.setLeaderOnlyJob( false );
            leaderElectionJobConfiguration.setUid( DEFAULT_LEADER_ELECTION_UID );
            addAndScheduleJob( leaderElectionJobConfiguration );
        }
        else
        {
            checkLeaderElectionJobConfiguration( jobConfigurations );
        }

    }

    private void checkLeaderElectionJobConfiguration( List<JobConfiguration> jobConfigurations )
    {
        Optional<JobConfiguration> leaderElectionJobConfigurationOptional = jobConfigurations.stream()
            .filter( jobConfiguration -> jobConfiguration.getName().equals( DEFAULT_LEADER_ELECTION ) ).findFirst();
        if ( leaderElectionJobConfigurationOptional.isPresent() )
        {
            JobConfiguration leaderElectionJobConfiguration = leaderElectionJobConfigurationOptional.get();
            leaderElectionJobConfiguration
                .setCronExpression( String.format( LEADER_JOB_CRON_FORMAT, leaderElectionTime ) );
            if ( "true".equalsIgnoreCase( redisEnabled ) )
            {
                leaderElectionJobConfiguration.setEnabled( true );
            }
            else
            {
                leaderElectionJobConfiguration.setEnabled( false );
            }
            jobConfigurationService.updateJobConfiguration( leaderElectionJobConfiguration );
        }
    }

    private boolean verifyNoJobExist( String name, List<JobConfiguration> jobConfigurations )
    {
        return jobConfigurations.stream().noneMatch( jobConfiguration -> jobConfiguration.getName().equals( name ) );
    }

    private void addAndScheduleJob( JobConfiguration jobConfiguration )
    {
        jobConfigurationService.addJobConfiguration( jobConfiguration );
        schedulingManager.scheduleJob( jobConfiguration );
    }


    public static void portJob( SystemSettingManager systemSettingManager, JobConfiguration jobConfiguration, SettingKey systemKey )
    {
        Date lastSuccessfulRun = (Date) systemSettingManager.getSystemSetting( systemKey );

        if ( lastSuccessfulRun != null )
        {
            jobConfiguration.setLastExecuted( lastSuccessfulRun );
            jobConfiguration.setLastExecutedStatus( JobStatus.COMPLETED );
        }
    }
}
