package com.mass3d.api.common;

public interface VersionedObject {

  /**
   * Returns the current version.
   */
  int getVersion();

  /**
   * Sets the version.
   */
  void setVersion(int version);

  /**
   * Increases the version and returns its new version.
   */
  int increaseVersion();
}
