package com.mass3d.api.interpretation;

import com.mass3d.api.user.User;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface InterpretationService {

  int saveInterpretation(Interpretation interpretation);

  Interpretation getInterpretation(int id);

  Interpretation getInterpretation(String uid);

  void updateInterpretation(Interpretation interpretation);

  void updateInterpretationText(Interpretation interpretation, String text);

  void deleteInterpretation(Interpretation interpretation);

  List<Interpretation> getInterpretations();

  List<Interpretation> getInterpretations(Date lastUpdated);

  List<Interpretation> getInterpretations(int first, int max);

//  InterpretationComment addInterpretationComment(String uid, String text);

  void updateComment(Interpretation interpretation, InterpretationComment comment);

  void updateSharingForMentions(Interpretation interpretation, Set<User> users);

  void updateCurrentUserLastChecked();

  long getNewInterpretationCount();

  /**
   * Adds a like to the given interpretation for the current user. This method will have a
   * "repeatable read" transaction isolation level to ensure an atomic increment of the like count
   * interpretation property.
   *
   * @param id the interpretation id.
   * @return true if the current user had not already liked the interpretation.
   */
  boolean likeInterpretation(int id);

  /**
   * Removes a like from the given interpretation for the current user. This method will have a
   * "repeatable read" transaction isolation level to ensure an atomic decrease of the like count
   * interpretation property.
   *
   * @param id the interpretation id.
   * @return true if the current user had previously liked the interpretation.
   */
  boolean unlikeInterpretation(int id);

//    int countMapInterpretations(Map map);
//
//    int countChartInterpretations(Chart chart);
//
//    int countReportTableInterpretations(ReportTable reportTable);

//  Interpretation getInterpretationByChart(int id);
}
