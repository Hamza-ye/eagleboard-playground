package com.mass3d.period;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RelativePeriodEnum {
  TODAY,
  YESTERDAY,
  LAST_3_DAYS,
  LAST_7_DAYS,
  LAST_14_DAYS,
  THIS_MONTH,
  LAST_MONTH,
  THIS_BIMONTH,
  LAST_BIMONTH,
  THIS_QUARTER,
  LAST_QUARTER,
  THIS_SIX_MONTH,
  LAST_SIX_MONTH,
  WEEKS_THIS_YEAR,
  MONTHS_THIS_YEAR,
  BIMONTHS_THIS_YEAR,
  QUARTERS_THIS_YEAR,
  THIS_YEAR,
  MONTHS_LAST_YEAR,
  QUARTERS_LAST_YEAR,
  LAST_YEAR,
  LAST_5_YEARS,
  LAST_12_MONTHS,
  LAST_6_MONTHS,
  LAST_3_MONTHS,
  LAST_6_BIMONTHS,
  LAST_4_QUARTERS,
  LAST_2_SIXMONTHS,
  THIS_FINANCIAL_YEAR,
  LAST_FINANCIAL_YEAR,
  LAST_5_FINANCIAL_YEARS,
  THIS_WEEK,
  LAST_WEEK,
  THIS_BIWEEK,
  LAST_BIWEEK,
  LAST_4_WEEKS,
  LAST_4_BIWEEKS,
  LAST_12_WEEKS,
  LAST_52_WEEKS;

  public static List<String> OPTIONS = new ArrayList<String>() {
    {
      addAll(Arrays.asList(TODAY.toString(), YESTERDAY.toString(), LAST_3_DAYS.toString(),
          LAST_7_DAYS.toString(), LAST_14_DAYS.toString(),
          THIS_MONTH.toString(), LAST_MONTH.toString(), THIS_BIMONTH.toString(),
          LAST_BIMONTH.toString(),
          THIS_QUARTER.toString(), LAST_QUARTER.toString(), THIS_SIX_MONTH.toString(),
          LAST_SIX_MONTH.toString(),
          WEEKS_THIS_YEAR.toString(), MONTHS_THIS_YEAR.toString(), BIMONTHS_THIS_YEAR.toString(),
          QUARTERS_THIS_YEAR.toString(), THIS_YEAR.toString(), MONTHS_LAST_YEAR.toString(),
          QUARTERS_LAST_YEAR.toString(),
          LAST_YEAR.toString(), LAST_5_YEARS.toString(), LAST_12_MONTHS.toString(),
          LAST_6_MONTHS.toString(), LAST_3_MONTHS.toString(), LAST_6_BIMONTHS.toString(),
          LAST_4_QUARTERS.toString(), LAST_2_SIXMONTHS.toString(), THIS_FINANCIAL_YEAR.toString(),
          LAST_FINANCIAL_YEAR.toString(),
          LAST_5_FINANCIAL_YEARS.toString(), THIS_WEEK.toString(), LAST_WEEK.toString(),
          THIS_BIWEEK.toString(), LAST_BIWEEK.toString(),
          LAST_4_WEEKS.toString(), LAST_4_BIWEEKS.toString(), LAST_12_WEEKS.toString(),
          LAST_52_WEEKS.toString()));
    }
  };

  public static boolean contains(String relativePeriod) {
    return OPTIONS.contains(relativePeriod);
  }
}
