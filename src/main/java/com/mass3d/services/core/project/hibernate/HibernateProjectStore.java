package com.mass3d.services.core.project.hibernate;

import com.mass3d.api.project.Project;
import com.mass3d.api.project.ProjectStore;
import com.mass3d.services.core.common.hibernate.HibernateIdentifiableObjectStore;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateProjectStore
    extends HibernateIdentifiableObjectStore<Project>
    implements ProjectStore {

}
