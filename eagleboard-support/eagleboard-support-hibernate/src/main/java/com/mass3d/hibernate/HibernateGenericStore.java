package com.mass3d.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mass3d.common.GenericStore;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

@Transactional
public class HibernateGenericStore<T>
    implements GenericStore<T>
{
  private static final Log log = LogFactory.getLog( HibernateGenericStore.class );

  protected SessionFactory sessionFactory;
  protected JdbcTemplate jdbcTemplate;
  protected Class<T> clazz;

  public HibernateGenericStore( SessionFactory sessionFactory, JdbcTemplate jdbcTemplate, Class<T> clazz, boolean cacheable )
  {
    checkNotNull( sessionFactory );
    checkNotNull( jdbcTemplate );
    checkNotNull( clazz );

    this.sessionFactory = sessionFactory;
    this.jdbcTemplate = jdbcTemplate;
    this.clazz = clazz;
    this.cacheable = cacheable;
  }

  protected boolean isCacheable()
  {
    return cacheable;
  }

  /**
   * Could be overridden programmatically.
   */
  @Override
  public Class<T> getClazz()
  {
    return clazz;
  }

  /**
   * Could be injected through container.
   */
  @Required
  public void setClazz( Class<T> clazz )
  {
    this.clazz = clazz;
  }

  protected boolean cacheable = false;

  /**
   * Could be injected through container.
   */
  public void setCacheable( boolean cacheable )
  {
    this.cacheable = cacheable;
  }

  // -------------------------------------------------------------------------
  // Convenience methods
  // -------------------------------------------------------------------------

  /**
   * Returns the current session.
   *
   * @return the current session.
   */
  protected final Session getSession()
  {
    return sessionFactory.getCurrentSession();
  }

  /**
   * Creates a Query for given HQL query string. Return type is casted
   * to generic type T of the Store class.
   * @param hql the HQL query.
   * @return a Query instance with return type is the object type T of the store class
   */
  @SuppressWarnings("unchecked")
  protected final Query<T> getQuery( String hql )
  {
    return getSession().createQuery( hql )
        .setCacheable( cacheable ).setHint( QueryHints.CACHEABLE, cacheable );
  }

  /**
   * Creates a Query for given HQL query string. Must specify the return
   * type of the Query variable.
   * @param hql the HQL query.
   * @return a Query instance with return type specified in the Query<Y>
   */
  @SuppressWarnings("unchecked")
  protected final <V> Query<V> getTypedQuery( String hql )
  {
    return getSession()
        .createQuery( hql )
        .setCacheable( cacheable ).setHint( QueryHints.CACHEABLE, cacheable );
  }

  /**
   * Creates a Criteria for the implementation Class type.
   * <p>
   * Please note that sharing is not considered.
   *
   * @return a Criteria instance.
   */
  @Deprecated
  public final Criteria getCriteria()
  {
    DetachedCriteria criteria = DetachedCriteria.forClass( getClazz() );

    preProcessDetachedCriteria( criteria );

    return getExecutableCriteria( criteria );
  }

  /**
   * Override to add additional restrictions to criteria before
   * it is invoked.
   */
  protected void preProcessDetachedCriteria( DetachedCriteria detachedCriteria )
  {
  }

  public final Criteria getExecutableCriteria( DetachedCriteria detachedCriteria )
  {
    return detachedCriteria.getExecutableCriteria( getSession() )
        .setCacheable( cacheable );
  }

  public CriteriaBuilder getCriteriaBuilder()
  {
    return sessionFactory.getCriteriaBuilder();
  }

  //------------------------------------------------------------------------------------------
  // JPA Methods
  //------------------------------------------------------------------------------------------

  /**
   * Get executable Typed Query from Criteria Query.
   * Apply cache if needed.
   * @param criteriaQuery
   * @return  executable TypedQuery
   */
  public final TypedQuery<T> getExecutableTypedQuery( CriteriaQuery<T> criteriaQuery )
  {
    return getSession()
        .createQuery( criteriaQuery )
        .setCacheable( cacheable ).setHint( QueryHints.CACHEABLE, cacheable );
  }

  /**
   * Method for adding additional Predicates into where clause
   * @param builder
   * @param predicates
   */
  protected void preProcessPredicates( CriteriaBuilder builder, List<Function<Root<T>, Predicate>> predicates )
  {
  }

  /**
   * Get single result from executable typedQuery
   * @param typedQuery TypedQuery
   * @return single object
   */
  protected <V> V getSingleResult( TypedQuery<V> typedQuery )
  {
    List<V> list = typedQuery.getResultList();

    if ( list != null && list.size() > 1 )
    {
      throw new NonUniqueResultException( "More than one entity found for query" );
    }

    return list != null && !list.isEmpty() ? list.get( 0 ) : null;
  }

  /**
   * Get List objects returned by executable TypedQuery
   * @param typedQuery
   * @return list result
   */
  protected final List<T> getList( TypedQuery<T> typedQuery )
  {
    return typedQuery.getResultList();
  }

  /**
   * Get List objects return by querying given JpaQueryParameters
   * @param builder
   * @param parameters JpaQueryParameters
   * @return list objects
   */
  protected final List<T> getList( CriteriaBuilder builder, JpaQueryParameters<T> parameters )
  {
    return getTypedQuery( builder, parameters ).getResultList();
  }

  /**
   * Get executable TypedQuery from JpaQueryParameter.
   * @param builder
   * @param parameters
   * @return executable TypedQuery
   */
  protected final TypedQuery<T> getTypedQuery( CriteriaBuilder builder, JpaQueryParameters<T> parameters )
  {
    List<Function<Root<T>, Predicate>> predicateProviders = parameters.getPredicates();
    List<Function<Root<T>, Order>> orderProviders = parameters.getOrders();
    preProcessPredicates( builder, predicateProviders );

    CriteriaQuery<T> query = builder.createQuery( getClazz() );
    Root<T> root = query.from( getClazz() );
    query.select( root );

    if ( !predicateProviders.isEmpty() )
    {
      List<Predicate> predicates = predicateProviders.stream().map( t -> t.apply( root ) ).collect( Collectors.toList() );
      query.where( predicates.toArray( new Predicate[0] ) );
    }

    if ( !orderProviders.isEmpty() )
    {
      List<Order> orders = orderProviders.stream().map( o -> o.apply( root ) ).collect( Collectors.toList() );
      query.orderBy( orders );
    }

    TypedQuery<T> typedQuery = getExecutableTypedQuery( query );

    if ( parameters.hasFirstResult() )
    {
      typedQuery.setFirstResult( parameters.getFirstResult() );
    }

    if ( parameters.hasMaxResult() )
    {
      typedQuery.setMaxResults( parameters.getMaxResults() );
    }

    return typedQuery
        .setHint( QueryHints.CACHEABLE, parameters.isCacheable( cacheable ) );
  }

  /**
   * Count number of objects based on given parameters
   * @param builder
   * @param parameters JpaQueryParameters
   * @return number of objects
   */
  protected final Long getCount( CriteriaBuilder builder, JpaQueryParameters<T> parameters  )
  {
    CriteriaQuery<Long> query = builder.createQuery( Long.class );

    Root<T> root = query.from( getClazz() );

    List<Function<Root<T>, Predicate>> predicateProviders = parameters.getPredicates();

    List<Function<Root<T>, Expression<Long>>> countExpressions = parameters.getCountExpressions();

    if ( !countExpressions.isEmpty() )
    {
      if ( countExpressions.size() > 1 )
      {
        query.multiselect( countExpressions.stream().map( c -> c.apply( root ) ).collect( Collectors.toList()) ) ;
      }
      else
      {
        query.select( countExpressions.get( 0 ).apply( root ) );
      }
    }
    else
    {
      query.select(  parameters.isUseDistinct() ?  builder.countDistinct( root ) :  builder.count( root ) );
    }

    if ( !predicateProviders.isEmpty() )
    {
      List<Predicate> predicates = predicateProviders.stream().map( t -> t.apply( root ) ).collect( Collectors.toList() );
      query.where( predicates.toArray( new Predicate[0] ) );
    }

    return getSession().createQuery( query )
        .setHint( QueryHints.CACHEABLE, parameters.isCacheable( cacheable ) )
        .getSingleResult();
  }

  /**
   * Retrieves an object based on the given Jpa Predicates.
   *
   * @param parameters
   * @return an object of the implementation Class type.
   */
  protected T getSingleResult( CriteriaBuilder builder,  JpaQueryParameters<T> parameters )
  {
    return getSingleResult( getTypedQuery( builder, parameters ) );
  }

  //------------------------------------------------------------------------------------------
  // End JPA Methods
  //------------------------------------------------------------------------------------------

  /**
   * Creates a SqlQuery.
   *
   * @param sql the SQL query String.
   * @return a NativeQuery<T> instance.
   */
  @SuppressWarnings("unchecked")
  protected final NativeQuery<T> getSqlQuery( String sql )
  {
    return getSession().createNativeQuery( sql )
        .setCacheable( cacheable ).setHint( QueryHints.CACHEABLE, cacheable );
  }

  /**
   * Creates a untyped SqlQuery.
   *
   * @param sql the SQL query String.
   * @return a NativeQuery<T> instance.
   */
  protected final NativeQuery<?> getUntypedSqlQuery( String sql  )
  {
    return getSession().createNativeQuery( sql )
        .setCacheable( cacheable ).setHint( QueryHints.CACHEABLE, cacheable );
  }

  // -------------------------------------------------------------------------
  // GenericIdentifiableObjectStore implementation
  // -------------------------------------------------------------------------

  @Override
  public void save( T object )
  {
//    AuditLogUtil.infoWrapper( log, object, AuditLogUtil.ACTION_CREATE );

    getSession().save( object );
  }

  @Override
  public void update( T object )
  {
    getSession().update( object );
  }

  @Override
  public void delete( T object )
  {
    getSession().delete( object );
  }

  @Override
  public T get( Long id )
  {
    T object = getSession().get( getClazz(), id );

    return postProcessObject( object );
  }

  /**
   * Override for further processing of a retrieved object.
   *
   * @param object the object.
   * @return the processed object.
   */
  protected T postProcessObject( T object )
  {
    return object;
  }

  @Override
  public List<T> getAll()
  {
    return getList( getCriteriaBuilder(), new JpaQueryParameters<T>() );
  }

//  @Override
//  public List<T> getAllByAttributes( List<Attribute> attributes )
//  {
//    CriteriaBuilder builder = getCriteriaBuilder();
//
//    JpaQueryParameters<T> parameters = new JpaQueryParameters<T>()
//        .addPredicate( root ->  root.join( "attributeValues", JoinType.INNER ).get( "attribute" ).in( attributes )  );
//
//    return getList( builder, parameters );
//  }

  @Override
  public int getCount()
  {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getCount( builder, newJpaParameters().count( root -> builder.countDistinct( root.get( "id" ) ) ) ).intValue();
  }

  /**
   * Create new instance of JpaQueryParameters
   * @return JpaQueryParameters<T>
   */
  protected JpaQueryParameters<T> newJpaParameters()
  {
    return new JpaQueryParameters<>();
  }
}