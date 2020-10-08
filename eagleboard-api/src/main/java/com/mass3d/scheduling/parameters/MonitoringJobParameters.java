package com.mass3d.scheduling.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.mass3d.common.CodeGenerator;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.scheduling.JobParameters;

public class MonitoringJobParameters
    implements JobParameters {

  private static final long serialVersionUID = -1683853240301569669L;

  @JsonProperty
  private int relativeStart;

  @JsonProperty
  private int relativeEnd;

  @JsonProperty
  private List<String> validationRuleGroups = new ArrayList<>();

  @JsonProperty
  private boolean sendNotifications;

  @JsonProperty
  private boolean persistResults;

  public MonitoringJobParameters() {
  }

  public MonitoringJobParameters(int relativeStart, int relativeEnd,
      List<String> validationRuleGroups,
      boolean sendNotifications, boolean persistResults) {
    this.relativeStart = relativeStart;
    this.relativeEnd = relativeEnd;
    this.validationRuleGroups =
        validationRuleGroups != null ? validationRuleGroups : Lists.newArrayList();
    this.sendNotifications = sendNotifications;
    this.persistResults = persistResults;
  }

  public int getRelativeStart() {
    return relativeStart;
  }

  public void setRelativeStart(int relativeStart) {
    this.relativeStart = relativeStart;
  }

  public int getRelativeEnd() {
    return relativeEnd;
  }

  public void setRelativeEnd(int relativeEnd) {
    this.relativeEnd = relativeEnd;
  }

  public List<String> getValidationRuleGroups() {
    return validationRuleGroups;
  }

  public void setValidationRuleGroups(List<String> validationRuleGroups) {
    this.validationRuleGroups = validationRuleGroups;
  }

  public boolean isSendNotifications() {
    return sendNotifications;
  }

  public void setSendNotifications(boolean sendNotifications) {
    this.sendNotifications = sendNotifications;
  }

  public boolean isPersistResults() {
    return persistResults;
  }

  public void setPersistResults(boolean persistResults) {
    this.persistResults = persistResults;
  }

  @Override
  public ErrorReport validate() {
    // No need to validate relatePeriods, since it will fail in the controller if invalid.

    // Validating validationRuleGroup. Since it's too late to check if the input was an array of strings or
    // something else, this is a best effort to avoid invalid data in the object.
    List<String> invalidUIDs = validationRuleGroups.stream()
        .filter((group) -> !CodeGenerator.isValidUid(group))
        .collect(Collectors.toList());

    if (invalidUIDs.size() > 0) {
      return new ErrorReport(this.getClass(), ErrorCode.E4014, invalidUIDs.get(0),
          "validationRuleGroups");
    }

    return null;
  }
}
