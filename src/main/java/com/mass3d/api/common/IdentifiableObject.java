package com.mass3d.api.common;

import java.io.Serializable;
import java.util.Date;


public interface IdentifiableObject
    extends Comparable<IdentifiableObject>, Serializable {

  long getId();

  String getUid();

  String getCode();

  String getName();

  String getDisplayName();

  Date getCreated();

  Date getLastUpdated();

  String getPublicAccess();

  boolean getExternalAccess();

//  User getUser();
}
