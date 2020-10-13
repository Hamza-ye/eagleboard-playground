package com.mass3d.user.hibernate;

import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.dataset.DataSet;
import com.mass3d.security.acl.AclService;
import com.mass3d.todotask.TodoTask;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserAuthorityGroupStore;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository( "com.mass3d.user.UserAuthorityGroupStore" )
public class HibernateUserAuthorityGroupStore
    extends HibernateIdentifiableObjectStore<UserAuthorityGroup>
    implements UserAuthorityGroupStore {

  @Autowired
  public HibernateUserAuthorityGroupStore( SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService ) {
    super( sessionFactory, jdbcTemplate, deletedObjectService, UserAuthorityGroup.class,
        currentUserService, aclService, false );
  }

  @Override
  public int countFieldSetUserAuthorityGroups(DataSet dataSet) {
    Query<Long> query = getTypedQuery(
        "select count(distinct c) from UserAuthorityGroup c where :dataSet in elements(c.fieldSets)");
    query.setParameter("fieldSet", dataSet);

    return query.getSingleResult().intValue();
  }

  @Override
  public int countTodoTaskUserAuthorityGroups(TodoTask todoTask) {
    Query<Long> query = getTypedQuery(
        "select count(distinct c) from UserAuthorityGroup c where :todoTask in elements(c.todoTasks)");
    query.setParameter("todoTask", todoTask);

    return query.getSingleResult().intValue();
  }
}
