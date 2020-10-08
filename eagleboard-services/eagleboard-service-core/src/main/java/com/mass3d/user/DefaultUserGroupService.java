package com.mass3d.user;

import com.mass3d.common.IdentifiableObjectStore;
import com.mass3d.security.acl.AclService;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.user.UserGroupService")
@Transactional
public class DefaultUserGroupService
    implements UserGroupService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private IdentifiableObjectStore<UserGroup> userGroupStore;
  private CurrentUserService currentUserService;
  private AclService aclService;
//  private HibernateCacheManager cacheManager;

  @Autowired
  public void setUserGroupStore(IdentifiableObjectStore<UserGroup> userGroupStore) {
    this.userGroupStore = userGroupStore;
  }

  @Autowired
  public void setCurrentUserService(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  @Autowired
  public void setAclService(AclService aclService) {
    this.aclService = aclService;
  }

//  public void setCacheManager(HibernateCacheManager cacheManager) {
//    this.cacheManager = cacheManager;
//  }

  // -------------------------------------------------------------------------
  // UserGroup
  // -------------------------------------------------------------------------

  @Override
  public Long addUserGroup(UserGroup userGroup) {
    userGroupStore.save(userGroup);
    return userGroup.getId();
  }

  @Override
  public void deleteUserGroup(UserGroup userGroup) {
    userGroupStore.delete(userGroup);
  }

  @Override
  public void updateUserGroup(UserGroup userGroup) {
    userGroupStore.update(userGroup);

    // Clear query cache due to sharing and user group membership

//    cacheManager.clearQueryCache();
  }

  @Override
  public List<UserGroup> getAllUserGroups() {
    return userGroupStore.getAll();
  }

  @Override
  public UserGroup getUserGroup(Long userGroupId) {
    return userGroupStore.get(userGroupId);
  }

  @Override
  public UserGroup getUserGroup(String uid) {
    return userGroupStore.getByUid(uid);
  }

  @Override
  public boolean canAddOrRemoveMember(String uid) {
    return canAddOrRemoveMember(uid, currentUserService.getCurrentUser());
  }

  @Override
  public boolean canAddOrRemoveMember(String uid, User currentUser) {
    UserGroup userGroup = getUserGroup(uid);

    if (userGroup == null || currentUser == null || currentUser.getUserCredentials() == null) {
      return false;
    }

    boolean canUpdate = aclService.canUpdate(currentUser, userGroup);
    boolean canAddMember = currentUser.getUserCredentials()
        .isAuthorized(UserGroup.AUTH_ADD_MEMBERS_TO_READ_ONLY_USER_GROUPS);

    return canUpdate || canAddMember;
  }

  @Override
  public void addUserToGroups(User user, Collection<String> uids) {
    addUserToGroups(user, uids, currentUserService.getCurrentUser());
  }

  @Override
  public void addUserToGroups(User user, Collection<String> uids, User currentUser) {
    for (String uid : uids) {
      if (canAddOrRemoveMember(uid, currentUser)) {
        UserGroup userGroup = getUserGroup(uid);
        userGroup.addUser(user);
        userGroupStore.updateNoAcl(userGroup);
      }
    }
  }

  @Override
  public void removeUserFromGroups(User user, Collection<String> uids) {
    for (String uid : uids) {
      if (canAddOrRemoveMember(uid)) {
        UserGroup userGroup = getUserGroup(uid);
        userGroup.removeUser(user);
        userGroupStore.updateNoAcl(userGroup);
      }
    }
  }

  @Override
  public void updateUserGroups(User user, Collection<String> uids) {
    updateUserGroups(user, uids, currentUserService.getCurrentUser());
  }

  @Override
  public void updateUserGroups(User user, Collection<String> uids, User currentUser) {
    Collection<UserGroup> updates = getUserGroupsByUid(uids);

    for (UserGroup userGroup : new HashSet<>(user.getGroups())) {
      if (!updates.contains(userGroup) && canAddOrRemoveMember(userGroup.getUid(), currentUser)) {
        userGroup.removeUser(user);
      }
    }

    for (UserGroup userGroup : updates) {
      if (canAddOrRemoveMember(userGroup.getUid(), currentUser)) {
        userGroup.addUser(user);
        userGroupStore.updateNoAcl(userGroup);
      }
    }
  }

  public Collection<UserGroup> getUserGroupsByUid(Collection<String> uids) {
    return userGroupStore.getByUid(uids);
  }

  @Override
  public List<UserGroup> getUserGroupByName(String name) {
    return userGroupStore.getAllEqName(name);
  }

  @Override
  public int getUserGroupCount() {
    return userGroupStore.getCount();
  }

  @Override
  public int getUserGroupCountByName(String name) {
    return userGroupStore.getCountLikeName(name);
  }

  @Override
  public List<UserGroup> getUserGroupsBetween(int first, int max) {
    return userGroupStore.getAllOrderedName(first, max);
  }

  @Override
  public List<UserGroup> getUserGroupsBetweenByName(String name, int first, int max) {
    return userGroupStore.getAllLikeName(name, first, max, false);
  }
}