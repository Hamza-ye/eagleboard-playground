package com.mass3d.period;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * PeriodType for weekly Periods. A valid weekly Period has startDate set to Monday and endDate set
 * to Sunday the same week, assuming Monday is the first day and Sunday is the last day of the
 * week.
 *
 */
//@Entity(name = "WeeklyPeriodType")
//@DiscriminatorValue("Weekly")
public class WeeklyPeriodType
    extends WeeklyAbstractPeriodType {

  public static final String NAME = "Weekly";

  public WeeklyPeriodType() {
    super(NAME, 1, "yyyyWn", "P7D", 7, "W");
  }
}
