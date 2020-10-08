package com.mass3d.system.util;

import static com.mass3d.expression.ExpressionService.GROUP_ID;
import static com.mass3d.expression.ExpressionService.GROUP_KEY;
import static com.mass3d.expression.ExpressionService.WILDCARD_EXPRESSION;

public class ExpressionUtils {

  /**
   * Normalizes the given expression. Ensures that data element operands which contains wild cards
   * and represents a data element total are rewritten in order to remove the wild cards.
   *
   * @param expression the expression to normalize.
   * @return the normalized expression.
   */
  public static String normalizeExpression(String expression) {
    return expression.replaceAll(WILDCARD_EXPRESSION, "${" + GROUP_KEY + "}{${" + GROUP_ID + "}}");
  }
}
