package com.mass3d.configuration;

public enum SettingType {
  USER_SETTING("user-settings"),
  SYSTEM_SETTING("system-settings"),
  CONFIGURATION("dhis-configurations");

  private String key;

  SettingType(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
