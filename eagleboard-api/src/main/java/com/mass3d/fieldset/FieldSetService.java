package com.mass3d.fieldset;

import com.mass3d.user.User;
import java.util.Collection;
import java.util.List;

public interface FieldSetService {

  String ID = FieldSetService.class.getName();

  // -------------------------------------------------------------------------
  // FieldSet
  // -------------------------------------------------------------------------

  /**
   * Adds a FieldSet.
   *
   * @param fieldSet The FieldSet to add.
   * @return The generated unique identifier for this FieldSet.
   */
  Long addFieldSet(FieldSet fieldSet);

  /**
   * Updates a FieldSet.
   *
   * @param fieldSet The FieldSet to update.
   */
  void updateFieldSet(FieldSet fieldSet);

  /**
   * Deletes a FieldSet.
   *
   * @param fieldSet The FieldSet to delete.
   */
  void deleteFieldSet(FieldSet fieldSet);

  /**
   * Get a FieldSet
   *
   * @param id The unique identifier for the FieldSet to get.
   * @return The FieldSet with the given id or null if it does not exist.
   */
  FieldSet getFieldSet(Long id);

  /**
   * Returns the FieldSet with the given UID.
   *
   * @param uid the UID.
   * @return the FieldSet with the given UID, or null if no match.
   */
  FieldSet getFieldSet(String uid);

  /**
   * Returns the FieldSet with the given UID. Bypasses the ACL system.
   *
   * @param uid the UID.
   * @return the FieldSet with the given UID, or null if no match.
   */
  FieldSet getFieldSetNoAcl(String uid);

  /**
   * Get all FieldSets.
   *
   * @return A list containing all FieldSets.
   */
  List<FieldSet> getAllFieldSets();

  /**
   * Returns a list of data sets with the given uids.
   *
   * @param uids the collection of uids.
   * @return a list of data sets.
   */
  List<FieldSet> getFieldSetsByUid(Collection<String> uids);

//  /**
//   * Returns all FieldSets that can be collected through mobile (one organisation unit).
//   */
//  List<FieldSet> getFieldSetsForMobile(TodoTask source);
//
  /**
   * Returns all FieldSets which are not assigned to any FieldSets.
   *
   * @return all FieldSets which are not assigned to any FieldSets.
   */
  List<FieldSet> getFieldSetsWithoutTodoTasks();

  /**
   * Returns all FieldSets which are assigned to at least one FieldSet.
   *
   * @return all FieldSets which are assigned to at least one FieldSet.
   */
  List<FieldSet> getFieldSetsWithTodoTasks();
  /**
   * Returns the field sets which given user have READ access. If the current user has the ALL
   * authority then all field sets are returned.
   *
   * @param user the user to query for field set list.
   * @return a list of field sets which the given user has data read access to.
   */
  List<FieldSet> getUserDataRead(User user);

  /**
   * Returns the data sets which current user have READ access. If the current user has the ALL
   * authority then all field sets are returned.
   */
  List<FieldSet> getAllDataRead();

  /**
   * Returns the field sets which current user have WRITE access. If the current user has the ALL
   * authority then all field sets are returned.
   */
  List<FieldSet> getAllDataWrite();

  /**
   * Returns the field sets which current user have WRITE access. If the current user has the ALL
   * authority then all field sets are returned.
   *
   * @param user the user to query for field set list.
   * @return a list of field sets which given user has data write access to.
   */
  List<FieldSet> getUserDataWrite(User user);
}
