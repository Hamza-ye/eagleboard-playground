package com.mass3d.user.hibernate;

import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserCredentialsStore;
import com.mass3d.hibernate.HibernateGenericStore;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserCredentialsStore
    extends HibernateGenericStore<UserCredentials>
    implements UserCredentialsStore {

  @Override
  public Class<UserCredentials> getClazz() {
    return UserCredentials.class;
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
