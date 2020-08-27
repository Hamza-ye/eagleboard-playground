package com.mass3d.api.common;

public interface NameableObject
    extends IdentifiableObject {

  String getShortName();

  String getDisplayShortName();

  String getDescription();

  String getDisplayDescription();
}
