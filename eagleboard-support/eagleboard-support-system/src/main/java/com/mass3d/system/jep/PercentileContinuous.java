package com.mass3d.system.jep;

import org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType;

/**
 * The percentileContinuous function is equivalent to the PostgreSQL function percentile_cont and
 * the Excel function PERCENTILE.INC
 *
 */
public class PercentileContinuous
    extends PercentileBase {

  @Override
  protected EstimationType getEstimationType() {
    return EstimationType.R_7;
  }
}
