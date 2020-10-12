package com.mass3d.option.hibernate;

import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.option.Option;
import com.mass3d.option.OptionStore;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

//@Transactional
@Repository( "com.mass3d.option.OptionStore" )
public class HibernateOptionStore
    extends HibernateIdentifiableObjectStore<Option>
    implements OptionStore {

  public HibernateOptionStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, Option.class, currentUserService,
        aclService, true);
  }
  // -------------------------------------------------------------------------
  // Implementation methods
  // -------------------------------------------------------------------------

  @Override
  public List<Option> getOptions(int optionSetId, String key, Integer max) {
    String hql =
        "select option from OptionSet as optionset " +
            "join optionset.options as option where optionset.id = :optionSetId ";

    if (key != null) {
      hql += "and lower(option.name) like lower('%" + key + "%') ";
    }

    hql += "order by index(option)";

    Query<Option> query = getQuery(hql);
    query.setParameter("optionSetId", optionSetId);

    if (max != null) {
      query.setMaxResults(max);
    }

    return query.list();
  }
}
