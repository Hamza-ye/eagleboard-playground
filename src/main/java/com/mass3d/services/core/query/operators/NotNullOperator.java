package com.mass3d.services.core.query.operators;

import com.mass3d.services.core.query.Typed;
import com.mass3d.services.core.query.planner.QueryPath;
import java.util.Date;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class NotNullOperator extends Operator {

  public NotNullOperator() {
    super("!null", Typed.from(String.class, Boolean.class, Number.class, Date.class, Enum.class));
  }

  @Override
  public Criterion getHibernateCriterion(QueryPath queryPath) {
    return Restrictions.isNotNull(queryPath.getPath());
  }

  @Override
  public boolean test(Object value) {
    return value != null;
  }
}
