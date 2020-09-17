package com.mass3d.fieldset.hibernate;

import com.mass3d.fieldset.FieldSet;
import com.mass3d.fieldset.FieldSetStore;
import com.mass3d.todotask.TodoTask;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateFieldSetStore
    extends HibernateIdentifiableObjectStore<FieldSet>
    implements FieldSetStore {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  // -------------------------------------------------------------------------
  // FieldSet
  // -------------------------------------------------------------------------

  @Override
  public Class<FieldSet> getClazz() {
    return FieldSet.class;
  }

  @Override
  public void save(FieldSet fieldSet) {
//    PeriodType periodType = periodService.reloadPeriodType(fieldSet.getPeriodType());
//
//    fieldSet.setPeriodType(periodType);

    super.save(fieldSet);
  }

  @Override
  public void update(FieldSet fieldSet) {
//    PeriodType periodType = periodService.reloadPeriodType(fieldSet.getPeriodType());
//
//    fieldSet.setPeriodType(periodType);

    super.update(fieldSet);
  }
//
//  @Override
//  public List<FieldSet> getFieldSetsByPeriodType(PeriodType periodType) {
//    PeriodType refreshedPeriodType = periodService.reloadPeriodType(periodType);
//
//    CriteriaBuilder builder = getCriteriaBuilder();
//
//    JpaQueryParameters<FieldSet> parameters = newJpaParameters()
//        .addPredicate(root -> builder.equal(root.get("periodType"), refreshedPeriodType));
//
//    return getList(builder, parameters);
//  }

  // Todo Eagle modified getFieldSetsForMobile()
//  @Override
//  @SuppressWarnings("unchecked")
//  public List<FieldSet> getFieldSetsForMobile(TodoTask source) {
////        String hql = "from FieldSet d where :source in elements(d.sources) and d.mobile = true";
////
////        return getQuery( hql ).setEntity( "source", source ).list();
//    return new ArrayList<>();
//  }

  @Override
  public List<FieldSet> getFieldSetsWithoutTodoTasks() {
    String hql = "from FieldSet d where size(d.sources) = 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  public List<FieldSet> getFieldSetsWithTodoTasks() {
    String hql = "from FieldSet d where size(d.sources) > 0";

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