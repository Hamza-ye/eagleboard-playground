package com.mass3d.period;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * PeriodType for weekly Periods. A valid weekly Period has startDate set to Sunday and endDate set
 * to Saturday the same week, assuming Sunday is the first day and Saturday is the last day of the
 * week.
 *
 */
@Entity(name = "WeeklySundayPeriodType")
@DiscriminatorValue("WeeklySunday")
public class WeeklySundayPeriodType
    extends WeeklyAbstractPeriodType {

  public static final String NAME = "WeeklySunday";

  public WeeklySundayPeriodType() {
    super(NAME, 7, "yyyySunWn", "P7D", 7, "SunW");
  }
}
