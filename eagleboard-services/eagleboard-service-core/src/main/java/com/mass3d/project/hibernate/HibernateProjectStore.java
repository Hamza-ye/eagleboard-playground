package com.mass3d.project.hibernate;

import com.mass3d.project.Project;
import com.mass3d.project.ProjectStore;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateProjectStore
    extends HibernateIdentifiableObjectStore<Project>
    implements ProjectStore {

  @Override
  public Class<Project> getClazz() {
    return Project.class;
  }
}
