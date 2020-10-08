package com.mass3d.system.jep;

/**
 * Sample standard defiation, where the variance is the sum of the squares of the distance from the
 * mean to the sample divided by the number of samples minus 1.
 * <p/>
 * If the number of samples is 1, returns 0.
 *
 */
public class StandardDeviationSample
    extends StandardDeviationBase {

  @Override
  protected double getVariance(double sum2, double n) {
    if (n == 1) {
      throw new NoValueException();
    }

    return sum2 / (n - 1);
  }
}
