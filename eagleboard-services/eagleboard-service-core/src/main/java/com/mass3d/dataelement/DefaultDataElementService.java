package com.mass3d.dataelement;

import com.mass3d.common.ValueType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.dataelement.DataElementService")
@Transactional
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
  public Long addDataElement(DataElement dataElement) {
    dataElementStore.save(dataElement);

    return dataElement.getId();
  }

  @Override
  public void updateDataElement(DataElement dataElement) {
    dataElementStore.update(dataElement);
  }

  @Override
  public void deleteDataElement(DataElement dataElement) {
    dataElementStore.delete(dataElement);
  }

  @Override
  public DataElement getDataElement(Long id) {
    return dataElementStore.get(id);
  }

  @Override
  public DataElement getDataElement(String uid) {
    return dataElementStore.getByUid(uid);
  }

  @Override
  public DataElement getDataElementByCode(String code) {
    return dataElementStore.getByCode(code);
  }

  @Override
  public List<DataElement> getAllDataElements() {
    return dataElementStore.getAll();
  }

  @Override
  public List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant) {
    return dataElementStore.getDataElementsByZeroIsSignificant(zeroIsSignificant);
  }

  @Override
  public List<DataElement> getAllDataElementsByValueType( ValueType valueType ){
    return dataElementStore.getDataElementsByValueType(valueType);
  }

  @Override
  public List<DataElement> getDataElementsWithoutFieldSets() {
    return dataElementStore.getDataElementsWithoutDataSets();
  }

  @Override
  public List<DataElement> getDataElementsWithFieldSets() {
    return dataElementStore.getDataElementsWithDataSets();
  }

  @Override
  public List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel) {
    return dataElementStore.getDataElementsByAggregationLevel( aggregationLevel );
  }

}
