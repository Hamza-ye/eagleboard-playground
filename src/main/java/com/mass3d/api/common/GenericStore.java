package com.mass3d.api.common;

import java.util.List;

public interface GenericStore<T> {

  /**
   * Class of the object for this store.
   */
  Class<T> getClazz();

  /**
   * Retrieves the object with the given identifier. This method will first look in the current
   * Session, then hit the database if not existing.
   *
   * @param id the object identifier.
   * @return the object identified by the given identifier.
   */
  T get(int id);

  /**
   * Gets the count of objects.
   *
   * @return the count of objects.
   */
  int getCount();

  /**
   * Retrieves a List of all objects.
   *
   * @return a List of all objects.
   */
  List<T> getAll();
}
