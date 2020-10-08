package com.mass3d.node.config;

import com.google.common.collect.Maps;
import java.util.Map;

public class Config {

  /**
   * Property map that can hold any key=value pair, can be used to set custom properties that only
   * certain serializers know about.
   */
  private final Map<String, Object> properties = Maps.newHashMap();
  /**
   * Inclusion strategy to use. There are a few already defined inclusions in the Inclusions enum.
   *
   * @see com.mass3d.node.config.InclusionStrategy.Include
   */
  private InclusionStrategy inclusionStrategy = InclusionStrategy.Include.NON_NULL;

  public Config() {
  }

  public InclusionStrategy getInclusionStrategy() {
    return inclusionStrategy;
  }

  public void setInclusionStrategy(InclusionStrategy inclusionStrategy) {
    this.inclusionStrategy = inclusionStrategy;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
}
