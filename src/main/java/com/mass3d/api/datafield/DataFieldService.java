package com.mass3d.api.datafield;

import com.mass3d.api.common.ValueType;
import java.util.List;

/**
 * Defines service functionality for DataFields.
 *
 * @version $Id: DataFieldService.java 6289 2008-11-14 17:53:24Z larshelg $
 */
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
   * Deletes a DataField. The DataField is also removed from any
   * DataFieldGroups it is a member of. It is not possible to delete a
   * DataField with children.
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
  DataField getDataField(Long id);

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
   * Returns all DataFields.
   *
   * @return a list of all DataFields, or an empty list if there
   * are no DataFields.
   */
  List<DataField> getAllDataFields();

  /**
   * Returns all DataFields of a given type.
   *
   * @param valueType the value type restriction
   * @return a list of all DataFields with the given value type,
   * or an empty list if there are no DataFields.
   */
  List<DataField> getAllDataFieldsByValueType(ValueType valueType);

//  /**
//   * Returns all DataFields with the given domain type.
//   *
//   * @param domainType the DataFieldDomainType.
//   * @return all DataFields with the given domainType.
//   */
//  List<DataField> getDataFieldsByDomainType( DataFieldDomain domainType );


//  /**
//   * Returns all DataFields with the given category combo.
//   *
//   * @param categoryCombo the CategoryCombo.
//   * @return all DataFields with the given category combo.
//   */
//  List<DataField> getDataFieldByCategoryCombo( CategoryCombo categoryCombo );
//
//  /**
//   * Returns all DataFields which are not member of any DataFieldGroups.
//   *
//   * @return all DataFields which are not member of any DataFieldGroups.
//   */
//  List<DataField> getDataFieldsWithoutGroups();

  /**
   * Returns all DataFields which are not assigned to any FieldSets.
   *
   * @return all DataFields which are not assigned to any FieldSets.
   */
  List<DataField> getDataFieldsWithoutFieldSets();

  /**
   * Returns all DataFields which are assigned to at least one FieldSet.
   *
   * @return all DataFields which are assigned to at least one FieldSet.
   */
  List<DataField> getDataFieldsWithFieldSets();

  /**
   * Returns all DataFields which have the given aggregation level assigned.
   *
   * @param aggregationLevel the aggregation level.
   * @return all DataFields which have the given aggregation level assigned.
   */
  List<DataField> getDataFieldsByAggregationLevel(int aggregationLevel);

  /**
   * Returns all DataFields which zeroIsSignificant property is true or false.
   *
   * @param zeroIsSignificant whether zero is significant is true for this query.
   * @return a collection of DataFields.
   */
  List<DataField> getDataFieldsByZeroIsSignificant(boolean zeroIsSignificant);
}
