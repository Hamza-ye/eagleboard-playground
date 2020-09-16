package com.mass3d.services.core.query.planner;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.mass3d.api.schema.Property;
import java.util.Arrays;

public class QueryPath {

  private static final Joiner PATH_JOINER = Joiner.on(".");
  private final Property property;
  private final boolean persisted;
  private String[] alias = new String[]{};

  public QueryPath(Property property, boolean persisted) {
    this.property = property;
    this.persisted = persisted;
  }

  public QueryPath(Property property, boolean persisted, String[] alias) {
    this(property, persisted);
    this.alias = alias;
  }

  public Property getProperty() {
    return property;
  }

  public String getPath() {
    return haveAlias() ? PATH_JOINER.join(alias) + "." + property.getFieldName()
        : property.getFieldName();
  }

  public boolean isPersisted() {
    return persisted;
  }

  public String[] getAlias() {
    return alias;
  }

  public boolean haveAlias() {
    return alias != null && alias.length > 0;
  }

  public boolean haveAlias(int n) {
    return alias != null && alias.length > n;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", property.getName())
        .add("path", getPath())
        .add("persisted", persisted)
        .add("alias", Arrays.toString(alias))
        .toString();
  }
}
