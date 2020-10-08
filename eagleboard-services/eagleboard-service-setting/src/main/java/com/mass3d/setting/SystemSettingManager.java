package com.mass3d.setting;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SystemSettingManager {

  void saveSystemSetting(SettingKey setting, Serializable value);

  void deleteSystemSetting(SettingKey setting);

  Serializable getSystemSetting(SettingKey setting);

  Serializable getSystemSetting(SettingKey setting, Serializable defaultValue);

  List<SystemSetting> getAllSystemSettings();

  /**
   * Returns all system settings as a mapping between the setting name and the value. Includes
   * system settings which have a default value but no explicitly set value.
   */
  Map<String, Serializable> getSystemSettingsAsMap();

  Map<String, Serializable> getSystemSettings(Collection<SettingKey> settings);

  void invalidateCache();

  // -------------------------------------------------------------------------
  // Specific methods
  // -------------------------------------------------------------------------

  List<String> getFlags();

  String getFlagImage();

  String getEmailHostName();

  int getEmailPort();

  String getEmailUsername();

  boolean getEmailTls();

  String getEmailSender();

  String getInstanceBaseUrl();

  boolean accountRecoveryEnabled();

  boolean selfRegistrationNoRecaptcha();

  boolean emailConfigured();

  boolean systemNotificationEmailValid();

  boolean hideUnapprovedDataInAnalytics();

  boolean isOpenIdConfigured();

  String googleAnalyticsUA();

  Integer credentialsExpires();

  boolean isConfidential(String name);
}
