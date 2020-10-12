package com.mass3d.option.hibernate;

import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.option.Option;
import com.mass3d.option.OptionGroupSet;
import com.mass3d.option.OptionGroupSetStore;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

//@Transactional
@Repository( "com.mass3d.option.OptionGroupSetStore" )
public class HibernateOptionGroupSetStore
    extends HibernateIdentifiableObjectStore<OptionGroupSet>
    implements OptionGroupSetStore {

  public HibernateOptionGroupSetStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, OptionGroupSet.class, currentUserService,
        aclService, true);
  }

}