package com.mass3d.user.hibernate;

import com.mass3d.user.CurrentUserStore;
import com.mass3d.user.User;
import com.mass3d.user.UserCredentials;
import com.mass3d.query.JpaQueryUtils;
import javax.persistence.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateCurrentUserStore
    implements CurrentUserStore {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public User getUser(long id) {
    return (User) sessionFactory.getCurrentSession().get(User.class, id);
  }

  @Override
  public UserCredentials getUserCredentialsByUsername(String username) {
    String hql = "from UserCredentials uc where uc.username = :username";

    Query query = sessionFactory.getCurrentSession().createQuery(hql);
    query.setParameter("username", username);
    query.setHint(JpaQueryUtils.HIBERNATE_CACHEABLE_HINT, true);

    return (UserCredentials) query.getResultList().stream().findFirst().orElse(null);
  }
}