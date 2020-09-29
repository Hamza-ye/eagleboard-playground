package com.mass3d.datafield.hibernate;

import com.mass3d.common.ValueType;
import com.mass3d.datafield.DataField;
import com.mass3d.datafield.DataFieldStore;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateDataFieldStore
    extends HibernateIdentifiableObjectStore<DataField>
    implements DataFieldStore {

  public HibernateDataFieldStore( SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService, AclService aclService )
  {
    super( sessionFactory, jdbcTemplate, deletedObjectService, DataField.class, currentUserService, aclService, false );
  }
  // -------------------------------------------------------------------------
  // DataElement
  // -------------------------------------------------------------------------
  @Override
  @SuppressWarnings("unchecked")
  public List<DataField> getDataFieldsByZeroIsSignificant(boolean zeroIsSignificant) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getList( builder, newJpaParameters()
        .addPredicate( root -> builder.equal( root.get( "zeroIsSignificant" ), zeroIsSignificant ) )
        .addPredicate( root -> root.get( "valueType" ).in( ValueType.NUMERIC_TYPES ) ));
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DataField> getDataFieldsWithoutFieldSets() {
    String hql = "from DataField d where size(d.fieldSetFields) = 0";

    return getQuery(hql).setCacheable(true).list();
//        return getQuery( hql ).setParameter( "domainType", DataElementDomain.AGGREGATE ).setCacheable( true ).list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DataField> getDataFieldsWithFieldSets() {
    String hql = "from DataField d where size(d.fieldSetFields) > 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  public List<DataField> getDataFieldsByAggregationLevel(int aggregationLevel) {
    String hql = "from DataField df join df.aggregationLevels al where al = :aggregationLevel";

    return getQuery( hql ).setParameter( "aggregationLevel", aggregationLevel ).list();
  }

  @Override
  public List<DataField> getDataFieldsByValueType(ValueType valueType) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getList( builder, newJpaParameters().addPredicate( root -> builder.equal( root.get( "valueType" ), valueType ) ) );
  }

}
