package com.mass3d.period;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * PeriodType for weekly Periods. A valid weekly Period has startDate set to Thursday and endDate
 * set to Wednesday the same week, assuming Thursday is the first day and Wednesday is the last day
 * of the week.
 *
 */
//@Entity(name = "WeeklyThursdayPeriodType")
//@DiscriminatorValue("WeeklyThursday")
public class WeeklyThursdayPeriodType
    extends WeeklyAbstractPeriodType {

  public static final String NAME = "WeeklyThursday";

  public WeeklyThursdayPeriodType() {
    super(NAME, 4, "yyyyThuWn", "P7D", 7, "ThuW");
  }
}
