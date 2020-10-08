package com.mass3d.document.impl;

import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.user.CurrentUserService;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.document.Document;
import com.mass3d.document.DocumentStore;
import com.mass3d.user.User;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository( "com.mass3d.document.DocumentStore" )
public class HibernateDocumentStore
    extends HibernateIdentifiableObjectStore<Document> implements DocumentStore {

  public HibernateDocumentStore( SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService,
      CurrentUserService currentUserService, AclService aclService ) {
    super( sessionFactory, jdbcTemplate, deletedObjectService, Document.class, currentUserService, aclService, false );
  }

  @Override
  public long getCountByUser(User user) {
    CriteriaBuilder builder = getSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<Document> root = query.from(Document.class);
    query.select(builder.count(root));
    query.where(builder.equal(root.get("user"), user));

    return getSession().createQuery(query).getSingleResult();
  }
}