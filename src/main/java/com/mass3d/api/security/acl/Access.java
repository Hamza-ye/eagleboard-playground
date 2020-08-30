package com.mass3d.api.security.acl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Access {

  private boolean manage;

  private boolean externalize;

  private boolean write;

  private boolean read;

  private boolean update;

  private boolean delete;

  private AccessData data;

  public Access() {
  }

  public Access(boolean value) {
    this.manage = value;
    this.externalize = value;
    this.write = value;
    this.read = value;
    this.update = value;
    this.delete = value;
  }

  @JsonProperty
  public boolean isManage() {
    return manage;
  }

  public Access setManage(boolean manage) {
    this.manage = manage;
    return this;
  }

  @JsonProperty
  public boolean isExternalize() {
    return externalize;
  }

  public Access setExternalize(boolean externalize) {
    this.externalize = externalize;
    return this;
  }

  @JsonProperty
  public boolean isWrite() {
    return write;
  }

  public Access setWrite(boolean write) {
    this.write = write;
    return this;
  }

  @JsonProperty
  public boolean isRead() {
    return read;
  }

  public Access setRead(boolean read) {
    this.read = read;
    return this;
  }

  @JsonProperty
  public boolean isUpdate() {
    return update;
  }

  public Access setUpdate(boolean update) {
    this.update = update;
    return this;
  }

  @JsonProperty
  public boolean isDelete() {
    return delete;
  }

  public Access setDelete(boolean delete) {
    this.delete = delete;
    return this;
  }

  @JsonProperty
  public AccessData getData() {
    return data;
  }

  public Access setData(AccessData data) {
    this.data = data;
    return this;
  }

  @Override
  public String toString() {
    return "Access{" +
        "manage=" + manage +
        ", externalize=" + externalize +
        ", write=" + write +
        ", read=" + read +
        ", update=" + update +
        ", delete=" + delete +
        ", data=" + data +
        '}';
  }
}
