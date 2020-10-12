package com.mass3d.indicator.hibernate;

import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.indicator.Indicator;
import com.mass3d.indicator.IndicatorStore;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @version $Id: HibernateIndicatorStore.java 3287 2007-05-08 00:26:53Z larshelg $
 */
@Repository( "com.mass3d.indicator.IndicatorStore" )
public class HibernateIndicatorStore
    extends HibernateIdentifiableObjectStore<Indicator>
    implements IndicatorStore {

  @Autowired
  public HibernateIndicatorStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, Indicator.class, currentUserService,
        aclService, true);
  }
  // -------------------------------------------------------------------------
  // Indicator
  // -------------------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public List<Indicator> getIndicatorsWithGroupSets() {
    final String hql = "from Indicator d where size(d.groupSets) > 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Indicator> getIndicatorsWithoutGroups() {
    final String hql = "from Indicator d where size(d.groups) = 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Indicator> getIndicatorsWithDataSets() {
    final String hql = "from Indicator d where size(d.dataSets) > 0";

    return getQuery(hql).setCacheable(true).list();
  }
}
