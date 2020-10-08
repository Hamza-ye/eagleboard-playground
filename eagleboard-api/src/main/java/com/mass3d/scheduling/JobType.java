package com.mass3d.scheduling;

import com.google.common.collect.ImmutableMap;
import com.mass3d.scheduling.parameters.MockJobParameters;
import com.mass3d.scheduling.parameters.MonitoringJobParameters;
import com.mass3d.scheduling.parameters.PredictorJobParameters;
import com.mass3d.scheduling.parameters.PushAnalysisJobParameters;
import com.mass3d.scheduling.parameters.SmsJobParameters;

/**
 * Enum describing the different jobs in the system. Each job has a key, class, configurable status
 * and possibly a map containing relative endpoints for possible parameters.
 * <p>
 * The key must match the jobs bean name so that the {@link SchedulingManager} can fetch the correct
 * job
 *
 */
public enum JobType {
  DATA_STATISTICS("dataStatisticsJob", false, null, null),
  DATA_INTEGRITY("dataIntegrityJob", true, null, null),
  RESOURCE_TABLE("resourceTableJob", true, null, null),
  // Todo Eagle commented out ANALYTICS_TABLE JobType
//    ANALYTICS_TABLE( "analyticsTableJob", true, AnalyticsJobParameters.class, ImmutableMap.of(
//        "skipTableTypes", "/api/analytics/tableTypes"
//    ) ),
  DATA_SYNC("dataSynchJob", true, null, null),
  PROGRAM_DATA_SYNC("programDataSyncJob", true, null, null),
  FILE_RESOURCE_CLEANUP("fileResourceCleanUpJob", false, null, null),
  META_DATA_SYNC("metadataSyncJob", true, null, null),
  SMS_SEND("sendSmsJob", false, SmsJobParameters.class, null),
  SEND_SCHEDULED_MESSAGE("sendScheduledMessageJob", true, null, null),
  PROGRAM_NOTIFICATIONS("programNotificationsJob", true, null, null),
  VALIDATION_RESULTS_NOTIFICATION("validationResultNotificationJob", false, null, null),
  CREDENTIALS_EXPIRY_ALERT("credentialsExpiryAlertJob", false, null, null),
  MONITORING("monitoringJob", true, MonitoringJobParameters.class, ImmutableMap.of(
      "relativePeriods", "/api/periodTypes/relativePeriodTypes",
      "validationRuleGroups", "/api/validationRuleGroups"
  )),
  PUSH_ANALYSIS("pushAnalysisJob", true, PushAnalysisJobParameters.class, ImmutableMap.of(
      "pushAnalysis", "/api/pushAnalysis"
  )),
  PREDICTOR("predictorJob", true, PredictorJobParameters.class, ImmutableMap.of(
      "predictors", "/api/predictors",
      "predictorGroups", "/api/predictorGroups"
  )),
  DATA_SET_NOTIFICATION("dataSetNotificationJob", false, null, null),
  REMOVE_EXPIRED_RESERVED_VALUES("removeExpiredReservedValuesJob", false, null, null),
  KAFKA_TRACKER("kafkaTrackerJob", false, null, null),

  // For tests
  MOCK("mockJob", false, MockJobParameters.class, null),

  // To satifisfy code that used the old enum TaskCategory
  DATAVALUE_IMPORT(null, false, null, null),
  ANALYTICSTABLE_UPDATE(null, false, null, null),
  METADATA_IMPORT(null, false, null, null),
  GML_IMPORT(null, false, null, null),
  DATAVALUE_IMPORT_INTERNAL(null, false, null, null),
  EVENT_IMPORT(null, false, null, null),
  ENROLLMENT_IMPORT(null, false, null, null),
  TEI_IMPORT(null, false, null, null),
  LEADER_ELECTION("leaderElectionJob", false, null, null),
  LEADER_RENEWAL("leaderRenewalJob", false, null, null),
  COMPLETE_DATA_SET_REGISTRATION_IMPORT(null, false, null, null);

  private final String key;

  private final Class<? extends JobParameters> jobParameters;

  private final boolean configurable;

  ImmutableMap<String, String> relativeApiElements;

  JobType(String key, boolean configurable, Class<? extends JobParameters> jobParameters,
      ImmutableMap<String, String> relativeApiElements) {
    this.key = key;
    this.jobParameters = jobParameters;
    this.configurable = configurable;
    this.relativeApiElements = relativeApiElements;
  }

  public String getKey() {
    return key;
  }

  public Class<? extends JobParameters> getJobParameters() {
    return jobParameters;
  }

  public boolean isConfigurable() {
    return configurable;
  }

  public ImmutableMap<String, String> getRelativeApiElements() {
    return relativeApiElements;
  }
}
