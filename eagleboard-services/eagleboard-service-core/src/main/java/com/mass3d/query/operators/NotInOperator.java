package com.mass3d.query.operators;

import com.mass3d.query.planner.QueryPath;
import java.util.Collection;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class NotInOperator extends InOperator {

  public NotInOperator(Collection<?> arg) {
    super("!in", arg);
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
