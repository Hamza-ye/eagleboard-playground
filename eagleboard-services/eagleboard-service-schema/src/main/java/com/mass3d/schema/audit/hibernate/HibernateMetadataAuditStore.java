package com.mass3d.schema.audit.hibernate;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import com.mass3d.common.Pager;
import com.mass3d.schema.audit.MetadataAudit;
import com.mass3d.schema.audit.MetadataAuditQuery;
import com.mass3d.schema.audit.MetadataAuditStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("com.mass3d.schema.audit.MetadataAuditStore")
public class HibernateMetadataAuditStore
    implements MetadataAuditStore
{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public int save( MetadataAudit audit )
    {
        return (int) getCurrentSession().save( audit );
    }

    @Override
    public void delete( MetadataAudit audit )
    {
        getCurrentSession().delete( audit );
    }

    @Override
    public int count( MetadataAuditQuery query )
    {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();

        CriteriaQuery<Long> criteriaQuery = builder.createQuery( Long.class );

        Root<MetadataAudit> root = criteriaQuery.from( MetadataAudit.class );

        criteriaQuery.select( builder.countDistinct( root.get( "id" ) ) );

        criteriaQuery.where( buildCriteria( builder, root, query ).toArray( new Predicate[0] ) );

        return getCurrentSession().createQuery( criteriaQuery ).getSingleResult().intValue();
    }

    @Override
    public List<MetadataAudit> query( MetadataAuditQuery query )
    {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();

        CriteriaQuery<MetadataAudit> criteriaQuery = builder.createQuery( MetadataAudit.class );

        Root<MetadataAudit> root = criteriaQuery.from( MetadataAudit.class );

        criteriaQuery.where( buildCriteria( builder, root, query ).toArray( new Predicate[0] ) );

        Query<MetadataAudit> typedQuery = getCurrentSession().createQuery( criteriaQuery );

        if ( !query.isSkipPaging() )
        {
            Pager pager = query.getPager();
            typedQuery.setFirstResult( pager.getOffset() );
            typedQuery.setMaxResults( pager.getPageSize() );
        }

        return typedQuery.getResultList();
    }

    private List<Predicate> buildCriteria( CriteriaBuilder builder, Root<MetadataAudit> root, MetadataAuditQuery query )
    {
        List<Predicate> predicates = new ArrayList<>();

        if ( query.getKlass().isEmpty() )
        {
            Predicate disjunction = builder.disjunction();

            if ( !query.getUid().isEmpty() )
            {
                 disjunction.getExpressions().add( root.get( "uid" ).in( query.getUid() ) );
            }

            if ( !query.getCode().isEmpty() )
            {
                disjunction.getExpressions().add( root.get( "code" ).in( query.getCode() ) );
            }

            predicates.add( disjunction );
        }
        else if ( query.getUid().isEmpty() && query.getCode().isEmpty() )
        {
            predicates.add( root.get( "klass" ).in( query.getKlass() ) );
        }
        else
        {
            Predicate disjunction = builder.disjunction();

            if ( !query.getUid().isEmpty() )
            {
                Predicate conjunction = builder.and( root.get( "klass" ).in( query.getKlass() ), root.get( "uid" ).in( query.getUid() ) );
                disjunction.getExpressions().add( conjunction );
            }

            if ( !query.getCode().isEmpty() )
            {
                Predicate conjunction = builder.and( root.get( "klass" ).in( query.getKlass() ), root.get( "code" ).in( query.getUid() ) );
                disjunction.getExpressions().add( conjunction );
            }

            predicates.add( disjunction );
        }

        if ( query.getCreatedAt() != null )
        {
            predicates.add( builder.greaterThanOrEqualTo( root.get( "createdAt" ), query.getCreatedAt() ) );
        }

        if ( query.getCreatedBy() != null )
        {
            predicates.add( builder.equal( root.get( "createdBy" ), query.getCreatedBy() ) );
        }

        if ( query.getType() != null )
        {
            predicates.add( builder.equal( root.get( "type" ), query.getType() ) );
        }

        return predicates;
    }

    private Session getCurrentSession()
    {
        return sessionFactory.getCurrentSession();
    }
}
