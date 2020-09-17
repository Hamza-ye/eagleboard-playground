package com.mass3d.user.hibernate;

import com.mass3d.fieldset.FieldSet;
import com.mass3d.todotask.TodoTask;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserAuthorityGroupStore;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserAuthorityGroupStore
    extends HibernateIdentifiableObjectStore<UserAuthorityGroup>
    implements UserAuthorityGroupStore {

  @Override
  public Class<UserAuthorityGroup> getClazz() {
    return UserAuthorityGroup.class;
  }

  @Override
  public int countFieldSetUserAuthorityGroups(FieldSet fieldSet) {
    Query<Long> query = getTypedQuery(
        "select count(distinct c) from UserAuthorityGroup c where :fieldSet in elements(c.fieldSets)");
    query.setParameter("fieldSet", fieldSet);

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
