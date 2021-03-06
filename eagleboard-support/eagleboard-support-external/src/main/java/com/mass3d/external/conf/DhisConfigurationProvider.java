package com.mass3d.external.conf;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import com.mass3d.encryption.EncryptionStatus;

/**
 * Interface which provides access to the DHIS 2 configuration specified through the dhis.config
 * file.
 *
 */
public interface DhisConfigurationProvider {

  /**
   * Get configuration as a set of properties.
   *
   * @return a Properties instance.
   */
  Properties getProperties();

  /**
   * Get the property value for the given key, or the default value for the configuration key if not
   * exists.
   *
   * @param key the configuration key.
   * @return the property value.
   */
  String getProperty(ConfigurationKey key);

  /**
   * Get the property value for the given key, or the default value if not exists.
   *
   * @param key the configuration key.
   * @param defaultValue the default value.
   * @return the property value.
   */
  String getPropertyOrDefault(ConfigurationKey key, String defaultValue);

  /**
   * Indicates whether it exists a value which is not null or blank for the given key.
   *
   * @param key the configuration key.
   * @return true if a value exists.
   */
  boolean hasProperty(ConfigurationKey key);

  /**
   * Indicates whether a value for the given key is equal to "on".
   *
   * @param key the configuration key.
   * @return true if the configuration key is enabled.
   */
  boolean isEnabled(ConfigurationKey key);

  /**
   * Indicates whether a value for the given key is equal to "off".
   *
   * @param key the configuration key.
   * @return true if the configuration key is disabled.
   */
  boolean isDisabled(ConfigurationKey key);

  /**
   * Returns a GoogleCredential, if a Google service account has been configured.
   *
   * @return a GoogleCredential
   */
  Optional<GoogleCredential> getGoogleCredential();

  /**
   * Returns a GoogleAccessToken. Returns empty if no Google service account has been configured, or
   * if no refresh token could be retrieved.
   *
   * @return a GoogleAccessToken.
   * @throws IllegalStateException if an error occurred while retrieving a token.
   */
  Optional<GoogleAccessToken> getGoogleAccessToken();

  /**
   * Indicates whether the system is set to read-only mode.
   *
   * @return true if the system is in read-only mode.
   */
  public boolean isReadOnlyMode();

  /**
   * Returns the analytics server-side cache expiration in seconds.
   */
  long getAnalyticsCacheExpiration();

  /**
   * Indicates whether analytics server-side cache is enabled, i.e. whether an expiration greater
   * than 0 is defined.
   */
  public boolean isAnalyticsCacheEnabled();

  /**
   * Indicates whether clustering is enabled.
   *
   * @return true if clustering is enabled.
   */
  boolean isClusterEnabled();

  /**
   * Returns the server base url
   *
   * @return the url.
   */
  String getServerBaseUrl();

  /**
   * Indicates whether LDAP authentication is configured.
   *
   * @return true if LDAP authentication is configured.
   */
  boolean isLdapConfigured();

  /**
   * Returns the status of the encryption setup.
   *
   * @return the EncryptionStatus.
   */
  EncryptionStatus getEncryptionStatus();

  /**
   * Gets map of all properties except those which are confidential
   *
   * @return map containing name of property and its value.
   */
  Map<String, Serializable> getConfigurationsAsMap();
}
