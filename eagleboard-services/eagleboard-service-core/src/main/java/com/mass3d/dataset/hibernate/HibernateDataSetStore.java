package com.mass3d.dataset.hibernate;

import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.dataset.DataSet;
import com.mass3d.dataset.DataSetStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("com.mass3d.dataset.DataSetStore")
public class HibernateDataSetStore
    extends HibernateIdentifiableObjectStore<DataSet>
    implements DataSetStore {

  public HibernateDataSetStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, DataSet.class, currentUserService,
        aclService, false);
  }
  // -------------------------------------------------------------------------
  // DataSet
  // -------------------------------------------------------------------------

  @Override
  public void save(DataSet dataSet) {
//    PeriodType periodType = periodService.reloadPeriodType(dataSet.getPeriodType());
//
//    dataSet.setPeriodType(periodType);

    super.save(dataSet);
  }

  @Override
  public void update(DataSet dataSet) {
//    PeriodType periodType = periodService.reloadPeriodType(dataSet.getPeriodType());
//
//    dataSet.setPeriodType(periodType);

    super.update(dataSet);
  }
//
//  @Override
//  public List<DataSet> getFieldSetsByPeriodType(PeriodType periodType) {
//    PeriodType refreshedPeriodType = periodService.reloadPeriodType(periodType);
//
//    CriteriaBuilder builder = getCriteriaBuilder();
//
//    JpaQueryParameters<DataSet> parameters = newJpaParameters()
//        .addPredicate(root -> builder.equal(root.get("periodType"), refreshedPeriodType));
//
//    return getList(builder, parameters);
//  }

  // Todo Eagle modified getFieldSetsForMobile()
//  @Override
//  @SuppressWarnings("unchecked")
//  public List<DataSet> getFieldSetsForMobile(TodoTask source) {
////        String hql = "from DataSet d where :source in elements(d.sources) and d.mobile = true";
////
////        return getQuery( hql ).setEntity( "source", source ).list();
//    return new ArrayList<>();
//  }

  @Override
  public List<DataSet> getDataSetsWithoutTodoTasks() {
    String hql = "from DataSet d where size(d.sources) = 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  public List<DataSet> getDataSetsWithTodoTasks() {
    String hql = "from DataSet d where size(d.sources) > 0";

    return getQuery(hql).setCacheable(true).list();
  }

  // Todo Eagle modified getDataSetsByDataEntryForm

//    @Override
//    @SuppressWarnings( "unchecked" )
//    public List<DataSet> getDataSetsByDataEntryForm( DataEntryForm dataEntryForm )
//    {
//        if ( dataEntryForm == null )
//        {
//            return Lists.newArrayList();
//        }
//
//        final String hql = "from DataSet d where d.dataEntryForm = :dataEntryForm";
//
//        return getQuery( hql ).setEntity( "dataEntryForm", dataEntryForm ).list();
//    }
}
