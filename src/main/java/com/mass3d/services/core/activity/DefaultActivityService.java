package com.mass3d.services.core.activity;

import com.google.common.collect.Lists;
import com.mass3d.api.activity.Activity;
import com.mass3d.api.activity.ActivityService;
import com.mass3d.api.activity.ActivityStore;
import com.mass3d.api.user.CurrentUserService;
import com.mass3d.api.user.User;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultActivityService
    implements ActivityService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private ActivityStore activityStore;
  private CurrentUserService currentUserService;

  public void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
  }

  public void setCurrentUserService(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  // -------------------------------------------------------------------------
  // activity
  // -------------------------------------------------------------------------

  @Override
  public long addActivity(Activity dataField) {
    activityStore.save(dataField);

    return dataField.getId();
  }

  @Override
  public void updateActivity(Activity dataField) {
    activityStore.update(dataField);
  }

  @Override
  public void deleteActivity(Activity dataField) {
    activityStore.delete(dataField);
  }

  @Override
  public Activity getActivity(Long id) {
    return activityStore.get(id);
  }

  @Override
  public Activity getActivity(String uid) {
    return activityStore.getByUid(uid);
  }

  @Override
  public Activity getActivityNoAcl(String uid) {
    return null;
  }

  @Override
  public Activity getActivityByCode(String code) {
    return activityStore.getByCode(code);
  }

  @Override
  public List<Activity> getAllActivitys() {
    return activityStore.getAll();
  }

  @Override
  public List<Activity> getActivitysByUid(Collection<String> uids) {
    return activityStore.getByUid(uids);
  }

  @Override
  public List<Activity> getActivitysWithoutProjects() {
    return activityStore.getActivitysWithoutProjects();
  }

  @Override
  public List<Activity> getActivitysWithProjects() {
    return activityStore.getActivitysWithProjects();
  }

//  @Override
//  public List<activity> getCurrentUserActivitys() {
//    return null;
//  }

  @Override
  public List<Activity> getUserDataRead(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllActivitys() : activityStore.getDataReadAll(user);
  }

  @Override
  public List<Activity> getAllDataRead() {
    User user = currentUserService.getCurrentUser();

    return getUserDataRead(user);
  }

  @Override
  public List<Activity> getAllDataWrite() {
    User user = currentUserService.getCurrentUser();

    return getUserDataWrite(user);
  }

  @Override
  public List<Activity> getUserDataWrite(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllActivitys() : activityStore.getDataWriteAll(user);
  }

//  @Override
//  public List<activity> getCurrentUserActivitys() {
//    User user = currentUserService.getCurrentUser();
//
//    if (user == null) {
//      return Lists.newArrayList();
//    }
//
//    if (user.isSuper()) {
//      return getAllActivitys();
//    } else {
//      return Lists.newArrayList(user.getUserCredentials().getAllActivities());
//    }
//  }

}
