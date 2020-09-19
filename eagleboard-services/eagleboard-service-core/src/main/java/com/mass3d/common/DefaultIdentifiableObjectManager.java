package com.mass3d.common;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mass3d.common.IdScheme;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.common.IdentifiableObjectStore;
import com.mass3d.common.IdentifiableObjectUtils;
import com.mass3d.common.IdentifiableProperty;
import com.mass3d.common.exception.InvalidIdentifierReferenceException;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserCredentials;
import com.mass3d.schema.SchemaService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultIdentifiableObjectManager
    implements IdentifiableObjectManager {

  private static final Log log = LogFactory.getLog(DefaultIdentifiableObjectManager.class);

  /**
   * Cache for default category objects. Disabled during test phase.
   */
  private static final Cache<Class<? extends IdentifiableObject>, IdentifiableObject> DEFAULT_OBJECT_CACHE = Caffeine
      .newBuilder()
      .expireAfterAccess(2, TimeUnit.HOURS)
      .initialCapacity(4)
      .build();

  @Autowired
  protected SchemaService schemaService;

  @Autowired
  private Set<IdentifiableObjectStore<? extends IdentifiableObject>> identifiableObjectStores;

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private CurrentUserService currentUserService;
  private Map<Class<? extends IdentifiableObject>, IdentifiableObjectStore<? extends IdentifiableObject>> identifiableObjectStoreMap;

  //--------------------------------------------------------------------------
  // IdentifiableObjectManager implementation
  //--------------------------------------------------------------------------

  @Override
  public void save(IdentifiableObject object) {
    save(object, true);
  }

  @Override
  public void save(IdentifiableObject object, boolean clearSharing) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(
        object.getClass());

    if (store != null) {
      store.save(object);
    }
  }

  @Override
  public void update(IdentifiableObject object) {
    update(object, currentUserService.getCurrentUser());
  }

  @Override
  public void update(IdentifiableObject object, User user) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(
        object.getClass());

    if (store != null) {
      store.update(object, user);
    }
  }

  @Override
  public void update(List<IdentifiableObject> objects) {
    update(objects, currentUserService.getCurrentUser());
  }

  @Override
  public void update(List<IdentifiableObject> objects, User user) {
    if (objects == null || objects.isEmpty()) {
      return;
    }

    for (IdentifiableObject object : objects) {
      update(object, user);
    }
  }

  @Override
  public void delete(IdentifiableObject object) {
    delete(object, currentUserService.getCurrentUser());
  }

  @Override
  public void delete(IdentifiableObject object, User user) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(
        object.getClass());

    if (store != null) {
      store.delete(object, user);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T get(String uid) {
    for (IdentifiableObjectStore<? extends IdentifiableObject> store : identifiableObjectStores) {
      T object = (T) store.getByUid(uid);

      if (object != null) {
        return object;
      }
    }

    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T get(Class<T> clazz, Long id) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (T) store.get(id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T get(Class<T> clazz, String uid) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (T) store.getByUid(uid);
  }

  @Override
  public <T extends IdentifiableObject> boolean exists(Class<T> clazz, String uid) {
    return get(clazz, uid) != null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T get(
      Collection<Class<? extends IdentifiableObject>> classes, String uid) {
    for (Class<? extends IdentifiableObject> clazz : classes) {
      T object = (T) get(clazz, uid);

      if (object != null) {
        return object;
      }
    }

    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T get(
      Collection<Class<? extends IdentifiableObject>> classes,
      IdScheme idScheme, String identifier) {
    for (Class<? extends IdentifiableObject> clazz : classes) {
      T object = (T) getObject(clazz, idScheme, identifier);

      if (object != null) {
        return object;
      }
    }

    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> get(Class<T> clazz, Collection<String> uids) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (List<T>) store.getByUid(uids);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T getByCode(Class<T> clazz, String code) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (T) store.getByCode(code);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T getByName(Class<T> clazz, String name) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (T) store.getByName(name);
  }

  @Override
  public <T extends IdentifiableObject> T search(Class<T> clazz, String query) {
    T object = get(clazz, query);

    if (object == null) {
      object = getByCode(clazz, query);
    }

    if (object == null) {
      object = getByName(clazz, query);
    }

    return object;
  }

  @Override
  public <T extends IdentifiableObject> List<T> filter(Class<T> clazz, String query) {
    Set<T> uniqueObjects = new HashSet<>();

    T uidObject = get(clazz, query);

    if (uidObject != null) {
      uniqueObjects.add(uidObject);
    }

    T codeObject = getByCode(clazz, query);

    if (codeObject != null) {
      uniqueObjects.add(codeObject);
    }

    uniqueObjects.addAll(getLikeName(clazz, query, false));

    List<T> objects = new ArrayList<>(uniqueObjects);

    Collections.sort(objects);

    return objects;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getAll(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getAll();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getDataWriteAll(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getDataWriteAll();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getDataReadAll(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getDataReadAll();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getAllSorted(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getAllOrderedName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getByUid(Class<T> clazz, Collection<String> uids) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return null; //(List<T>) store.getByUid(uids);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getById(Class<T> clazz, Collection<Long> ids) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (List<T>) store.getById(ids);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getByCode(Class<T> clazz,
      Collection<String> codes) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getByCode(codes);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getByUidOrdered(Class<T> clazz, List<String> uids) {
    IdentifiableObjectStore<T> store = (IdentifiableObjectStore<T>) getIdentifiableObjectStore(
        clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    List<T> list = new ArrayList<>();

    if (uids != null) {
      for (String uid : uids) {
        T object = store.getByUid(uid);

        if (object != null) {
          list.add(object);
        }
      }
    }

    return list;
  }

  @Override
  public <T extends IdentifiableObject> int getCount(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store != null) {
      return store.getCount();
    }

    return 0;
  }

  @Override
  public <T extends IdentifiableObject> int getCountByCreated(Class<T> clazz, Date created) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store != null) {
      return store.getCountGeCreated(created);
    }

    return 0;
  }

  @Override
  public <T extends IdentifiableObject> int getCountByLastUpdated(Class<T> clazz,
      Date lastUpdated) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store != null) {
      return store.getCountGeLastUpdated(lastUpdated);
    }

    return 0;
  }

  @Override
  public <T extends IdentifiableObject> List<T> getLikeName(Class<T> clazz, String name) {
    return getLikeName(clazz, name, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getLikeName(Class<T> clazz, String name,
      boolean caseSensitive) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getAllLikeName(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getBetweenSorted(Class<T> clazz, int first,
      int max) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getAllOrderedName(first, max);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getBetweenLikeName(Class<T> clazz,
      Set<String> words, int first, int max) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getAllLikeName(words, first, max);
  }

  @Override
  public <T extends IdentifiableObject> Date getLastUpdated(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return store.getLastUpdated();
  }

  @Override
  public <T extends IdentifiableObject> Map<String, T> getIdMap(Class<T> clazz,
      IdentifiableProperty property) {
    return getIdMap(clazz, IdScheme.from(property));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> Map<String, T> getIdMap(Class<T> clazz, IdScheme idScheme) {
    IdentifiableObjectStore<T> store = (IdentifiableObjectStore<T>) getIdentifiableObjectStore(
        clazz);

    Map<String, T> map = new HashMap<>();

    if (store == null) {
      return map;
    }

    List<T> objects = store.getAll();

    return IdentifiableObjectUtils.getIdMap(objects, idScheme);
  }

  @Override
  public <T extends IdentifiableObject> Map<String, T> getIdMapNoAcl(Class<T> clazz,
      IdentifiableProperty property) {
    return getIdMapNoAcl(clazz, IdScheme.from(property));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> Map<String, T> getIdMapNoAcl(Class<T> clazz,
      IdScheme idScheme) {
    IdentifiableObjectStore<T> store = (IdentifiableObjectStore<T>) getIdentifiableObjectStore(
        clazz);

    Map<String, T> map = new HashMap<>();

    if (store == null) {
      return map;
    }

    List<T> objects = store.getAllNoAcl();

    return IdentifiableObjectUtils.getIdMap(objects, idScheme);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getObjects(Class<T> clazz,
      IdentifiableProperty property, Collection<String> identifiers) {
    IdentifiableObjectStore<T> store = (IdentifiableObjectStore<T>) getIdentifiableObjectStore(
        clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    if (identifiers != null && !identifiers.isEmpty()) {
      if (property == null || IdentifiableProperty.UID.equals(property)) {
        return store.getByUid(identifiers);
      } else if (IdentifiableProperty.CODE.equals(property)) {
        return store.getByCode(identifiers);
      } else if (IdentifiableProperty.NAME.equals(property)) {
        return store.getByName(identifiers);
      }

      throw new InvalidIdentifierReferenceException(
          "Invalid identifiable property / class combination: " + property);
    }

    return new ArrayList<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getObjects(Class<T> clazz,
      Collection<Long> identifiers) {
    IdentifiableObjectStore<T> store = (IdentifiableObjectStore<T>) getIdentifiableObjectStore(
        clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return store.getById(identifiers);
  }

  @Override
  public <T extends IdentifiableObject> T getObject(Class<T> clazz, IdentifiableProperty property,
      String value) {
    return getObject(clazz, IdScheme.from(property), value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T getObject(Class<T> clazz, IdScheme idScheme,
      String value) {
    IdentifiableObjectStore<T> store = (IdentifiableObjectStore<T>) getIdentifiableObjectStore(
        clazz);

    if (store == null) {
      return null;
    }

    if (!StringUtils.isEmpty(value)) {
      if (idScheme.isNull() || idScheme.is(IdentifiableProperty.UID)) {
        return store.getByUid(value);
      } else if (idScheme.is(IdentifiableProperty.CODE)) {
        return store.getByCode(value);
      } else if (idScheme.is(IdentifiableProperty.NAME)) {
        return store.getByName(value);
      }
      else if (idScheme.is(IdentifiableProperty.ID)) {
        if (Integer.valueOf(value) > 0) {
          return store.get(Long.valueOf(value));
        }
      }

      throw new InvalidIdentifierReferenceException(
          "Invalid identifiable property / class combination: " + idScheme);
    }

    return null;
  }

  @Override
  public IdentifiableObject getObject(String uid, String simpleClassName) {
    for (IdentifiableObjectStore<? extends IdentifiableObject> objectStore : identifiableObjectStores) {
      if (simpleClassName.equals(objectStore.getClazz().getSimpleName())) {
        return objectStore.getByUid(uid);
      }
    }

    return null;
  }

  @Override
  public IdentifiableObject getObject(Long id, String simpleClassName) {
    for (IdentifiableObjectStore<? extends IdentifiableObject> objectStore : identifiableObjectStores) {
      if (simpleClassName.equals(objectStore.getClazz().getSimpleName())) {
        return objectStore.get(id);
      }
    }

    return null;
  }

  @Override
  public void refresh(Object object) {
    sessionFactory.getCurrentSession().refresh(object);
  }

  @Override
  public void flush() {
    sessionFactory.getCurrentSession().flush();
  }

  @Override
  public void evict(Object object) {
    sessionFactory.getCurrentSession().evict(object);
  }


  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> T getNoAcl(Class<T> clazz, String uid) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return null;
    }

    return (T) store.getByUidNoAcl(uid);
  }

  @Override
  public <T extends IdentifiableObject> void updateNoAcl(T object) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(
        object.getClass());

    if (store != null) {
      store.updateNoAcl(object);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IdentifiableObject> List<T> getAllNoAcl(Class<T> clazz) {
    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore(clazz);

    if (store == null) {
      return new ArrayList<>();
    }

    return (List<T>) store.getAllNoAcl();
  }
  //--------------------------------------------------------------------------
  // Supportive methods
  //--------------------------------------------------------------------------

  @Override
  public boolean isDefault(IdentifiableObject object) {
    return true;
    // Todo Eagle modified
//        Map<Class<? extends IdentifiableObject>, IdentifiableObject> defaults = getDefaults();
//        if ( object == null )
//        {
//            return false;
//        }
//        Class<?> realClass = getRealClass( object.getClass() );
//        if ( !defaults.containsKey( realClass ) )
//        {
//            return false;
//        }
//        IdentifiableObject defaultObject = defaults.get( realClass );
//        return defaultObject != null && defaultObject.getUid().equals( object.getUid() );
  }

  @SuppressWarnings("unchecked")
  private <T extends IdentifiableObject> IdentifiableObjectStore<IdentifiableObject> getIdentifiableObjectStore(
      Class<T> clazz) {
    initMaps();

    IdentifiableObjectStore<? extends IdentifiableObject> store = identifiableObjectStoreMap
        .get(clazz);

    if (store == null) {
      store = identifiableObjectStoreMap.get(clazz.getSuperclass());

      if (store == null && !UserCredentials.class.isAssignableFrom(clazz)) {
        log.debug("No IdentifiableObjectStore found for class: " + clazz);
      }
    }

    return (IdentifiableObjectStore<IdentifiableObject>) store;
  }

  private void initMaps() {
    if (identifiableObjectStoreMap != null) {
      return; // Already initialized
    }

    identifiableObjectStoreMap = new HashMap<>();

    for (IdentifiableObjectStore<? extends IdentifiableObject> store : identifiableObjectStores) {
      identifiableObjectStoreMap.put(store.getClazz(), store);
    }
  }
}
