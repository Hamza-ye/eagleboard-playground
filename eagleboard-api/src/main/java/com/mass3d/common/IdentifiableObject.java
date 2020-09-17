package com.mass3d.common;

import com.mass3d.security.acl.Access;
import com.mass3d.user.User;
import com.mass3d.user.UserAccess;
import com.mass3d.user.UserGroupAccess;
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
