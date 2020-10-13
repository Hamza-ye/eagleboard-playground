package com.mass3d.dataelement;

import com.mass3d.common.ValueType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.dataelement.DataElementService")
//@Transactional
public class DefaultDataElementService
    implements DataElementService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private DataElementStore dataElementStore;

  @Autowired
  public void setDataElementStore(DataElementStore dataElementStore) {
    this.dataElementStore = dataElementStore;
  }

  // -------------------------------------------------------------------------
  // DataElement
  // -------------------------------------------------------------------------

  @Override
  @Transactional
  public long addDataElement(DataElement dataElement) {
    dataElementStore.save(dataElement);

    return dataElement.getId();
  }

  @Override
  @Transactional
  public void updateDataElement(DataElement dataElement) {
    dataElementStore.update(dataElement);
  }

  @Override
  @Transactional
  public void deleteDataElement(DataElement dataElement) {
    dataElementStore.delete(dataElement);
  }

  @Override
  @Transactional
  public DataElement getDataElement(long id) {
    return dataElementStore.get(id);
  }

  @Override
  @Transactional(readOnly = true)
  public DataElement getDataElement(String uid) {
    return dataElementStore.getByUid(uid);
  }

  @Override
  @Transactional(readOnly = true)
  public DataElement getDataElementByCode(String code) {
    return dataElementStore.getByCode(code);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataElement> getAllDataElements() {
    return dataElementStore.getAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant) {
    return dataElementStore.getDataElementsByZeroIsSignificant(zeroIsSignificant);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataElement> getAllDataElementsByValueType( ValueType valueType ){
    return dataElementStore.getDataElementsByValueType(valueType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataElement> getDataElementsWithoutDataSets() {
    return dataElementStore.getDataElementsWithoutDataSets();
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataElement> getDataElementsWithDataSets() {
    return dataElementStore.getDataElementsWithDataSets();
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel) {
    return dataElementStore.getDataElementsByAggregationLevel( aggregationLevel );
  }

}
