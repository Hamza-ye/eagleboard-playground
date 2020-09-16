package com.mass3d.services.core.query.operators;

import com.mass3d.api.schema.Property;
import com.mass3d.services.core.query.QueryException;
import com.mass3d.services.core.query.QueryUtils;
import com.mass3d.services.core.query.Type;
import com.mass3d.services.core.query.Typed;
import com.mass3d.services.core.query.planner.QueryPath;
import java.util.Collection;
import java.util.Date;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class GreaterEqualOperator extends Operator {

  public GreaterEqualOperator(Object arg) {
    super("ge", Typed.from(String.class, Boolean.class, Number.class, Date.class), arg);
  }

  @Override
  public Criterion getHibernateCriterion(QueryPath queryPath) {
    Property property = queryPath.getProperty();

    if (property.isCollection()) {
      Integer value = QueryUtils.parseValue(Integer.class, args.get(0));

      if (value == null) {
        throw new QueryException(
            "Left-side is collection, and right-side is not a valid integer, so can't compare by size.");
      }

      return Restrictions.sizeGe(queryPath.getPath(), value);
    }

    return Restrictions.ge(queryPath.getPath(), args.get(0));
  }

  @Override
  public boolean test(Object value) {
    if (args.isEmpty() || value == null) {
      return false;
    }

    Type type = new Type(value);

    if (type.isString()) {
      String s1 = getValue(String.class);
      String s2 = (String) value;

      return s1 != null && (s2.equals(s1) || s2.compareTo(s1) > 0);
    }
    if (type.isInteger()) {
      Integer s1 = getValue(Integer.class);
      Integer s2 = (Integer) value;

      return s1 != null && s2 >= s1;
    } else if (type.isFloat()) {
      Float s1 = getValue(Float.class);
      Float s2 = (Float) value;

      return s1 != null && s2 >= s1;
    } else if (type.isDate()) {
      Date s1 = getValue(Date.class);
      Date s2 = (Date) value;

      return s1 != null && (s2.after(s1) || s2.equals(s1));
    } else if (type.isCollection()) {
      Collection<?> collection = (Collection<?>) value;
      Integer size = getValue(Integer.class);

      return size != null && collection.size() >= size;
    }

    return false;
  }
}
