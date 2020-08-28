package com.mass3d.api.common;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IdentifiableObjectStore<T> extends JpaRepository<T, Long>, GenericStore<T>{

  /**
   * Retrieves the object with the given uid.
   *
   * @param uid the uid.
   * @return the object with the given uid.
   */
  T findByUid(String uid);

  /**
   * Retrieves the object with the given name.
   *
   * @param name the name.
   * @return the object with the given name.
   */
  T findByName(String name);

  /**
   * Retrieves the object with the given code.
   *
   * @param code the code.
   * @return the object with the given code.
   */
  T findByCode(String code);
  /**
   * Retrieves a List of all objects (sorted on name).
   *
   * @return a List of all objects.
   */
  List<T> findByNameOrderByName();

  /**
   * Retrieves a List of objects where the name is like the given name.
   *
   * @param name the name.
   * @return a List of objects.
   */
  List<T> findByNameLike(String name);

  /**
   * Returns all objects that are equal to or newer than given date.
   *
   * @param created Date to compare with.
   * @return All objects equal or newer than given date.
   */
  List<T> findByCreatedAfter(Date created);

  /**
   * Returns all objects which are equal to or older than the given date.
   *
   * @param created Date to compare with.
   * @return All objects equals to or older than the given date.
   */
  List<T> findByCreatedBefore(Date created);

  /**
   * Returns all objects that are equal to or newer than given date.
   *
   * @param lastUpdated Date to compare with.
   * @return All objects equal or newer than given date.
   */
  List<T> findByLastUpdatedAfter(Date lastUpdated);

  /**
   * Returns the date of the last updated object.
   *
   * @return a Date / time stamp.
   */
  Date getLastUpdated();

  /**
   * Returns the number of objects that are equal to or newer than given last updated date.
   *
   * @param lastUpdated Date to compare to.
   * @return the number of objects equal or newer than given date.
   */
  int countByLastUpdatedAfter(Date lastUpdated);

  /**
   * Returns the number of objects that are equal to or newer than given created date.
   *
   * @param created Date to compare to.
   * @return the number of objects equal or newer than given date.
   */
  int countByCreatedAfter(Date created);

}
