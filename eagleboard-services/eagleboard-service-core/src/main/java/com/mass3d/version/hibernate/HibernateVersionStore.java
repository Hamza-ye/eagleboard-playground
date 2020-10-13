package com.mass3d.version.hibernate;

import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import javax.persistence.criteria.CriteriaBuilder;
import com.mass3d.hibernate.HibernateGenericStore;
import com.mass3d.version.Version;
import com.mass3d.version.VersionStore;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository( "com.mass3d.version.VersionStore" )
public class HibernateVersionStore
    extends HibernateGenericStore<Version>
    implements VersionStore
{

    @Autowired
    public HibernateVersionStore(SessionFactory sessionFactory,
        JdbcTemplate jdbcTemplate) {
        super(sessionFactory, jdbcTemplate, Version.class, true);
    }

    @Override
    public Version getVersionByKey( String key )
    {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult( builder, newJpaParameters().addPredicate( root -> builder.equal( root.get( "key" ), key ) ) );
    }
}
