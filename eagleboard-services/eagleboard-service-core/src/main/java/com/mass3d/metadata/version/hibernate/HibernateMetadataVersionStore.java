package com.mass3d.metadata.version.hibernate;

import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.metadata.version.MetadataVersion;
import com.mass3d.metadata.version.MetadataVersionStore;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of MetadataVersionStore.
 */
@Repository( "com.mass3d.metadata.version.MetadataVersionStore" )
//@Transactional
public class HibernateMetadataVersionStore
    extends HibernateIdentifiableObjectStore<MetadataVersion>
    implements MetadataVersionStore {

  public HibernateMetadataVersionStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService) {
    super(sessionFactory, jdbcTemplate, deletedObjectService, MetadataVersion.class,
        currentUserService, aclService, false);
  }

  @Override
  public MetadataVersion getVersionByKey(Long key) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getSingleResult(builder, newJpaParameters()
        .addPredicate(root -> builder.equal(root.get("id"), key)));
  }

  @Override
  public MetadataVersion getVersionByName(String versionName) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getSingleResult(builder, newJpaParameters()
        .addPredicate(root -> builder.equal(root.get("name"), versionName)));
  }

  @Override
  public MetadataVersion getCurrentVersion() {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getSingleResult(builder, newJpaParameters()
        .addOrder(root -> builder.desc(root.get("created")))
        .setMaxResults(1)
        .setCacheable(false));
  }

  @Override
  public List<MetadataVersion> getAllVersionsInBetween(Date startDate, Date endDate) {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getList(builder, newJpaParameters()
        .addPredicate(root -> builder.between(root.get("created"), startDate, endDate)));
  }

  @Override
  public MetadataVersion getInitialVersion() {
    CriteriaBuilder builder = getCriteriaBuilder();

    return getSingleResult(builder, newJpaParameters()
        .addOrder(root -> builder.asc(root.get("created")))
        .setMaxResults(1)
        .setCacheable(false));
  }
}
