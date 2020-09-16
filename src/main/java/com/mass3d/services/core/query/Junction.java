package com.mass3d.services.core.query;

import com.mass3d.services.schema.Schema;

public abstract class Junction extends Criteria implements Criterion {

  protected Type type;

  public Junction(Schema schema, Type type) {
    super(schema);
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "[ " + type + ", " + criterions + "]";
  }


  public enum Type {
    AND, OR
  }
}
