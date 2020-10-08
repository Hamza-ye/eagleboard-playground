package com.mass3d.common;

import com.mass3d.constant.Constant;
import com.mass3d.activity.Activity;
import com.mass3d.dataelement.DataElement;
import com.mass3d.dataset.DataSet;
import com.mass3d.project.Project;
import com.mass3d.todotask.TodoTask;
import com.mass3d.indicator.Indicator;
import com.mass3d.indicator.IndicatorGroup;
import com.mass3d.indicator.IndicatorGroupSet;
import com.mass3d.indicator.IndicatorType;
import com.mass3d.period.Period;
import com.mass3d.user.User;
import com.mass3d.user.UserGroup;

/**
 * @version $Id$
 */
public enum Objects {
  CONSTANT("constant", Constant.class),

  // Eagle Board modifications
  DATAELEMENT("dataElement", DataElement.class),
  DATASET("dataSet", DataSet.class),
  TODOTASK("todoTask", TodoTask.class),
  ACTIVITY("activity", Activity.class),
  PROJECT("project", Project.class),

  //    DATAELEMENT( "dataElement", DataElement.class ),
//    EXTENDEDDATAELEMENT( "extendedDataElement", DataElement.class ),
//    DATAELEMENTGROUP( "dataElementGroup", DataElementGroup.class ),
//    DATAELEMENTGROUPSET( "dataElementGroupSet", DataElementGroupSet.class ),
    INDICATORTYPE( "indicatorType", IndicatorType.class ),
    INDICATOR( "indicator", Indicator.class ),
    INDICATORGROUP( "indicatorGroup", IndicatorGroup.class ),
    INDICATORGROUPSET( "indicatorGroupSet", IndicatorGroupSet.class ),
//    DATASET( "dataSet", DataSet.class ),
//    ORGANISATIONUNIT( "organisationUnit", OrganisationUnit.class ),
//    ORGANISATIONUNITGROUP( "organisationUnitGroup", OrganisationUnitGroup.class ),
//    ORGANISATIONUNITGROUPSET( "organisationUnitGroupSet", OrganisationUnitGroupSet.class ),
//    ORGANISATIONUNITLEVEL( "organisationUnitLevel", OrganisationUnitLevel.class ),
//    VALIDATIONRULE( "validationRule", ValidationRule.class ),
    PERIOD( "period", Period.class ),
//    DATAVALUE( "dataValue", DataValue.class ),
  USER("user", User.class),
  USERGROUP("userGroup", UserGroup.class);
//    REPORTTABLE( "reportTable", ReportTable.class ),
//    REPORT( "report", Report.class ),
//    CHART( "chart", Chart.class ),
//    MAP( "map", Map.class ),
//    DASHBOARD( "dashboard", Dashboard.class ),
//    PROGRAM( "program", Program.class ),
//    PROGRAMSTAGEINSTANCE( "programStageInstance", ProgramStageInstance.class ),
//    TRACKEDENTITYATTRIBUTE( "trackedEntityAttribute", TrackedEntityAttribute.class );

  private String value;

  private Class<?> clazz;

  Objects(String value, Class<?> clazz) {
    this.value = value;
    this.clazz = clazz;
  }

  public static Objects fromClass(Class<?> clazz)
      throws IllegalAccessException {
    if (clazz == null) {
      throw new NullPointerException();
    }

    for (Objects obj : Objects.values()) {
      if (obj.clazz.equals(clazz)) {
        return obj;
      }
    }

    throw new IllegalAccessException(
        "No item found in enum Objects for class '" + clazz.getSimpleName() + "'. ");
  }

  public String getValue() {
    return value;
  }

}
