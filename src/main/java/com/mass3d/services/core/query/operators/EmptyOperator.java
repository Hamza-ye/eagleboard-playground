package com.mass3d.services.core.query.operators;

import com.mass3d.services.core.query.Type;
import com.mass3d.services.core.query.Typed;
import com.mass3d.services.core.query.planner.QueryPath;
import java.util.Collection;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class EmptyOperator extends Operator {

  public EmptyOperator() {
    super("empty", Typed.from(Collection.class));
  }

  @Override
  public Criterion getHibernateCriterion(QueryPath queryPath) {
    return Restrictions.sizeEq(queryPath.getPath(), 0);
  }

  @Override
  public boolean test(Object value) {
    if (value == null) {
      return false;
    }

    Type type = new Type(value);

    if (type.isCollection()) {
      Collection<?> collection = (Collection<?>) value;
      return collection.isEmpty();
    }

    return false;
  }
}