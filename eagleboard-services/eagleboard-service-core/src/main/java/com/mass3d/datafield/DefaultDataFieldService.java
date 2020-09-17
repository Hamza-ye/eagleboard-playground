package com.mass3d.datafield;

import com.mass3d.common.ValueType;
import com.mass3d.datafield.DataField;
import com.mass3d.datafield.DataFieldService;
import com.mass3d.datafield.DataFieldStore;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultDataFieldService
    implements DataFieldService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private DataFieldStore dataFieldStore;

  @Autowired
  public void setDataFieldStore(DataFieldStore dataFieldStore) {
    this.dataFieldStore = dataFieldStore;
  }

  // -------------------------------------------------------------------------
  // DataField
  // -------------------------------------------------------------------------

  @Override
  public Long addDataField(DataField dataField) {
    dataFieldStore.save(dataField);

    return dataField.getId();
  }

  @Override
  public void updateDataField(DataField dataField) {
    dataFieldStore.update(dataField);
  }

  @Override
  public void deleteDataField(DataField dataField) {
    dataFieldStore.delete(dataField);
  }

  @Override
  public DataField getDataField(Long id) {
    return dataFieldStore.get(id);
  }

  @Override
  public DataField getDataField(String uid) {
    return dataFieldStore.getByUid(uid);
  }

  @Override
  public DataField getDataFieldByCode(String code) {
    return dataFieldStore.getByCode(code);
  }

  @Override
  public List<DataField> getAllDataFields() {
    return dataFieldStore.getAll();
  }

  @Override
  public List<DataField> getDataFieldsByZeroIsSignificant(boolean zeroIsSignificant) {
    return dataFieldStore.getDataFieldsByZeroIsSignificant(zeroIsSignificant);
  }

  @Override
  public List<DataField> getAllDataFieldsByValueType( ValueType valueType ){
    return dataFieldStore.getDataFieldsByValueType(valueType);
  }

  @Override
  public List<DataField> getDataFieldsWithoutFieldSets() {
    return dataFieldStore.getDataFieldsWithoutFieldSets();
  }

  @Override
  public List<DataField> getDataFieldsWithFieldSets() {
    return dataFieldStore.getDataFieldsWithFieldSets();
  }

  @Override
  public List<DataField> getDataFieldsByAggregationLevel(int aggregationLevel) {
    return dataFieldStore.getDataFieldsByAggregationLevel( aggregationLevel );
  }

}
