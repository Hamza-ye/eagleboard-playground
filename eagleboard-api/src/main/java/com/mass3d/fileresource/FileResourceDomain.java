package com.mass3d.fileresource;

public enum FileResourceDomain {
  DATA_VALUE("dataValue"),
  PUSH_ANALYSIS("pushAnalysis"),
  DOCUMENT("document"),
  MESSAGE_ATTACHMENT("messageAttachment"),
  USER_AVATAR("userAvatar");

  /**
   * Container name to use when storing blobs of this FileResourceDomain
   */
  private String containerName;

  FileResourceDomain(String containerName) {
    this.containerName = containerName;
  }

  public String getContainerName() {
    return containerName;
  }
}
