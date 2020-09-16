package com.mass3d.services.core.query;

import com.mass3d.services.schema.Schema;

public class Disjunction extends Junction {

  public Disjunction(Schema schema) {
    super(schema, Type.OR);
  }

  @Override
  public String toString() {
    return "OR[" + criterions + "]";
  }
}
