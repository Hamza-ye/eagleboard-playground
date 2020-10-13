package com.mass3d.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mass3d.scheduling.JobStatus.DISABLED;

import com.mass3d.commons.util.DebugUtils;
import com.mass3d.leader.election.LeaderManager;
import com.mass3d.message.MessageService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;


/**
 * Cron refers to the cron expression used for scheduling. Key refers to the key identifying the
 * scheduled jobs.
 */
@Service("com.mass3d.scheduling.SchedulingManager")
public class DefaultSchedulingManager
    implements SchedulingManager {

  public static final String CONTINOUS_CRON = "* * * * * ?";
  public static final String HOUR_CRON = "0 0 * ? * *";
  private static final Log log = LogFactory.getLog(DefaultSchedulingManager.class);
  private Map<String, ScheduledFuture<?>> futures = new HashMap<>();

  private Map<String, ListenableFuture<?>> currentTasks = new HashMap<>();

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  //    @Autowired
  private final ApplicationContext applicationContext;

  //    @Autowired
  private final JobConfigurationService jobConfigurationService;

  //    @Autowired
  private final MessageService messageService;

  //    @Autowired
  private final LeaderManager leaderManager;

  private TaskScheduler jobScheduler;

  private AsyncListenableTaskExecutor jobExecutor;
  private List<JobConfiguration> runningJobConfigurations = new CopyOnWriteArrayList<>();

  public DefaultSchedulingManager(JobConfigurationService jobConfigurationService,
      MessageService messageService,
      LeaderManager leaderManager, @Qualifier("taskScheduler") TaskScheduler jobScheduler,
      @Qualifier("taskScheduler") AsyncListenableTaskExecutor jobExecutor,
      ApplicationContext applicationContext) {
    checkNotNull(jobConfigurationService);
    checkNotNull(messageService);
    checkNotNull(leaderManager);
    checkNotNull(jobScheduler);
    checkNotNull(jobExecutor);
    checkNotNull(applicationContext);

    this.jobConfigurationService = jobConfigurationService;
    this.messageService = messageService;
    this.leaderManager = leaderManager;
    this.jobScheduler = jobScheduler;
    this.jobExecutor = jobExecutor;
    this.applicationContext = applicationContext;
  }

  // -------------------------------------------------------------------------
  // Queue
  // -------------------------------------------------------------------------

  @PostConstruct
  public void init() {
    leaderManager.setSchedulingManager(this);
  }

  public boolean isJobConfigurationRunning(JobConfiguration jobConfiguration) {
    if (jobConfiguration.isInMemoryJob()) {
      return false;
    }

    return !jobConfiguration.isContinuousExecution() && runningJobConfigurations.stream().anyMatch(
        jobConfig -> jobConfig.getJobType().equals(jobConfiguration.getJobType()) &&
            !jobConfig.isContinuousExecution());
  }

  public void jobConfigurationStarted(JobConfiguration jobConfiguration) {
    if (!jobConfiguration.isInMemoryJob()) {
      runningJobConfigurations.add(jobConfiguration);
      jobConfigurationService.updateJobConfiguration(jobConfiguration);
    }
  }

  public void jobConfigurationFinished(JobConfiguration jobConfiguration) {
    runningJobConfigurations.remove(jobConfiguration);

    JobConfiguration tempJobConfiguration = jobConfigurationService
        .getJobConfigurationByUid(jobConfiguration.getUid());

    if (tempJobConfiguration != null) {
      if (tempJobConfiguration.getJobStatus() == DISABLED) {
        jobConfiguration.setJobStatus(DISABLED);
        jobConfiguration.setEnabled(false);
      }

      jobConfigurationService.updateJobConfiguration(jobConfiguration);
    }
  }

  // -------------------------------------------------------------------------
  // SchedulingManager implementation
  // -------------------------------------------------------------------------

  @Override
  public void scheduleJob(JobConfiguration jobConfiguration) {
    if (ifJobInSystemStop(jobConfiguration.getUid())) {
      JobInstance jobInstance = new DefaultJobInstance();

      if (jobConfiguration.getUid() != null && !futures.containsKey(jobConfiguration.getUid())) {
        ScheduledFuture<?> future = jobScheduler
            .schedule(() -> {
              try {
                jobInstance.execute(jobConfiguration, this, messageService, leaderManager);
              } catch (Exception e) {
                log.error(DebugUtils.getStackTrace(e));
              }
            }, new CronTrigger(jobConfiguration.getCronExpression()));

        futures.put(jobConfiguration.getUid(), future);

        log.info("Scheduled job: " + jobConfiguration);
      }
    }
  }

  @Override
  public void stopJob(JobConfiguration jobConfiguration) {
    if (isJobInSystem(jobConfiguration.getUid())) {
      jobConfiguration.setLastExecutedStatus(JobStatus.STOPPED);
      jobConfigurationService.updateJobConfiguration(jobConfiguration);

      internalStopJob(jobConfiguration.getUid());
    }
  }

  @Override
  public boolean executeJob(JobConfiguration jobConfiguration) {
    if (jobConfiguration != null && !isJobConfigurationRunning(jobConfiguration)) {
      internalExecuteJobConfiguration(jobConfiguration);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void executeJob(Runnable job) {
    jobExecutor.execute(job);
  }

  @Override
  public void scheduleJobWithStartTime(JobConfiguration jobConfiguration, Date startTime) {
    if (ifJobInSystemStop(jobConfiguration.getUid())) {
      JobInstance jobInstance = new DefaultJobInstance();

      if (jobConfiguration.getUid() != null && !futures.containsKey(jobConfiguration.getUid())) {
        ScheduledFuture<?> future = jobScheduler
            .schedule(() -> {
              try {
                jobInstance.execute(jobConfiguration, this, messageService, leaderManager);
              } catch (Exception e) {
                log.error(DebugUtils.getStackTrace(e));
              }
            }, startTime);

        futures.put(jobConfiguration.getUid(), future);

        log.info("Scheduled job: " + jobConfiguration);
      }
    }
  }

  @Override
  public Map<String, ScheduledFuture<?>> getAllFutureJobs() {
    return futures;
  }

  // -------------------------------------------------------------------------
  // Supportive methods
  // -------------------------------------------------------------------------

  public Job getJob(JobType jobType) {
    return (Job) applicationContext.getBean(jobType.getKey());
  }

  // -------------------------------------------------------------------------
  // Spring execution/scheduling
  // -------------------------------------------------------------------------

  @Override
  public <T> ListenableFuture<T> executeJob(Callable<T> callable) {
    return jobExecutor.submitListenable(callable);
  }

  private void internalExecuteJobConfiguration(JobConfiguration jobConfiguration) {
    JobInstance jobInstance = new DefaultJobInstance();

    ListenableFuture<?> future = jobExecutor.submitListenable(() -> {
      try {
        jobInstance.execute(jobConfiguration, this, messageService, leaderManager);
      } catch (Exception e) {
        log.error(DebugUtils.getStackTrace(e));
      }
    });
    currentTasks.put(jobConfiguration.getUid(), future);

    log.info("Scheduler initiated execution of job: " + jobConfiguration);
  }

  private boolean internalStopJob(String uid) {
    if (uid != null) {
      ScheduledFuture<?> future = futures.get(uid);

      if (future == null) {
        log.info("Tried to stop job with key '" + uid + "', but was not found");
        return true;
      } else {
        boolean result = future.cancel(true);

        futures.remove(uid);

        log.info("Stopped job with key: " + uid + " successfully: " + result);

        return result;
      }
    }

    return false;
  }

  private boolean ifJobInSystemStop(String jobKey) {
    return !isJobInSystem(jobKey) || internalStopJob(jobKey);
  }

  private boolean isJobInSystem(String jobKey) {
    return futures.get(jobKey) != null || currentTasks.get(jobKey) != null;
  }

}
