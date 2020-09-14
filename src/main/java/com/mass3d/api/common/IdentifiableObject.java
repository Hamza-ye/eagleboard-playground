package com.mass3d.api.common;

import com.mass3d.api.security.acl.Access;
import com.mass3d.api.user.User;
import com.mass3d.api.user.UserAccess;
import com.mass3d.api.user.UserGroupAccess;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;


public interface IdentifiableObject
    extends Comparable<IdentifiableObject>, Serializable {

  Long getId();

  String getUid();

  String getCode();

  String getName();

  String getDisplayName();

  Date getCreated();

  Date getLastUpdated();

  String getPropertyValue(IdScheme idScheme);

  Set<UserGroupAccess> getUserGroupAccesses();

  Set<UserAccess> getUserAccesses();

  Access getAccess();

  String getPublicAccess();

  boolean getExternalAccess();

  User getUser();
}
