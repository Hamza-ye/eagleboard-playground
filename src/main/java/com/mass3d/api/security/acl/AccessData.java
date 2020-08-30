package com.mass3d.api.security.acl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessData {

  private boolean write;

  private boolean read;

  public AccessData() {
  }

  public AccessData(boolean read, boolean write) {
    this.read = read;
    this.write = write;
  }

  @JsonProperty
  public boolean isWrite() {
    return write;
  }

  public void setWrite(boolean write) {
    this.write = write;
  }

  @JsonProperty
  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  @Override
  public String toString() {
    return "AccessData{" +
        "write=" + write +
        ", read=" + read +
        '}';
  }
}
