package com.mass3d.user.hibernate;

import com.mass3d.user.User;
import com.mass3d.user.UserSetting;
import com.mass3d.user.UserSettingStore;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserSettingStore
    implements UserSettingStore {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private SessionFactory sessionFactory;

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  // -------------------------------------------------------------------------
  // UserSettingStore implementation
  // -------------------------------------------------------------------------

  @Override
  public void addUserSetting(UserSetting userSetting) {
    Session session = sessionFactory.getCurrentSession();

    session.save(userSetting);
  }

  @Override
  public void updateUserSetting(UserSetting userSetting) {
    Session session = sessionFactory.getCurrentSession();

    session.update(userSetting);
  }

  @Override
  public UserSetting getUserSetting(User user, String name) {
    Session session = sessionFactory.getCurrentSession();

    Query query = session
        .createQuery("from UserSetting us where us.user = :user and us.name = :name");

    query.setParameter("user", user);
    query.setParameter("name", name);
    query.setCacheable(true);

    return (UserSetting) query.uniqueResult();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<UserSetting> getAllUserSettings(User user) {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.createQuery("from UserSetting us where us.user = :user");
    query.setParameter("user", user);

    return query.list();
  }

  @Override
  public void deleteUserSetting(UserSetting userSetting) {
    Session session = sessionFactory.getCurrentSession();

    session.delete(userSetting);
  }

  @Override
  public void removeUserSettings(User user) {
    Session session = sessionFactory.getCurrentSession();

    String hql = "delete from UserSetting us where us.user = :user";

    session.createQuery(hql).setParameter("user", user).executeUpdate();
  }
}
