package com.mass3d.services.core.user;

import com.mass3d.api.common.GenericStore;
import com.mass3d.api.user.UserAccess;
import com.mass3d.api.user.UserAccessService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultUserAccessService implements UserAccessService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private GenericStore<UserAccess> userAccessStore;

  public void setUserAccessStore(GenericStore<UserAccess> userAccessStore) {
    this.userAccessStore = userAccessStore;
  }

  // -------------------------------------------------------------------------
  // UserGroupAccess
  // -------------------------------------------------------------------------

  @Override
  public void addUserAccess(UserAccess userAccess) {
    userAccessStore.save(userAccess);
  }

  @Override
  public void updateUserAccess(UserAccess userAccess) {
    userAccessStore.update(userAccess);
  }

  @Override
  public void deleteUserAccess(UserAccess userAccess) {
    userAccessStore.delete(userAccess);
  }

  @Override
  public List<UserAccess> getAllUserAccesses() {
    return userAccessStore.getAll();
  }
}
