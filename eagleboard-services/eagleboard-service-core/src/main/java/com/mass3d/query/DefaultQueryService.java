package com.mass3d.query;

import com.mass3d.common.IdentifiableObject;
import com.mass3d.fieldfilter.Defaults;
import com.mass3d.preheat.Preheat;
import com.mass3d.query.planner.QueryPlan;
import com.mass3d.query.planner.QueryPlanner;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.query.QueryService" )
public class DefaultQueryService
    implements QueryService {

  private static final Log log = LogFactory.getLog(DefaultQueryService.class);

  private final QueryParser queryParser;

  private final QueryPlanner queryPlanner;

  private final CriteriaQueryEngine<? extends IdentifiableObject> criteriaQueryEngine;

  private final InMemoryQueryEngine<? extends IdentifiableObject> inMemoryQueryEngine;

  public DefaultQueryService(QueryParser queryParser, QueryPlanner queryPlanner,
      CriteriaQueryEngine<? extends IdentifiableObject> criteriaQueryEngine,
      InMemoryQueryEngine<? extends IdentifiableObject> inMemoryQueryEngine) {
    this.queryParser = queryParser;
    this.queryPlanner = queryPlanner;
    this.criteriaQueryEngine = criteriaQueryEngine;
    this.inMemoryQueryEngine = inMemoryQueryEngine;
  }

  @Override
  public List<? extends IdentifiableObject> query(Query query) {
    return queryObjects(query);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<? extends IdentifiableObject> query(Query query, ResultTransformer transformer) {
    List<? extends IdentifiableObject> objects = queryObjects(query);

    if (transformer != null) {
      return transformer.transform(objects);
    }

    return objects;
  }

  @Override
  public int count(Query query) {
    query.setFirstResult(0);
    query.setMaxResults(Integer.MAX_VALUE);

    return queryObjects(query).size();
  }

  @Override
  public Query getQueryFromUrl(Class<?> klass, List<String> filters, List<Order> orders)
      throws QueryParserException {
    return getQueryFromUrl(klass, filters, orders, Junction.Type.AND);
  }

  @Override
  public Query getQueryFromUrl(Class<?> klass, List<String> filters, List<Order> orders,
      Junction.Type rootJunction) throws QueryParserException {
    Query query = queryParser.parse(klass, filters, rootJunction);
    query.addOrders(orders);

    return query;
  }

  //---------------------------------------------------------------------------------------------
  // Helper methods
  //---------------------------------------------------------------------------------------------

  private List<? extends IdentifiableObject> queryObjects(Query query) {
    List<? extends IdentifiableObject> objects = query.getObjects();

    if (objects != null) {
      objects = inMemoryQueryEngine.query(query.setObjects(objects));
      clearDefaults(query.getSchema().getKlass(), objects, query.getDefaults());

      return objects;
    }

    QueryPlan queryPlan = queryPlanner.planQuery(query);

    Query pQuery = queryPlan.getPersistedQuery();
    Query npQuery = queryPlan.getNonPersistedQuery();

    objects = criteriaQueryEngine.query(pQuery);

    if (!npQuery.isEmpty()) {
      if (log.isDebugEnabled()) {
        log.debug("Doing in-memory for " + npQuery.getCriterions().size() + " criterions and "
            + npQuery.getOrders().size() + " orders.");
      }

      npQuery.setObjects(objects);

      objects = inMemoryQueryEngine.query(npQuery);
    }

    clearDefaults(query.getSchema().getKlass(), objects, query.getDefaults());

    return objects;
  }

  private void clearDefaults(Class<?> klass, List<? extends IdentifiableObject> objects,
      Defaults defaults) {
    if (Defaults.INCLUDE == defaults || !Preheat.isDefaultClass(klass)) {
      return;
    }

    objects.removeIf(object -> "default".equals(object.getName()));
  }
}
