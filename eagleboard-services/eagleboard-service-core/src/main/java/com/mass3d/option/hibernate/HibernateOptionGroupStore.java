package com.mass3d.option.hibernate;

import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.option.Option;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import com.mass3d.common.DataDimensionType;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.option.OptionGroup;
import com.mass3d.option.OptionGroupSet;
import com.mass3d.option.OptionGroupStore;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

//@Transactional
@Repository( "com.mass3d.option.OptionGroupStore" )
public class HibernateOptionGroupStore
    extends HibernateIdentifiableObjectStore<OptionGroup>
    implements OptionGroupStore
{
    public HibernateOptionGroupStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
        DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
        AclService aclService) {
        super(sessionFactory, jdbcTemplate, deletedObjectService, OptionGroup.class, currentUserService,
            aclService, true);
    }

    @Override
    public List<OptionGroup> getOptionGroups( OptionGroupSet groupSet )
    {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList( builder, newJpaParameters()
            .addPredicates( getSharingPredicates( builder ) )
            .addPredicate( root -> builder.equal( root.get( "groupSet" ), groupSet ) ) );
    }

    @Override
    public List<OptionGroup> getOptionGroupsNoAcl( DataDimensionType dataDimensionType, boolean dataDimension )
    {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList( builder, newJpaParameters()
            .addPredicate( root -> builder.equal( root.get( "dataDimensionType" ), dataDimension ) )
            .addPredicate( root -> builder.equal( root.get( "dataDimension" ), dataDimension ) ) );
    }
}
