package com.mass3d.period;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PeriodHierarchy {

  private Map<Integer, Set<Integer>> intersectingPeriods = new HashMap<>();

  private Map<Integer, Set<Integer>> periodsBetween = new HashMap<>();

  public Map<Integer, Set<Integer>> getIntersectingPeriods() {
    return intersectingPeriods;
  }

  public Set<Integer> getIntersectingPeriods(Period period) {
    return new HashSet<>(intersectingPeriods.get(period.getId()));
  }

  public Set<Integer> getIntersectingPeriods(Collection<Period> periods) {
    periods = new HashSet<>(periods);

    Set<Integer> set = new HashSet<>();

    for (Period period : periods) {
      if (intersectingPeriods.containsKey(period.getId())) {
        set.addAll(intersectingPeriods.get(period.getId()));
      }
    }

    return set;
  }

  public Map<Integer, Set<Integer>> getPeriodsBetween() {
    return periodsBetween;
  }

  public Set<Integer> getPeriodsBetween(Period period) {
    return new HashSet<>(periodsBetween.get(period.getId()));
  }

  public Set<Integer> getPeriodsBetween(Collection<Period> periods) {
    periods = new HashSet<>(periods);

    Set<Integer> set = new HashSet<>();

    for (Period period : periods) {
      if (periodsBetween.containsKey(period.getId())) {
        set.addAll(periodsBetween.get(period.getId()));
      }
    }

    return set;
  }
}