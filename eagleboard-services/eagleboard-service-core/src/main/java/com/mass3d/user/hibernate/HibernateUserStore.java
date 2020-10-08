package com.mass3d.user.hibernate;

import com.mass3d.common.IdentifiableObjectUtils;
import com.mass3d.deletedobject.DeletedObjectService;
import com.mass3d.security.acl.AclService;
import com.mass3d.todotask.TodoTask;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserQueryParams;
import com.mass3d.user.UserStore;
import com.mass3d.common.hibernate.HibernateIdentifiableObjectStore;
import com.mass3d.query.JpaQueryUtils;
import com.mass3d.query.Order;
import com.mass3d.query.QueryUtils;
import com.mass3d.schema.Schema;
import com.mass3d.schema.SchemaService;
import com.mass3d.commons.util.SqlHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserStore
    extends HibernateIdentifiableObjectStore<User>
    implements UserStore {

  private final SchemaService schemaService;

  public HibernateUserStore( SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
      DeletedObjectService deletedObjectService, CurrentUserService currentUserService,
      AclService aclService,  SchemaService schemaService  ) {
    super( sessionFactory, jdbcTemplate, deletedObjectService, User.class,
        currentUserService, aclService, false );
    this.schemaService = schemaService;
  }


  @Override
  public List<User> getUsers(UserQueryParams params, @Nullable List<String> orders) {
    return extractUserQueryUsers(getUserQuery(params, orders, false).list());
  }

  @Override
  public List<User> getUsers(UserQueryParams params) {
    return getUsers(params, null);
  }

  @Override
  public List<User> getExpiringUsers(UserQueryParams params) {
    return extractUserQueryUsers(getUserQuery(params, null, false).list());
  }

  @Override
  public int getUserCount(UserQueryParams params) {
    Long count = (Long) getUserQuery(params, null, true).uniqueResult();
    return count != null ? count.intValue() : 0;
  }

  @Nonnull
  private List<User> extractUserQueryUsers(@Nonnull List<?> result) {
    if (result.isEmpty()) {
      return Collections.emptyList();
    }
    final List<User> users = new ArrayList<>(result.size());
    for (Object o : result) {
      if (o instanceof User) {
        users.add((User) o);
      } else if (o.getClass().isArray()) {
        users.add((User) ((Object[]) o)[0]);
      }
    }
    return users;
  }

  private Query getUserQuery(UserQueryParams params, List<String> orders, boolean count) {
    SqlHelper hlp = new SqlHelper();

    List<Order> convertedOrder = null;
    String hql;
    if (count) {
      hql = "select count(distinct u) ";
    } else {
      Schema userSchema = schemaService.getSchema(User.class);
      convertedOrder = QueryUtils.convertOrderStrings(orders, userSchema);

      hql = Stream.of("select distinct u", JpaQueryUtils
          .createSelectOrderExpression(convertedOrder, "u")).filter(Objects::nonNull)
          .collect(Collectors.joining(","));
      hql += " ";
    }

    hql +=
        "from User u " +
            "inner join u.userCredentials uc " +
            "left join u.groups g ";

    // Todo Eagle commented out, need to add the same for TodoTask and/or activity ...
//        if ( !params.getTodoTasks().isEmpty() )
//        {
//            hql += "left join u.organisationUnits ou ";
//
//            if ( params.isIncludeOrgUnitChildren() )
//            {
//                hql += hlp.whereAnd() + " (";
//
//                for ( int i = 0; i < params.getTodoTasks().size(); i++ )
//                {
//                    hql += String.format( "ou.path like :ouUid%d or ", i );
//                }
//
//                hql = TextUtils.removeLastOr( hql ) + ")";
//            }
//            else
//            {
//                hql += hlp.whereAnd() + " ou.id in (:ouIds) ";
//            }
//        }

    if (params.getDisabled() != null) {
      hql += hlp.whereAnd() + " uc.disabled = :disabled ";
    }

    if (params.isNot2FA()) {
      hql += hlp.whereAnd() + " uc.secret is null ";
    }

    if (params.getQuery() != null) {
      hql += hlp.whereAnd() + " (" +
          "lower(u.firstName) like :key " +
          "or lower(u.email) like :key " +
          "or lower(u.surname) like :key " +
          "or lower(uc.username) like :key) ";
    }

    if (params.getPhoneNumber() != null) {
      hql += hlp.whereAnd() + " u.phoneNumber = :phoneNumber ";
    }

    if (params.isCanManage() && params.getUser() != null) {
      hql += hlp.whereAnd() + " g.id in (:ids) ";
    }

    if (params.isAuthSubset() && params.getUser() != null) {
      hql += hlp.whereAnd() + " not exists (" +
          "select uc2 from UserCredentials uc2 " +
          "inner join uc2.userAuthorityGroups ag2 " +
          "inner join ag2.authorities a " +
          "where uc2.id = uc.id " +
          "and a not in (:auths) ) ";
    }

    // TODO handle users with no user roles

    if (params.isDisjointRoles() && params.getUser() != null) {
      hql += hlp.whereAnd() + " not exists (" +
          "select uc3 from UserCredentials uc3 " +
          "inner join uc3.userAuthorityGroups ag3 " +
          "where uc3.id = uc.id " +
          "and ag3.id in (:roles) ) ";
    }

    if (params.getLastLogin() != null) {
      hql += hlp.whereAnd() + " uc.lastLogin >= :lastLogin ";
    }

    if (params.getInactiveSince() != null) {
      hql += hlp.whereAnd() + " uc.lastLogin < :inactiveSince ";
    }

    if (params.getPasswordLastUpdated() != null) {
      hql += hlp.whereAnd() + " uc.passwordLastUpdated < :passwordLastUpdated ";
    }

    if (params.isSelfRegistered()) {
      hql += hlp.whereAnd() + " uc.selfRegistered = true ";
    }

//    if (UserInvitationStatus.ALL.equals(params.getInvitationStatus())) {
//      hql += hlp.whereAnd() + " uc.invitation = true ";
//    }
//
//    if (UserInvitationStatus.EXPIRED.equals(params.getInvitationStatus())) {
//      hql += hlp.whereAnd() + " uc.invitation = true " +
//          "and uc.restoreToken is not null " +
//          "and uc.restoreExpiry is not null " +
//          "and uc.restoreExpiry < current_timestamp() ";
//    }

    if (!count) {
      String orderExpression = JpaQueryUtils.createOrderExpression(convertedOrder, "u");
      hql += "order by " + StringUtils.defaultString(orderExpression, "u.surname, u.firstName");
    }

    Query query = sessionFactory.getCurrentSession().createQuery(hql);

    if (params.getQuery() != null) {
      query.setParameter("key", "%" + params.getQuery().toLowerCase() + "%");
    }

    if (params.getPhoneNumber() != null) {
      query.setParameter("phoneNumber", params.getPhoneNumber());
    }

    if (params.isCanManage() && params.getUser() != null) {
      Collection<Long> managedGroups = IdentifiableObjectUtils
          .getIdentifiers(params.getUser().getManagedGroups());

      query.setParameterList("ids", managedGroups);
    }

    if (params.getDisabled() != null) {
      query.setParameter("disabled", params.getDisabled());
    }

    if (params.isAuthSubset() && params.getUser() != null) {
      Set<String> auths = params.getUser().getUserCredentials().getAllAuthorities();

      query.setParameterList("auths", auths);
    }

    if (params.isDisjointRoles() && params.getUser() != null) {
      Collection<Long> roles = IdentifiableObjectUtils
          .getIdentifiers(params.getUser().getUserCredentials().getUserAuthorityGroups());

      query.setParameterList("roles", roles);
    }

    if (params.getLastLogin() != null) {
      query.setParameter("lastLogin", params.getLastLogin());
    }

    if (params.getPasswordLastUpdated() != null) {
      query.setParameter("passwordLastUpdated", params.getPasswordLastUpdated());
    }

    if (params.getInactiveSince() != null) {
      query.setParameter("inactiveSince", params.getInactiveSince());
    }

    // Todo Eagle commented out, need to add the same for TodoTask and/or activity ...
//        if ( !params.getTodoTasks().isEmpty() )
//        {
//            if ( params.isIncludeOrgUnitChildren() )
//            {
//                for ( int i = 0; i < params.getTodoTasks().size(); i++ )
//                {
//                    query.setParameter( String.format( "ouUid%d", i ), "%/" + params.getTodoTasks().get( i ).getUid() + "%" );
//                }
//            }
//            else
//            {
//                Collection<Integer> ouIds = IdentifiableObjectUtils.getIdentifiers( params.getTodoTasks() );
//
//                query.setParameterList( "ouIds", ouIds );
//            }
//        }

    if (params.getFirst() != null) {
      query.setFirstResult(params.getFirst());
    }

    if (params.getMax() != null) {
      query.setMaxResults(params.getMax()).list();
    }

    return query;
  }

  @Override
  public int getUserCount() {
    Query<Long> query = getTypedQuery("select count(*) from User");
    return query.uniqueResult().intValue();
  }
}
