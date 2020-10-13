package com.mass3d.interpretation.hibernate;

import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.interpretation.Interpretation;
import com.mass3d.interpretation.InterpretationStore;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository( "com.mass3d.interpretation.InterpretationStore" )
public class HibernateInterpretationStore
    extends HibernateIdentifiableObjectStore<Interpretation> implements InterpretationStore {

  @Autowired
  public HibernateInterpretationStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, Interpretation.class,
        currentUserService, aclService, false);
  }

  @SuppressWarnings("unchecked")
  public List<Interpretation> getInterpretations(User user) {
    String hql = "select distinct i from Interpretation i left join i.comments c " +
        "where i.user = :user or c.user = :user order by i.lastUpdated desc";

    Query query = getQuery(hql);
    query.setParameter("user", user);

    return query.list();
  }

  @SuppressWarnings("unchecked")
  public List<Interpretation> getInterpretations(User user, int first, int max) {
    String hql = "select distinct i from Interpretation i left join i.comments c " +
        "where i.user = :user or c.user = :user order by i.lastUpdated desc";

    Query query = getQuery(hql);
    query.setParameter("user", user);
    query.setMaxResults(first);
    query.setMaxResults(max);

    return query.list();
  }

  // Todo Eagle commented out
//    @Override
//    public int countMapInterpretations( Map map )
//    {
//        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where c.map=:map" );
//        query.setParameter( "map", map );
//
//        return ((Long) query.uniqueResult()).intValue();
//    }
//
//    @Override
//    public int countChartInterpretations( Chart chart )
//    {
//        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where c.chart=:chart" );
//        query.setParameter( "chart", chart );
//
//        return ((Long) query.uniqueResult()).intValue();
//    }
//
//    @Override
//    public int countReportTableInterpretations( ReportTable reportTable )
//    {
//        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where c.reportTable=:reportTable" );
//        query.setParameter( "reportTable", reportTable );
//
//        return ((Long) query.uniqueResult()).intValue();
//    }
//
//    @Override
//    public Interpretation getByChartId( int id )
//    {
//        String hql = "from Interpretation i where i.chart.id = " + id;
//
//        Query query = getSession().createQuery( hql );
//
//        return (Interpretation) query.uniqueResult();
//    }
}
