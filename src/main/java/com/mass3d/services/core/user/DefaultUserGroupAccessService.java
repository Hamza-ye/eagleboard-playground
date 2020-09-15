package com.mass3d.services.core.user;

import com.mass3d.api.common.GenericStore;
import com.mass3d.api.user.UserGroupAccess;
import com.mass3d.api.user.UserGroupAccessService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultUserGroupAccessService implements UserGroupAccessService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private GenericStore<UserGroupAccess> userGroupAccessStore;

  public void setUserGroupAccessStore(GenericStore<UserGroupAccess> userGroupAccessStore) {
    this.userGroupAccessStore = userGroupAccessStore;
  }

  // -------------------------------------------------------------------------
  // UserGroupAccess
  // -------------------------------------------------------------------------

  @Override
  public void addUserGroupAccess(UserGroupAccess userGroupAccess) {
    userGroupAccessStore.save(userGroupAccess);
  }

  @Override
  public void updateUserGroupAccess(UserGroupAccess userGroupAccess) {
    userGroupAccessStore.update(userGroupAccess);
  }

  @Override
  public void deleteUserGroupAccess(UserGroupAccess userGroupAccess) {
    userGroupAccessStore.delete(userGroupAccess);
  }

  @Override
  public List<UserGroupAccess> getAllUserGroupAccesses() {
    return userGroupAccessStore.getAll();
  }
}
