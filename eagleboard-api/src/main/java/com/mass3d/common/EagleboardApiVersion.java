package com.mass3d.common;

/**
 * Enum representing web API versions. The API version is exposed through the API URL at
 * <code>/api/{version}/{resource}</code>, where <code>{version}</code> is a numeric value and must
 * match a value of this enum. If omitted, the
 * <code>DEFAULT</code> value will be used. The API resources can also be mapped
 * to all versions using the <code>ALL</code> value.
 * <p>
 * TODO The <code>DEFAULT</code> version must be updated for each release.
 *
 */
public enum EagleboardApiVersion {
  ALL(-1, true),
  V26(26),
  V27(27),
  V28(28),
  V29(29),
  V30(30),
  V31(31),
  DEFAULT(V31.getVersion());

  final int version;

  final boolean ignore;

  EagleboardApiVersion(int version) {
    this.version = version;
    this.ignore = false;
  }

  EagleboardApiVersion(int version, boolean ignore) {
    this.version = version;
    this.ignore = ignore;
  }

  public static EagleboardApiVersion getVersion(int version) {
    for (int i = 0; i < EagleboardApiVersion.values().length; i++) {
      EagleboardApiVersion v = EagleboardApiVersion.values()[i];

      if (version == v.getVersion()) {
        return v;
      }
    }

    return DEFAULT;
  }

  public int getVersion() {
    return version;
  }

  public String getVersionString() {
    return this == DEFAULT ? "" : String.valueOf(version);
  }

  public boolean isIgnore() {
    return ignore;
  }

  /**
   * Indicates whether this version is equal to the given version.
   *
   * @param apiVersion the API version.
   */
  public boolean eq(EagleboardApiVersion apiVersion) {
    return version == apiVersion.getVersion();
  }

  /**
   * Indicates whether this version is less than the given version.
   *
   * @param apiVersion the API version.
   */
  public boolean lt(EagleboardApiVersion apiVersion) {
    return version < apiVersion.getVersion();
  }

  /**
   * Indicates whether this version is less than or equal to the given version.
   *
   * @param apiVersion the API version.
   */
  public boolean le(EagleboardApiVersion apiVersion) {
    return version <= apiVersion.getVersion();
  }

  /**
   * Indicates whether this version is greater than the given version.
   *
   * @param apiVersion the API version.
   */
  public boolean gt(EagleboardApiVersion apiVersion) {
    return version > apiVersion.getVersion();
  }

  /**
   * Indicates whether this version is greater than or equal to the given version.
   *
   * @param apiVersion the API version.
   */
  public boolean ge(EagleboardApiVersion apiVersion) {
    return version >= apiVersion.getVersion();
  }
}
