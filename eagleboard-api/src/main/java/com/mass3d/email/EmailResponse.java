package com.mass3d.email;

public enum EmailResponse {
  SENT("success"),
  FAILED("failed"),
  ABORTED("aborted"),
  NOT_CONFIGURED("no configuration found");

  private String responseMessage;

  EmailResponse(String responseMessage) {
    this.responseMessage = responseMessage;
  }

  public String getResponseMessage() {
    return responseMessage;
  }

  public void setResponseMessage(String responseMessage) {
    this.responseMessage = responseMessage;
  }
}
