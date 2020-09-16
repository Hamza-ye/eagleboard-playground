package com.mass3d.services.core.query.planner;

import com.mass3d.api.user.User;
import com.mass3d.services.core.query.Query;
import com.mass3d.services.schema.Schema;

public class QueryPlan {

  private final Query persistedQuery;

  private final Query nonPersistedQuery;

  public QueryPlan(Query persistedQuery, Query nonPersistedQuery) {
    this.persistedQuery = persistedQuery;
    this.nonPersistedQuery = nonPersistedQuery;
  }

  public Query getPersistedQuery() {
    return persistedQuery;
  }

  public Query getNonPersistedQuery() {
    return nonPersistedQuery;
  }

  public Schema getSchema() {
    if (persistedQuery != null) {
      return persistedQuery.getSchema();
    } else if (nonPersistedQuery != null) {
      return nonPersistedQuery.getSchema();
    }

    return null;
  }

  public User getUser() {
    if (persistedQuery != null) {
      return persistedQuery.getUser();
    } else if (nonPersistedQuery != null) {
      return nonPersistedQuery.getUser();
    }

    return null;
  }

  public void setUser(User user) {
    if (persistedQuery != null) {
      persistedQuery.setUser(user);
    }

    if (nonPersistedQuery != null) {
      nonPersistedQuery.setUser(user);
    }
  }
}
