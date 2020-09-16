package com.mass3d.services.core.query.operators;

import com.mass3d.services.core.query.planner.QueryPath;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class NotEqualOperator extends EqualOperator {

  public NotEqualOperator(Object arg) {
    super("ne", arg);
  }

  @Override
  public Criterion getHibernateCriterion(QueryPath queryPath) {
    return Restrictions.not(super.getHibernateCriterion(queryPath));
  }

  @Override
  public boolean test(Object value) {
    return !super.test(value);
  }
}
