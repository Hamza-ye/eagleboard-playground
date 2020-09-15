package com.mass3d.services.core.fieldset;

import com.google.common.collect.Lists;
import com.mass3d.api.fieldset.FieldSet;
import com.mass3d.api.fieldset.FieldSetService;
import com.mass3d.api.fieldset.FieldSetStore;
import com.mass3d.api.todotask.TodoTask;
import com.mass3d.api.user.CurrentUserService;
import com.mass3d.api.user.User;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultFieldSetService
    implements FieldSetService {

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------
  private FieldSetStore fieldSetStore;
  private CurrentUserService currentUserService;

  public void setFieldSetStore(FieldSetStore fieldSetStore) {
    this.fieldSetStore = fieldSetStore;
  }

  public void setCurrentUserService(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  // -------------------------------------------------------------------------
  // FieldSet
  // -------------------------------------------------------------------------

//  @Override
//  public List<FieldSet> getFieldSetsByPeriodType(PeriodType periodType) {
//    return fieldSetStore.getFieldSetsByPeriodType(periodType);
//  }

  @Override
  public Long addFieldSet(FieldSet fieldSet) {
    fieldSetStore.save(fieldSet);
    return fieldSet.getId();
  }

  @Override
  public void updateFieldSet(FieldSet fieldSet) {
    fieldSetStore.update(fieldSet);
  }

  @Override
  public void deleteFieldSet(FieldSet fieldSet) {
    fieldSetStore.delete(fieldSet);
  }

  @Override
  public FieldSet getFieldSet(Long id) {
    return fieldSetStore.get(id);
  }

  @Override
  public FieldSet getFieldSet(String uid) {
    return fieldSetStore.getByUid(uid);
  }

  @Override
  public FieldSet getFieldSetNoAcl(String uid) {
    return fieldSetStore.getByUidNoAcl(uid);
  }

  @Override
  public List<FieldSet> getAllFieldSets() {
    return fieldSetStore.getAll();
  }


  @Override
  public List<FieldSet> getFieldSetsByUid(Collection<String> uids) {
    return fieldSetStore.getByUid(uids);
  }

//  @Override
//  public List<FieldSet> getFieldSetsForMobile(TodoTask source) {
//    return fieldSetStore.getFieldSetsForMobile(source);
//  }
//
  @Override
  public List<FieldSet> getFieldSetsWithoutTodoTasks() {
    return fieldSetStore.getFieldSetsWithoutTodoTasks();
  }

  @Override
  public List<FieldSet> getFieldSetsWithTodoTasks() {
    return fieldSetStore.getFieldSetsWithTodoTasks();
  }

  @Override
  public List<FieldSet> getUserDataRead(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllFieldSets() : fieldSetStore.getDataReadAll(user);
  }

  @Override
  public List<FieldSet> getAllDataRead() {
    User user = currentUserService.getCurrentUser();

    return getUserDataRead(user);
  }

  @Override
  public List<FieldSet> getAllDataWrite() {
    User user = currentUserService.getCurrentUser();

    return getUserDataWrite(user);
  }

  @Override
  public List<FieldSet> getUserDataWrite(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllFieldSets() : fieldSetStore.getDataWriteAll(user);
  }

//  @Override
//  public List<FieldSet> getCurrentUserFieldSets() {
//    User user = currentUserService.getCurrentUser();
//
//    if (user == null) {
//      return Lists.newArrayList();
//    }
//
//    if (user.isSuper()) {
//      return getAllFieldSets();
//    } else {
//      return Lists.newArrayList(user.getUserCredentials().getAllFieldSets());
//    }
//  }

}
