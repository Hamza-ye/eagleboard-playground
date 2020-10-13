package com.mass3d.dataelement.hibernate;

import com.mass3d.common.ValueType;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.dataelement.DataElement;
import com.mass3d.dataelement.DataElementStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("com.mass3d.dataelement.DataElementStore")
public class HibernateDataElementStore
    extends HibernateIdentifiableObjectStore<DataElement>
    implements DataElementStore {

  public HibernateDataElementStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, DataElement.class, currentUserService,
        aclService, false);
  }

  // -------------------------------------------------------------------------
  // DataElement
  // -------------------------------------------------------------------------
  @Override
  @SuppressWarnings("unchecked")
  public List<DataElement> getDataElementsByZeroIsSignificant(boolean zeroIsSignificant) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getList(builder, newJpaParameters()
        .addPredicate(root -> builder.equal(root.get("zeroIsSignificant"), zeroIsSignificant))
        .addPredicate(root -> root.get("valueType").in(ValueType.NUMERIC_TYPES)));
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DataElement> getDataElementsWithoutDataSets() {
    String hql = "from DataElement d where size(d.dataSetElements) = 0";

    return getQuery(hql).setCacheable(true).list();
//        return getQuery( hql ).setParameter( "domainType", DataElementDomain.AGGREGATE ).setCacheable( true ).list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DataElement> getDataElementsWithDataSets() {
    String hql = "from DataElement d where size(d.dataSetElements) > 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  public List<DataElement> getDataElementsByAggregationLevel(int aggregationLevel) {
    String hql = "from DataElement df join df.aggregationLevels al where al = :aggregationLevel";

    return getQuery(hql).setParameter("aggregationLevel", aggregationLevel).list();
  }

  @Override
  public List<DataElement> getDataElementsByValueType(ValueType valueType) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getList(builder,
        newJpaParameters().addPredicate(root -> builder.equal(root.get("valueType"), valueType)));
  }

}
