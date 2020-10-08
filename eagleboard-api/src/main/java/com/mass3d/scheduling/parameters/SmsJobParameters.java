package com.mass3d.scheduling.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.scheduling.JobParameters;

public class SmsJobParameters
    implements JobParameters {

  private static final long serialVersionUID = -6116489359345047961L;

  @JsonProperty
  private String smsSubject;

  @JsonProperty
  private List<String> recipientsList = new ArrayList<>();

  @JsonProperty
  private String message;

  public SmsJobParameters() {
  }

  public SmsJobParameters(String smsSubject, String message, List<String> recipientsList) {
    this.smsSubject = smsSubject;
    this.recipientsList = recipientsList;
    this.message = message;
  }

  public String getSmsSubject() {
    return smsSubject;
  }

  public void setSmsSubject(String smsSubject) {
    this.smsSubject = smsSubject;
  }

  public List<String> getRecipientsList() {
    return recipientsList;
  }

  public void setRecipientsList(List<String> recipientsList) {
    this.recipientsList = recipientsList;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public ErrorReport validate() {
    return null;
  }
}
