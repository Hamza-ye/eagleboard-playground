package com.mass3d.hibernate;

import java.util.LinkedHashSet;
import java.util.Map;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.pojo.javassist.SerializableProxy;

public class HibernateUtils {

  public static boolean isProxy(Object object) {
    return object != null && ((object instanceof HibernateProxy)
        || (object instanceof PersistentCollection));
  }

  /**
   * If object is proxy, get unwrapped non-proxy object.
   *
   * @param proxy Object to check and unwrap
   * @return Unwrapped object if proxyied, if not just returns same object
   */
  @SuppressWarnings("unchecked")
  public static <T> T unwrap(T proxy) {
    if (!isProxy(proxy)) {
      return proxy;
    }

    Hibernate.initialize(proxy);

    if (HibernateProxy.class.isInstance(proxy)) {
      Object result = ((HibernateProxy) proxy).writeReplace();

      if (!SerializableProxy.class.isInstance(result)) {
        return (T) result;
      }
    }

    if (PersistentCollection.class.isInstance(proxy)) {
      PersistentCollection persistentCollection = (PersistentCollection) proxy;

      if (PersistentSet.class.isInstance(persistentCollection)) {
        Map<?, ?> map = (Map<?, ?>) persistentCollection.getStoredSnapshot();
        return (T) new LinkedHashSet<>(map.keySet());
      }

      return (T) persistentCollection.getStoredSnapshot();
    }

    return proxy;
  }
}
