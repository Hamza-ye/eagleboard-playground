package com.mass3d.api.user;

import com.mass3d.api.common.IdentifiableObjectStore;
import com.mass3d.api.fieldset.FieldSet;
import com.mass3d.api.todotask.TodoTask;

public interface UserAuthorityGroupStore
    extends IdentifiableObjectStore<UserAuthorityGroup> {
//  /**
//   * Returns the number of UserAuthorityGroups which are associated with the
//   * given DataSet.
//   *
//   * @param dataSet the DataSet.
//   * @return number of UserAuthorityGroups.
//   */
//  int countDataSetUserAuthorityGroups(DataSet dataSet);

  /**
   * Returns the number of UserAuthorityGroups which are associated with the given DataSet.
   *
   * @param fieldSet the DataSet.
   * @return number of UserAuthorityGroups.
   */
  int countFieldSetUserAuthorityGroups(FieldSet fieldSet);

  /**
   * Returns the number of UserAuthorityGroups which are associated with the given TodoTask.
   *
   * @param todoTask the todoTask.
   * @return number of UserAuthorityGroups.
   */
  int countTodoTaskUserAuthorityGroups(TodoTask todoTask);
}
