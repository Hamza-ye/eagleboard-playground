package com.mass3d.dataset;

import com.google.common.collect.Lists;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.dataset.DataSetService")
//@Transactional
public class DefaultDataSetService
    implements DataSetService {

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------
  private DataSetStore dataSetStore;
  private CurrentUserService currentUserService;

  @Autowired
  public void setDataSetStore(DataSetStore dataSetStore) {
    this.dataSetStore = dataSetStore;
  }

  @Autowired
  public void setCurrentUserService(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  // -------------------------------------------------------------------------
  // DataSet
  // -------------------------------------------------------------------------

//  @Override
//  public List<DataSet> getFieldSetsByPeriodType(PeriodType periodType) {
//    return dataSetStore.getFieldSetsByPeriodType(periodType);
//  }

  @Override
  @Transactional
  public long addDataSet(DataSet dataSet) {
    dataSetStore.save(dataSet);
    return dataSet.getId();
  }

  @Override
  @Transactional
  public void updateDataSet(DataSet dataSet) {
    dataSetStore.update(dataSet);
  }

  @Override
  @Transactional
  public void deleteDataSet(DataSet dataSet) {
    dataSetStore.delete(dataSet);
  }

  @Override
  @Transactional(readOnly = true)
  public DataSet getDataSet(long id) {
    return dataSetStore.get(id);
  }

  @Override
  @Transactional(readOnly = true)
  public DataSet getDataSet(String uid) {
    return dataSetStore.getByUid(uid);
  }

  @Override
  @Transactional(readOnly = true)
  public DataSet getDataSetNoAcl(String uid) {
    return dataSetStore.getByUidNoAcl(uid);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DataSet> getAllDataSets() {
    return dataSetStore.getAll();
  }


  @Override
  @Transactional(readOnly = true)
  public List<DataSet> getDataSetsByUid(Collection<String> uids) {
    return dataSetStore.getByUid(uids);
  }

//  @Override
//  public List<DataSet> getFieldSetsForMobile(TodoTask source) {
//    return dataSetStore.getFieldSetsForMobile(source);
//  }
//
  @Override
  @Transactional
  public List<DataSet> getDataSetsWithoutTodoTasks() {
    return dataSetStore.getDataSetsWithoutTodoTasks();
  }

  @Override
  @Transactional
  public List<DataSet> getDataSetsWithTodoTasks() {
    return dataSetStore.getDataSetsWithTodoTasks();
  }

  @Override
  @Transactional
  public List<DataSet> getUserDataRead(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllDataSets() : dataSetStore.getDataReadAll(user);
  }

  @Override
  @Transactional
  public List<DataSet> getAllDataRead() {
    User user = currentUserService.getCurrentUser();

    return getUserDataRead(user);
  }

  @Override
  @Transactional
  public List<DataSet> getAllDataWrite() {
    User user = currentUserService.getCurrentUser();

    return getUserDataWrite(user);
  }

  @Override
  @Transactional
  public List<DataSet> getUserDataWrite(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllDataSets() : dataSetStore.getDataWriteAll(user);
  }

//  @Override
//  public List<DataSet> getCurrentUserFieldSets() {
//    User user = currentUserService.getCurrentUser();
//
//    if (user == null) {
//      return Lists.newArrayList();
//    }
//
//    if (user.isSuper()) {
//      return getAllDataSets();
//    } else {
//      return Lists.newArrayList(user.getUserCredentials().getAllDataSets());
//    }
//  }

}
