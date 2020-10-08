package com.mass3d.scheduling.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mass3d.scheduling.JobParameters;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;

public class PushAnalysisJobParameters
    implements JobParameters {

  private static final long serialVersionUID = -1848833906375595488L;

  @JsonProperty(required = true)
  private String pushAnalysis;

  public PushAnalysisJobParameters() {
  }

  public PushAnalysisJobParameters(String pushAnalysis) {
    this.pushAnalysis = pushAnalysis;
  }

  public String getPushAnalysis() {
    return pushAnalysis;
  }

  @Override
  public ErrorReport validate() {
    if (pushAnalysis == null) {
      return new ErrorReport(this.getClass(), ErrorCode.E4014, pushAnalysis, "pushAnalysis");
    }

    return null;
  }
}
