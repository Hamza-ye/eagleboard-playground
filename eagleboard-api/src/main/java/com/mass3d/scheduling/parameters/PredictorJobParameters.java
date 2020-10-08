package com.mass3d.scheduling.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.scheduling.JobParameters;

public class PredictorJobParameters
    implements JobParameters {

  private static final long serialVersionUID = 5526554074518768146L;

  @JsonProperty
  private int relativeStart;

  @JsonProperty
  private int relativeEnd;

  @JsonProperty
  private List<String> predictors = new ArrayList<>();

  @JsonProperty
  private List<String> predictorGroups = new ArrayList<>();

  public PredictorJobParameters() {
  }

  public PredictorJobParameters(int relativeStart, int relativeEnd, List<String> predictors,
      List<String> predictorGroups) {
    this.relativeStart = relativeStart;
    this.relativeEnd = relativeEnd;
    this.predictors = predictors;
    this.predictorGroups = predictorGroups;
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

  public List<String> getPredictors() {
    return predictors;
  }

  public void setPredictors(List<String> predictors) {
    this.predictors = predictors;
  }

  public List<String> getPredictorGroups() {
    return predictorGroups;
  }

  public void setPredictorGroups(List<String> predictorGroups) {
    this.predictorGroups = predictorGroups;
  }

  @Override
  public ErrorReport validate() {
    return null;
  }
}
