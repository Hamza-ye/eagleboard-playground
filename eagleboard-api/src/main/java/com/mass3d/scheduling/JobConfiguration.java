package com.mass3d.scheduling;

import static com.mass3d.scheduling.JobStatus.DISABLED;
import static com.mass3d.scheduling.JobStatus.RUNNING;
import static com.mass3d.scheduling.JobStatus.SCHEDULED;
import static com.mass3d.schema.annotation.Property.Value.FALSE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.SecondaryMetadataObject;
import java.util.Date;
import javax.annotation.Nonnull;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.schema.annotation.Property;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

/**
 * This class defines configuration for a job in the system. The job is defined with general
 * identifiers, as well as job specific, such as jobType {@link JobType}.
 * <p>
 * All system jobs should be included in JobType enum and can be scheduled/executed with {@link
 * SchedulingManager}.
 * <p>
 * The class uses a custom deserializer to handle several potential {@link JobParameters}.
 *
 * The configurable property in the configurable property in the method based on the job type we are
 * adding.
 *
 */
@JacksonXmlRootElement(localName = "jobConfiguration", namespace = DxfNamespaces.DXF_2_0)
@JsonDeserialize(using = JobConfigurationDeserializer.class)
public class JobConfiguration
    extends BaseIdentifiableObject implements SecondaryMetadataObject {

  private String cronExpression;

  private JobType jobType;

  private JobStatus jobStatus;

  private Date nextExecutionTime;

  private JobStatus lastExecutedStatus;

  private Date lastExecuted;

  private String lastRuntimeExecution;

  private JobParameters jobParameters;

  private boolean continuousExecution = false;

  private boolean enabled = true;

  private boolean inMemoryJob = false;

  private String userUid;

  private boolean leaderOnlyJob = false;

  public JobConfiguration() {
  }

  public JobConfiguration(String name, JobType jobType, String userUid, boolean inMemoryJob) {
    this.name = name;
    this.jobType = jobType;
    this.userUid = userUid;
    this.inMemoryJob = inMemoryJob;
    init();
  }

  public JobConfiguration(String name, JobType jobType, String cronExpression,
      JobParameters jobParameters,
      boolean continuousExecution, boolean enabled) {
    constructor(name, jobType, cronExpression, jobParameters, continuousExecution, enabled);
  }

  public JobConfiguration(String name, JobType jobType, String cronExpression,
      JobParameters jobParameters,
      boolean continuousExecution, boolean enabled, boolean inMemoryJob) {
    this.inMemoryJob = inMemoryJob;
    constructor(name, jobType, cronExpression, jobParameters, continuousExecution, enabled);
  }

  public JobConfiguration(String name, JobType jobType, String cronExpression,
      JobParameters jobParameters, boolean leaderOnlyJob) {
    this.leaderOnlyJob = leaderOnlyJob;
    constructor(name, jobType, cronExpression, jobParameters, false, true);
  }

  private void constructor(String name, JobType jobType, String cronExpression,
      JobParameters jobParameters,
      boolean continuousExecution, boolean enabled) {
    this.name = name;
    this.cronExpression = cronExpression;
    this.jobType = jobType;
    this.jobParameters = jobParameters;
    this.continuousExecution = continuousExecution;
    this.enabled = enabled;
    setJobStatus(enabled ? SCHEDULED : DISABLED);
    init();
  }

  private void init() {
    if (inMemoryJob) {
      setAutoFields();
    }
  }

  @JacksonXmlProperty
  @JsonProperty
  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  @JacksonXmlProperty
  @JsonProperty
  public JobType getJobType() {
    return jobType;
  }

  public void setJobType(JobType jobType) {
    this.jobType = jobType;
  }

  @JacksonXmlProperty
  @JsonProperty
  public JobStatus getJobStatus() {
    return jobStatus;
  }

  public void setJobStatus(JobStatus jobStatus) {
    this.jobStatus = jobStatus;
  }

  @JacksonXmlProperty
  @JsonProperty
  public Date getLastExecuted() {
    return lastExecuted;
  }

  public void setLastExecuted(Date lastExecuted) {
    this.lastExecuted = lastExecuted;
  }

  @JacksonXmlProperty
  @JsonProperty
  public JobStatus getLastExecutedStatus() {
    return lastExecutedStatus;
  }

  public void setLastExecutedStatus(JobStatus lastExecutedStatus) {
    this.lastExecutedStatus = lastExecutedStatus;
  }

  @JacksonXmlProperty
  @JsonProperty
  public String getLastRuntimeExecution() {
    return lastRuntimeExecution;
  }

  public void setLastRuntimeExecution(String lastRuntimeExecution) {
    this.lastRuntimeExecution = lastRuntimeExecution;
  }

  @JacksonXmlProperty
  @JsonProperty
  @Property(required = FALSE)
  public JobParameters getJobParameters() {
    return jobParameters;
  }

  public void setJobParameters(JobParameters jobParameters) {
    this.jobParameters = jobParameters;
  }

  @JacksonXmlProperty
  @JsonProperty
  public Date getNextExecutionTime() {
    return nextExecutionTime;
  }

  /**
   * Only set next execution time if the job is not continuous.
   */
  public void setNextExecutionTime(Date nextExecutionTime) {
    if (cronExpression == null || cronExpression.equals("") || cronExpression
        .equals("* * * * * ?")) {
      return;
    }

    if (nextExecutionTime != null) {
      this.nextExecutionTime = nextExecutionTime;
    } else {
      this.nextExecutionTime = new CronTrigger(cronExpression)
          .nextExecutionTime(new SimpleTriggerContext());
    }
  }

  @JacksonXmlProperty
  @JsonProperty
  public boolean isContinuousExecution() {
    return continuousExecution;
  }

  public void setContinuousExecution(boolean continuousExecution) {
    this.continuousExecution = continuousExecution;
  }

  @JacksonXmlProperty
  @JsonProperty
  public boolean isConfigurable() {
    return jobType.isConfigurable();
  }

  @JacksonXmlProperty
  @JsonProperty
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @JacksonXmlProperty
  @JsonProperty
  public boolean isLeaderOnlyJob() {
    return leaderOnlyJob;
  }

  public void setLeaderOnlyJob(boolean leaderOnlyJob) {
    this.leaderOnlyJob = leaderOnlyJob;
  }

  public boolean isInMemoryJob() {
    return inMemoryJob;
  }

  public void setInMemoryJob(boolean inMemoryJob) {
    this.inMemoryJob = inMemoryJob;
  }

  @JacksonXmlProperty
  @JsonProperty
  public String getUserUid() {
    return userUid;
  }

  public void setUserUid(String userUid) {
    this.userUid = userUid;
  }

  /**
   * Checks if this job has changes compared to the specified job configuration that are only
   * allowed for configurable jobs.
   *
   * @param jobConfiguration the job configuration that should be checked.
   * @return <code>true</code> if this job configuration has changes in fields that are only
   * allowed for configurable jobs, <code>false</code> otherwise.
   */
  public boolean hasNonConfigurableJobChanges(@Nonnull JobConfiguration jobConfiguration) {
    if (jobType != jobConfiguration.getJobType()) {
      return true;
    }
    if (!isEqualJobStatus(jobStatus, jobConfiguration.getJobStatus())) {
      return true;
    }
    if (jobParameters != jobConfiguration.getJobParameters()) {
      return true;
    }
    if (continuousExecution != jobConfiguration.isContinuousExecution()) {
      return true;
    }
    return enabled != jobConfiguration.isEnabled();
  }

  private boolean isEqualJobStatus(JobStatus jobStatus1, JobStatus jobStatus2) {
    if (jobStatus1 == jobStatus2) {
      return true;
    }

    return (jobStatus1 == SCHEDULED || jobStatus1 == RUNNING) && (jobStatus2 == SCHEDULED
        || jobStatus2 == RUNNING);
  }

  @Override
  public String toString() {
    return uid + ", " + name + ", " + jobType + ", " + cronExpression;
  }
}
