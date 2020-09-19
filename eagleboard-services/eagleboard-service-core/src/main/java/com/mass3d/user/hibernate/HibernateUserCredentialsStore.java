package com.mass3d.user.hibernate;

import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.todotask.TodoTask;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserCredentialsStore;
import com.mass3d.hibernate.HibernateGenericStore;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserCredentialsStore
    extends HibernateGenericStore<UserCredentials>
    implements UserCredentialsStore {

  public HibernateUserCredentialsStore( SessionFactory sessionFactory, JdbcTemplate jdbcTemplate)  {
    super( sessionFactory, jdbcTemplate, UserCredentials.class, false );
  }

  @Override
  public UserCredentials getUserCredentialsByUsername(String username) {
    Query query = getQuery("from UserCredentials uc where uc.username = :username");
    query.setParameter("username", username);
    return (UserCredentials) query.uniqueResult();
  }

  @Override
  public UserCredentials getUserCredentialsByOpenId(String openId) {
    Query query = getQuery("from UserCredentials uc where uc.openId = :openId");
    query.setParameter("openId", openId);
    return (UserCredentials) query.uniqueResult();
  }

  public UserCredentials getUserCredentialsByLdapId(String ldapId) {
    Query query = getQuery("from UserCredentials uc where uc.ldapId = :ldapId");
    query.setParameter("ldapId", ldapId);
    return (UserCredentials) query.uniqueResult();
  }
}
