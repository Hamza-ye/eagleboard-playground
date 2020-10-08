package com.mass3d.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

public class DefaultHibernateCacheManager
    implements HibernateCacheManager {

  private static final Log log = LogFactory.getLog(DefaultHibernateCacheManager.class);

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  // -------------------------------------------------------------------------
  // HibernateCacheManager implementation
  // -------------------------------------------------------------------------

  @Override
  public void clearObjectCache() {
    sessionFactory.getCache().evictEntityRegions();
    sessionFactory.getCache().evictCollectionRegions();
  }

  @Override
  public void clearQueryCache() {
    sessionFactory.getCache().evictDefaultQueryRegion();
    sessionFactory.getCache().evictQueryRegions();
  }

  @Override
  public void clearCache() {
    clearObjectCache();
    clearQueryCache();

    log.info("Cleared Hibernate caches");
  }

  @Override
  public Statistics getStatistics() {
    return sessionFactory.getStatistics();
  }
}
