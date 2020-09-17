package com.mass3d.common;

import com.mass3d.user.User;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IdentifiableObjectManager {

  String ID = IdentifiableObjectManager.class.getName();

  void save(IdentifiableObject object);

  void save(IdentifiableObject object, boolean clearSharing);

  void update(IdentifiableObject object);

  void update(IdentifiableObject object, User user);

  void update(List<IdentifiableObject> objects);

  void update(List<IdentifiableObject> objects, User user);

  void delete(IdentifiableObject object);

  void delete(IdentifiableObject object, User user);

  <T extends IdentifiableObject> T get(String uid);

  <T extends IdentifiableObject> T get(Class<T> clazz, Long id);

  <T extends IdentifiableObject> T get(Class<T> clazz, String uid);

  <T extends IdentifiableObject> boolean exists(Class<T> clazz, String uid);

  <T extends IdentifiableObject> T get(Collection<Class<? extends IdentifiableObject>> classes,
      String uid);

  <T extends IdentifiableObject> T get(Collection<Class<? extends IdentifiableObject>> classes,
      IdScheme idScheme, String value);

  <T extends IdentifiableObject> T getByCode(Class<T> clazz, String code);

  <T extends IdentifiableObject> List<T> getByCode(Class<T> clazz, Collection<String> codes);

  <T extends IdentifiableObject> T getByName(Class<T> clazz, String name);

  <T extends IdentifiableObject> T search(Class<T> clazz, String query);

  <T extends IdentifiableObject> List<T> filter(Class<T> clazz, String query);

  <T extends IdentifiableObject> List<T> getAll(Class<T> clazz);

  <T extends IdentifiableObject> List<T> getDataWriteAll(Class<T> clazz);

  <T extends IdentifiableObject> List<T> getDataReadAll(Class<T> clazz);

  <T extends IdentifiableObject> List<T> getAllSorted(Class<T> clazz);

  <T extends IdentifiableObject> List<T> getByUid(Class<T> clazz, Collection<String> uids);

  <T extends IdentifiableObject> List<T> getById(Class<T> clazz, Collection<Integer> ids);

  <T extends IdentifiableObject> List<T> getByUidOrdered(Class<T> clazz, List<String> uids);

  <T extends IdentifiableObject> List<T> getLikeName(Class<T> clazz, String name);

  <T extends IdentifiableObject> List<T> getLikeName(Class<T> clazz, String name,
      boolean caseSensitive);

  <T extends IdentifiableObject> List<T> getBetweenSorted(Class<T> clazz, int first, int max);

  <T extends IdentifiableObject> List<T> getBetweenLikeName(Class<T> clazz, Set<String> words,
      int first, int max);

  <T extends IdentifiableObject> Date getLastUpdated(Class<T> clazz);

  <T extends IdentifiableObject> Map<String, T> getIdMap(Class<T> clazz,
      IdentifiableProperty property);

  <T extends IdentifiableObject> Map<String, T> getIdMap(Class<T> clazz, IdScheme idScheme);

  <T extends IdentifiableObject> Map<String, T> getIdMapNoAcl(Class<T> clazz,
      IdentifiableProperty property);

  <T extends IdentifiableObject> Map<String, T> getIdMapNoAcl(Class<T> clazz, IdScheme idScheme);

  <T extends IdentifiableObject> List<T> getObjects(Class<T> clazz, IdentifiableProperty property,
      Collection<String> identifiers);

  <T extends IdentifiableObject> List<T> getObjects(Class<T> clazz,
      Collection<Integer> identifiers);

  <T extends IdentifiableObject> T getObject(Class<T> clazz, IdentifiableProperty property,
      String value);

  <T extends IdentifiableObject> T getObject(Class<T> clazz, IdScheme idScheme, String value);

  IdentifiableObject getObject(String uid, String simpleClassName);

  IdentifiableObject getObject(Long id, String simpleClassName);

  <T extends IdentifiableObject> int getCount(Class<T> clazz);

  <T extends IdentifiableObject> int getCountByCreated(Class<T> clazz, Date created);

  <T extends IdentifiableObject> int getCountByLastUpdated(Class<T> clazz, Date lastUpdated);

  void refresh(Object object);

  void flush();

  void evict(Object object);

  // Todo Eagle modified
//  Map<Class<? extends IdentifiableObject>, IdentifiableObject> getDefaults();

  <T extends IdentifiableObject> List<T> get(Class<T> clazz, Collection<String> uids);

  boolean isDefault(IdentifiableObject object);

  // -------------------------------------------------------------------------
  // NO ACL
  // -------------------------------------------------------------------------

  <T extends IdentifiableObject> T getNoAcl(Class<T> clazz, String uid);

  <T extends IdentifiableObject> void updateNoAcl(T object);

  <T extends IdentifiableObject> List<T> getAllNoAcl(Class<T> clazz);
}
