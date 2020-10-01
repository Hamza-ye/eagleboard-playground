package com.mass3d.cache;

import org.hibernate.stat.Statistics;

public interface HibernateCacheManager {

  /**
   * Evicts all entities and collections from the cache.
   */
  void clearObjectCache();

  /**
   * Evicts all queries from the cache.
   */
  void clearQueryCache();

  /**
   * Evicts all entities, collections and queries from the cache.
   */
  void clearCache();

  /**
   * Gets the statistics.
   *
   * @return the statistics.
   */
  Statistics getStatistics();
}
