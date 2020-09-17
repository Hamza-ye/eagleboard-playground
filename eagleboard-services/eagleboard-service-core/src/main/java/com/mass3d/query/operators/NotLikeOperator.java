package com.mass3d.query.operators;

import com.mass3d.query.planner.QueryPath;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class NotLikeOperator extends LikeOperator {

  public NotLikeOperator(Object arg, boolean caseSensitive, MatchMode matchMode) {
    super("!like", arg, caseSensitive, matchMode);
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
