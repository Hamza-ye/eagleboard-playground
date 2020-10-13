package com.mass3d.query.planner;

import com.mass3d.schema.Property;
import com.mass3d.query.Conjunction;
import com.mass3d.query.Criterion;
import com.mass3d.query.Disjunction;
import com.mass3d.query.Query;
import com.mass3d.query.Restriction;
import com.mass3d.schema.Schema;
import com.mass3d.schema.SchemaService;
import com.mass3d.query.Junction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.query.planner.QueryPlanner" )
public class DefaultQueryPlanner implements QueryPlanner {

  private final SchemaService schemaService;

  public DefaultQueryPlanner(SchemaService schemaService) {
    this.schemaService = schemaService;
  }

  @Override
  public QueryPlan planQuery(Query query) {
    return planQuery(query, false);
  }

  @Override
  public QueryPlan planQuery(Query query, boolean persistedOnly) {
    if (Junction.Type.OR == query.getRootJunctionType() && !persistedOnly) {
      return new QueryPlan(
          Query.from(query.getSchema()).setPlannedQuery(true),
          Query.from(query).setPlannedQuery(true)
      );
    }

    Query npQuery = Query.from(query)
        .setUser(query.getUser()).setPlannedQuery(true);

    Query pQuery = getQuery(npQuery, persistedOnly)
        .setUser(query.getUser()).setPlannedQuery(true);

    // if there are any non persisted criterions left, we leave the paging to the in-memory engine
    if (!npQuery.getCriterions().isEmpty()) {
      pQuery.setSkipPaging(true);
    } else {
      pQuery.setFirstResult(npQuery.getFirstResult());
      pQuery.setMaxResults(npQuery.getMaxResults());
    }

    return new QueryPlan(pQuery, npQuery);
  }

  @Override
  public QueryPath getQueryPath(Schema schema, String path) {
    Schema curSchema = schema;
    Property curProperty = null;
    boolean persisted = true;
    List<String> alias = new ArrayList<>();
    String[] pathComponents = path.split("\\.");

    if (pathComponents.length == 0) {
      return null;
    }

    for (int idx = 0; idx < pathComponents.length; idx++) {
      String name = pathComponents[idx];
      curProperty = curSchema.getProperty(name);

      if (curProperty == null) {
        throw new RuntimeException("Invalid path property: " + name);
      }

      if (!curProperty.isPersisted()) {
        persisted = false;
      }

      if ((!curProperty.isSimple() && idx == pathComponents.length - 1)) {
        return new QueryPath(curProperty, persisted, alias.toArray(new String[]{}));
      }

      if (curProperty.isCollection()) {
        curSchema = schemaService.getDynamicSchema(curProperty.getItemKlass());
        alias.add(curProperty.getFieldName());
      } else if (!curProperty.isSimple()) {
        curSchema = schemaService.getDynamicSchema(curProperty.getKlass());
        alias.add(curProperty.getFieldName());
      } else {
        return new QueryPath(curProperty, persisted, alias.toArray(new String[]{}));
      }
    }

    return new QueryPath(curProperty, persisted, alias.toArray(new String[]{}));
  }

  public Path getQueryPath(Root root, Schema schema, String path) {
    Schema curSchema = schema;
    Property curProperty = null;
    String[] pathComponents = path.split("\\.");

    Path currentPath = root;

    if (pathComponents.length == 0) {
      return null;
    }

    for (int idx = 0; idx < pathComponents.length; idx++) {
      String name = pathComponents[idx];
      curProperty = curSchema.getProperty(name);

      if (curProperty == null) {
        throw new RuntimeException("Invalid path property: " + name);
      }

      if ((!curProperty.isSimple() && idx == pathComponents.length - 1)) {
        return root.join(curProperty.getFieldName());
      }

      if (curProperty.isCollection()) {
        currentPath = root.join(curProperty.getFieldName());
        curSchema = schemaService.getDynamicSchema(curProperty.getItemKlass());
      } else if (!curProperty.isSimple()) {
        curSchema = schemaService.getDynamicSchema(curProperty.getKlass());
        currentPath = root.join(curProperty.getFieldName());
      } else {
        return currentPath.get(curProperty.getFieldName());
      }
    }

    return currentPath;
  }

  /**
   * @param query Query
   * @return Query instance
   */
  private Query getQuery(Query query, boolean persistedOnly) {
    Query pQuery = Query.from(query.getSchema(), query.getRootJunctionType());
    Iterator<Criterion> iterator = query.getCriterions().iterator();

    while (iterator.hasNext()) {
      Criterion criterion = iterator.next();

      if (Junction.class.isInstance(criterion)) {
        Junction junction = handleJunction(pQuery, (Junction) criterion, persistedOnly);

        if (!junction.getCriterions().isEmpty()) {
          pQuery.getAliases().addAll(junction.getAliases());
          pQuery.add(junction);
        }

        if (((Junction) criterion).getCriterions().isEmpty()) {
          iterator.remove();
        }
      } else if (Restriction.class.isInstance(criterion)) {
        Restriction restriction = (Restriction) criterion;
        restriction.setQueryPath(getQueryPath(query.getSchema(), restriction.getPath()));

        if (restriction.getQueryPath().isPersisted() && !restriction.getQueryPath().haveAlias()) {
          pQuery.getAliases()
              .addAll(Arrays.asList(((Restriction) criterion).getQueryPath().getAlias()));
          pQuery.getCriterions().add(criterion);
          iterator.remove();
        }
      }
    }

    if (query.ordersPersisted()) {
      pQuery.addOrders(query.getOrders());
      query.clearOrders();
    }

    return pQuery;
  }

  private Junction handleJunction(Query query, Junction queryJunction, boolean persistedOnly) {
    Iterator<Criterion> iterator = queryJunction.getCriterions().iterator();
    Junction criteriaJunction = Disjunction.class.isInstance(queryJunction) ?
        new Disjunction(query.getSchema()) : new Conjunction(query.getSchema());

    while (iterator.hasNext()) {
      Criterion criterion = iterator.next();

      if (Junction.class.isInstance(criterion)) {
        Junction junction = handleJunction(query, (Junction) criterion, persistedOnly);

        if (!junction.getCriterions().isEmpty()) {
          criteriaJunction.getAliases().addAll(junction.getAliases());
          criteriaJunction.add(junction);
        }

        if (((Junction) criterion).getCriterions().isEmpty()) {
          iterator.remove();
        }
      } else if (Restriction.class.isInstance(criterion)) {
        Restriction restriction = (Restriction) criterion;
        restriction.setQueryPath(getQueryPath(query.getSchema(), restriction.getPath()));

        if (restriction.getQueryPath().isPersisted() && !restriction.getQueryPath().haveAlias(1)) {
          criteriaJunction.getAliases()
              .addAll(Arrays.asList(((Restriction) criterion).getQueryPath().getAlias()));
          criteriaJunction.getCriterions().add(criterion);
          iterator.remove();
        } else if (persistedOnly) {
          throw new RuntimeException("Path " + restriction.getQueryPath().getPath() +
              " is not fully persisted, unable to build persisted only query plan.");
        }
      }
    }

    return criteriaJunction;
  }
}
