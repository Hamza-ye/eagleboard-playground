package com.mass3d.system.jep;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * Base abstract class for percentile functions
 * <p/>
 * All percentile function take two arguments:
 * <p/>
 * percentile... ( fraction, valueList )
 * <p/>
 * The percentile is computed according to the EstimationType of the subclass.
 *
 */
public abstract class PercentileBase
    extends PostfixMathCommand
    implements PostfixMathCommandI {

  private final Percentile percentile = new Percentile().withEstimationType(getEstimationType());

  public PercentileBase() {
    numberOfParameters = 2;
  }

  /**
   * Each subclass defines its percentile estimation type.
   *
   * @return the percentile estimation type.
   */
  protected abstract EstimationType getEstimationType();

  // nFunk's JEP run() method uses the raw Stack type
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void run(Stack inStack)
      throws ParseException {
    checkStack(inStack);

    // First arg was pushed on the stack first, and pops last.
    Object valueList = inStack.pop();
    Object fractionObject = inStack.pop();

    List<Double> valList = CustomFunctions.checkVector(valueList);
    Double fraction = CustomFunctions.checkDouble(fractionObject);

    if (valList.size() == 0 || fraction < 0d || fraction > 1d) {
      throw new NoValueException();
    } else if (fraction == 0d) {
      inStack.push(valList.get(0));
    } else {
      Collections.sort(valList);

      double[] vals = ArrayUtils.toPrimitive(valList.toArray(new Double[0]));

      Double result = percentile.evaluate(vals, fraction * 100.);

      inStack.push(result);
    }
  }
}
