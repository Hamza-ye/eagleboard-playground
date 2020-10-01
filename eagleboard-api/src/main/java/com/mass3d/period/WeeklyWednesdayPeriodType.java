package com.mass3d.period;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * PeriodType for weekly Periods. A valid weekly Period has startDate set to Wednesday and endDate
 * set to Tuesday the same week, assuming Wednesday is the first day and Tuesday is the last day of
 * the week.
 *
 */
@Entity(name = "WeeklyWednesdayPeriodType")
@DiscriminatorValue("WeeklyWednesday")
public class WeeklyWednesdayPeriodType
    extends WeeklyAbstractPeriodType {

  public static final String NAME = "WeeklyWednesday";

  public WeeklyWednesdayPeriodType() {
    super(NAME, 3, "yyyyWedWn", "P7D", 7, "WedW");
  }
}
