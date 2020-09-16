package com.mass3d.services.core.query;

import com.mass3d.services.schema.Schema;

public class Conjunction extends Junction {

  public Conjunction(Schema schema) {
    super(schema, Type.AND);
  }

  @Override
  public String toString() {
    return "AND[" + criterions + "]";
  }
}
