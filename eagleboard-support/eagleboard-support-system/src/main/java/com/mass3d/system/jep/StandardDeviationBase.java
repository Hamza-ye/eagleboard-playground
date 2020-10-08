package com.mass3d.system.jep;

import java.util.List;
import java.util.Stack;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.function.PostfixMathCommandI;

public abstract class StandardDeviationBase
    extends PostfixMathCommand
    implements PostfixMathCommandI {

  public StandardDeviationBase() {
    numberOfParameters = 1;
  }

  /**
   * Each subclass defines its variance computation.
   *
   * @param sum2 Sum of the squares of distance from the mean
   * @param n Total number of samples
   * @return the variances
   */
  protected abstract double getVariance(double sum2, double n);

  // nFunk's JEP run() method uses the raw Stack type
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void run(Stack inStack)
      throws ParseException {
    checkStack(inStack);

    Object param = inStack.pop();
    List<Double> vals = CustomFunctions.checkVector(param);
    int n = vals.size();

    if (n == 0) {
      throw new NoValueException();
    } else {
      double sum = 0, sum2 = 0, mean, variance;

      for (Double v : vals) {
        sum = sum + v;
      }

      mean = sum / n;

      for (Double v : vals) {
        sum2 = sum2 + ((v - mean) * (v - mean));
      }

      variance = getVariance(sum2, n);

      inStack.push(new Double(Math.sqrt(variance)));
    }
  }
}
