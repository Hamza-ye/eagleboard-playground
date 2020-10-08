package com.mass3d.dataset;

import com.mass3d.common.IdentifiableObjectStore;
import java.util.List;

public interface DataSetStore
    extends IdentifiableObjectStore<DataSet> {

  String ID = DataSetStore.class.getName();

//  /**
//   * Gets all FieldSets associated with the given PeriodType.
//   *
//   * @param periodType the PeriodType.
//   * @return a list of DataSets.
//   */
//  List<DataSet> getFieldSetsByPeriodType(PeriodType periodType);

//  /**
//   * Returns all FieldSets that can be collected through mobile.
//   */
//  List<DataSet> getFieldSetsForMobile(TodoTask source);


  /**
   * Returns all FieldSets which are not assigned to any TodoTasks.
   *
   * @return all FieldSets which are not assigned to any TodoTasks.
   */
  List<DataSet> getDataSetsWithoutTodoTasks();

  /**
   * Returns all FieldSets which are assigned to at least one TodoTasks.
   *
   * @return all FieldSets which are assigned to at least one TodoTasks.
   */
  List<DataSet> getDataSetsWithTodoTasks();
}
