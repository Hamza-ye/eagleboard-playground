package com.mass3d.api.project;

import com.mass3d.api.common.IdentifiableObjectStore;

public interface ProjectStore
    extends IdentifiableObjectStore<Project> {

  String ID = ProjectStore.class.getName();

}
