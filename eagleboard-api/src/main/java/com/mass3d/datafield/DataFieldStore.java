package com.mass3d.datafield;

import com.mass3d.common.GenericDimensionalObjectStore;
import com.mass3d.common.ValueType;
import java.util.List;

public interface DataFieldStore
    extends GenericDimensionalObjectStore<DataField> {

  String ID = DataFieldStore.class.getName();

  // -------------------------------------------------------------------------
  // DataField
  // -------------------------------------------------------------------------

//  /**
//   * Returns all DataFields with the given category combo.
//   *
//   * @param categoryCombo the CategoryCombo.
//   * @return all DataFields with the given category combo.
//   */
//  List<DataField> getDataFieldByCategoryCombo( CategoryCombo categoryCombo );

  /**
   * Returns all DataField which zeroIsSignificant property is true or false
   *
   * @param zeroIsSignificant is zeroIsSignificant property
   * @return a collection of all DataField
   */
  List<DataField> getDataFieldsByZeroIsSignificant(boolean zeroIsSignificant);

//  /**
//   * Returns all DataFields of the given domain type.
//   *
//   * @param domainType the domain type.
//   * @return all DataFields of the given domain type.
//   */
//  List<DataField> getDataFieldsByDomainType( DataFieldDomain domainType );

  /**
   * Returns all DataFields of the given value type.
   *
   * @param valueType the value type.
   * @return all DataFields of the given value type.
   */
  List<DataField> getDataFieldsByValueType(ValueType valueType);

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
}
