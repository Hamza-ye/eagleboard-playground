package com.mass3d.api.datafield;

import java.util.List;

public interface DataFieldService {

  String ID = DataFieldService.class.getName();

  // -------------------------------------------------------------------------
  // DataField
  // -------------------------------------------------------------------------

  /**
   * Adds a DataField.
   *
   * @param dataField the DataField to add.
   * @return a generated unique id of the added DataField.
   */
  Long addDataField(DataField dataField);

  /**
   * Updates a DataField.
   *
   * @param dataField the DataField to update.
   */
  void updateDataField(DataField dataField);

  /**
   * Deletes a DataField. The DataField is also removed from any DataFieldGroups it is a member of.
   * It is not possible to delete a DataField with children.
   *
   * @param dataField the DataField to delete.
   * @throws HierarchyViolationException if the DataField has children.
   */
  void deleteDataField(DataField dataField);

  /**
   * Returns a DataField.
   *
   * @param id the id of the DataField to return.
   * @return the DataField with the given id, or null if no match.
   */
  DataField getDataField(int id);

  /**
   * Returns the DataField with the given UID.
   *
   * @param uid the UID.
   * @return the DataField with the given UID, or null if no match.
   */
  DataField getDataField(String uid);

  /**
   * Returns the DataField with the given code.
   *
   * @param code the code.
   * @return the DataField with the given code, or null if no match.
   */
  DataField getDataFieldByCode(String code);

  /**
   * Returns all DataField.
   *
   * @return a list of all DataField, or an empty list if there are no DataFields.
   */
  List<DataField> getAllDataFields();

  /**
   * Returns all DataFields which are not assigned to any DataSet.
   *
   * @return all DataFields which are not assigned to any DataSet.
   */
  List<DataField> getDataFieldsWithoutFieldSets();

  /**
   * Returns all DataFields which are assigned to at least one DataSet.
   *
   * @return all DataFields which are assigned to at least one DataSet.
   */
  List<DataField> getDataFieldsWithFieldSets();

  /**
   * Returns all DataField which zeroIsSignificant property is true or false
   *
   * @param zeroIsSignificant is zeroIsSignificant property
   * @return a collection of all DataField
   */
  List<DataField> getDataFieldsByZeroIsSignificant(boolean zeroIsSignificant);
}
