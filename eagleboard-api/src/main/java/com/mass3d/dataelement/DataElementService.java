package com.mass3d.dataelement;

import com.mass3d.common.ValueType;
import java.util.List;

/**
 * Defines service functionality for DataFields.
 *
 * @version $Id: DataElementService.java 6289 2008-11-14 17:53:24Z larshelg $
 */
public interface DataElementService {

  String ID = DataElementService.class.getName();

  // -------------------------------------------------------------------------
  // DataElement
  // -------------------------------------------------------------------------

  /**
   * Adds a DataElement.
   *
   * @param dataElement the DataElement to add.
   * @return a generated unique id of the added DataElement.
   */
  Long addDataElement(DataElement dataElement);

  /**
   * Updates a DataElement.
   *
   * @param dataElement the DataElement to update.
   */
  void updateDataElement(DataElement dataElement);

  /**
   * Deletes a DataElement. The DataElement is also removed from any
   * DataFieldGroups it is a member of. It is not possible to delete a
   * DataElement with children.
   *
   * @param dataElement the DataElement to delete.
   * @throws HierarchyViolationException if the DataElement has children.
   */
  void deleteDataElement(DataElement dataElement);

  /**
   * Returns a DataElement.
   *
   * @param id the id of the DataElement to return.
   * @return the DataElement with the given id, or null if no match.
   */
  DataElement getDataElement(Long id);

  /**
   * Returns the DataElement with the given UID.
   *
   * @param uid the UID.
   * @return the DataElement with the given UID, or null if no match.
   */
  DataElement getDataElement(String uid);

  /**
   * Returns the DataElement with the given code.
   *
   * @param code the code.
   * @return the DataElement with the given code, or null if no match.
   */
  DataElement getDataElementByCode(String code);

  /**
   * Returns all DataFields.
   *
   * @return a list of all DataFields, or an empty list if there
   * are no DataFields.
   */
  List<DataElement> getAllDataElements();

  /**
   * Returns all DataFields of a given type.
   *
   * @param valueType the value type restriction
   * @return a list of all DataFields with the given value type,
   * or an empty list if there are no DataFields.
   */
  List<DataElement> getAllDataElementsByValueType(ValueType valueType);

//  /**
//   * Returns all DataFields with the given domain type.
//   *
//   * @param domainType the DataFieldDomainType.
//   * @return all DataFields with the given domainType.
//   */
//  List<DataElement> getDataFieldsByDomainType( DataFieldDomain domainType );


//  /**
//   * Returns all DataFields with the given category combo.
//   *
//   * @param categoryCombo the CategoryCombo.
//   * @return all DataFields with the given category combo.
//   */
//  List<DataElement> getDataFieldByCategoryCombo( CategoryCombo categoryCombo );
//
//  /**
//   * Returns all DataFields which are not member of any DataFieldGroups.
//   *
//   * @return all DataFields which are not member of any DataFieldGroups.
//   */
//  List<DataElement> getDataFieldsWithoutGroups();

  /**
   * Returns all DataFields which are not assigned to any FieldSets.
   *
   * @return all DataFields which are not assigned to any FieldSets.
   */
  List<DataElement> getDataElementsWithoutFieldSets();

  /**
   * Returns all DataFields which are assigned to at least one DataSet.
   *
   * @return all DataFields which are assigned to at least one DataSet.
   */
  List<DataElement> getDataElementsWithFieldSets();

  /**
   * Returns all DataFields which have the given aggregation level assigned.
   *
   * @param aggregationLevel the aggregation level.
   * @return all DataFields which have the given aggregation level assigned.
   */
  List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel);

  /**
   * Returns all DataFields which zeroIsSignificant property is true or false.
   *
   * @param zeroIsSignificant whether zero is significant is true for this query.
   * @return a collection of DataFields.
   */
  List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant);
}
