package com.mass3d.indicator;

import java.util.List;

/**
 * @version $Id$
 */
public interface IndicatorService {

  String ID = IndicatorService.class.getName();

  // -------------------------------------------------------------------------
  // Indicator
  // -------------------------------------------------------------------------

  int addIndicator(Indicator indicator);

  void updateIndicator(Indicator indicator);

  void deleteIndicator(Indicator indicator);

  Indicator getIndicator(int id);

  Indicator getIndicator(String uid);

  List<Indicator> getAllIndicators();

  List<Indicator> getIndicatorsWithGroupSets();

  List<Indicator> getIndicatorsWithoutGroups();

  List<Indicator> getIndicatorsWithDataSets();

  // -------------------------------------------------------------------------
  // IndicatorType
  // -------------------------------------------------------------------------

  int addIndicatorType(IndicatorType indicatorType);

  void updateIndicatorType(IndicatorType indicatorType);

  void deleteIndicatorType(IndicatorType indicatorType);

  IndicatorType getIndicatorType(int id);

  IndicatorType getIndicatorType(String uid);

  List<IndicatorType> getAllIndicatorTypes();

  // -------------------------------------------------------------------------
  // IndicatorGroup
  // -------------------------------------------------------------------------

  int addIndicatorGroup(IndicatorGroup indicatorGroup);

  void updateIndicatorGroup(IndicatorGroup indicatorGroup);

  void deleteIndicatorGroup(IndicatorGroup indicatorGroup);

  IndicatorGroup getIndicatorGroup(int id);

  IndicatorGroup getIndicatorGroup(String uid);

  List<IndicatorGroup> getAllIndicatorGroups();

  // -------------------------------------------------------------------------
  // IndicatorGroupSet
  // -------------------------------------------------------------------------

  int addIndicatorGroupSet(IndicatorGroupSet groupSet);

  void updateIndicatorGroupSet(IndicatorGroupSet groupSet);

  void deleteIndicatorGroupSet(IndicatorGroupSet groupSet);

  IndicatorGroupSet getIndicatorGroupSet(int id);

  IndicatorGroupSet getIndicatorGroupSet(String uid);

  List<IndicatorGroupSet> getAllIndicatorGroupSets();
}
