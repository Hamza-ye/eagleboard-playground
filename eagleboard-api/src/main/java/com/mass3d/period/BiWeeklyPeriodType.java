package com.mass3d.period;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "BiWeeklyPeriodType")
@DiscriminatorValue("BiWeekly")
public class BiWeeklyPeriodType
    extends BiWeeklyAbstractPeriodType {

  public static final String NAME = "BiWeekly";

  public BiWeeklyPeriodType() {
    super(NAME, 1, "yyyyBiWn", "P14D", 14, "BiW");
  }
}
