package com.mass3d.period;

import java.util.Calendar;
import com.mass3d.calendar.DateTimeUnit;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

//@Entity(name = "FinancialJulyPeriodType")
//@DiscriminatorValue("FinancialJuly")
public class FinancialJulyPeriodType
    extends FinancialPeriodType {

  public static final String NAME = "FinancialJuly";
  /**
   * Determines if a de-serialized file is compatible with this class.
   */
  private static final long serialVersionUID = 5190072405972068226L;
  private static final String ISO_FORMAT = "yyyyJuly";
  private static final String ISO8601_DURATION = "P1Y";

  @Override
  public int getBaseMonth() {
    return Calendar.JULY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getIsoDate(DateTimeUnit dateTimeUnit, com.mass3d.calendar.Calendar calendar) {
    return String.format("%dJuly", dateTimeUnit.getYear());
  }

  @Override
  public String getIsoFormat() {
    return ISO_FORMAT;
  }

  @Override
  public String getIso8601Duration() {
    return ISO8601_DURATION;
  }

}
