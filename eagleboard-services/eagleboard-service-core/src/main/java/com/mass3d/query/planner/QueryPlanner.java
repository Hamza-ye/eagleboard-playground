package com.mass3d.query.planner;

import com.mass3d.query.Query;
import com.mass3d.schema.Schema;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public interface QueryPlanner {

  QueryPlan planQuery(Query query);

  QueryPlan planQuery(Query query, boolean persistedOnly);

  QueryPath getQueryPath(Schema schema, String path);

  Path getQueryPath(Root root, Schema schema, String path);
}
