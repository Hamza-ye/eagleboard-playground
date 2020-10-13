package com.mass3d.metadata.version;

import com.mass3d.common.GenericStore;
import java.util.Date;
import java.util.List;

/**
 * Define MetadataStore to interact with the database.
 *
 */
public interface MetadataVersionStore
    extends GenericStore<MetadataVersion> {

  String ID = MetadataVersionStore.class.getName();

  /**
   * @param id Key to lookup.
   * @return MetadataVersion Value that matched key, or null if there was no match.
   */
  MetadataVersion getVersionByKey(Long id);

  /**
   * Get the version by name.
   *
   * @return MetadataVersion object matched by the name
   */
  MetadataVersion getVersionByName(String versionName);

  /**
   * Gets the current version in the system.
   *
   * @return MetadataVersion object which is the latest in the system
   */
  MetadataVersion getCurrentVersion();

  /**
   * Gets MetadataVersion 's based on start created and end created dates
   *
   * @return List of MetadataVersion objects lying in that range of dates
   */
  List<MetadataVersion> getAllVersionsInBetween(Date startDate, Date endDate);

  /**
   * @return Initial/First MetadataVersion of the system
   */
  MetadataVersion getInitialVersion();
}
