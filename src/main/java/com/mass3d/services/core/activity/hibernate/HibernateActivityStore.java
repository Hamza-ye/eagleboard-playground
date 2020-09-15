package com.mass3d.services.core.activity.hibernate;

import com.mass3d.api.activity.Activity;
import com.mass3d.api.activity.ActivityStore;
import com.mass3d.services.core.common.hibernate.HibernateIdentifiableObjectStore;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateActivityStore
    extends HibernateIdentifiableObjectStore<Activity>
    implements ActivityStore {
  // -------------------------------------------------------------------------
  // activity
  // -------------------------------------------------------------------------

  @Override
  public List<Activity> getActivitysWithoutProjects() {
    String hql = "from activity d where size(d.projects) = 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  public List<Activity> getActivitysWithProjects() {
    String hql = "from activity d where size(d.projects) > 0";

    return getQuery(hql).setCacheable(true).list();
  }
}
