package com.mass3d.fieldset;

import com.mass3d.common.IdentifiableObjectStore;
import java.util.List;

public interface FieldSetStore
    extends IdentifiableObjectStore<FieldSet> {

  String ID = FieldSetStore.class.getName();

//  /**
//   * Gets all FieldSets associated with the given PeriodType.
//   *
//   * @param periodType the PeriodType.
//   * @return a list of DataSets.
//   */
//  List<FieldSet> getFieldSetsByPeriodType(PeriodType periodType);

//  /**
//   * Returns all FieldSets that can be collected through mobile.
//   */
//  List<FieldSet> getFieldSetsForMobile(TodoTask source);


  /**
   * Returns all FieldSets which are not assigned to any TodoTasks.
   *
   * @return all FieldSets which are not assigned to any TodoTasks.
   */
  List<FieldSet> getFieldSetsWithoutTodoTasks();

  /**
   * Returns all FieldSets which are assigned to at least one TodoTasks.
   *
   * @return all FieldSets which are assigned to at least one TodoTasks.
   */
  List<FieldSet> getFieldSetsWithTodoTasks();
}
