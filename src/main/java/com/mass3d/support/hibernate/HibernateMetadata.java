package com.mass3d.support.hibernate;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;

public class HibernateMetadata implements SessionFactoryBuilderFactory {

  private static final ThreadLocal<MetadataImplementor> metadataImplementor = new ThreadLocal<>();

  public static MetadataImplementor getMetadataImplementor() {
    return metadataImplementor.get();
  }

  @Override
  public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor metadataImplementor,
      SessionFactoryBuilderImplementor defaultBuilder) {
    HibernateMetadata.metadataImplementor.set(metadataImplementor);
    return defaultBuilder;
  }
}
