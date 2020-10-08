package com.mass3d.system.jep;

/**
 * Population standard deviation, where the variance is the sum of the squares of the distance from
 * the mean to the sample divided by the number of samples
 *
 */
public class StandardDeviationPopulation
    extends StandardDeviationBase {

  @Override
  protected double getVariance(double sum2, double n) {
    return sum2 / n;
  }
}
