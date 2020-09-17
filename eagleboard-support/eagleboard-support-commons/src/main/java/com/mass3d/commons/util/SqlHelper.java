package com.mass3d.commons.util;

public class SqlHelper {

  private boolean includeSpaces = false;

  private boolean whereInvoked = false;

  private boolean havingInvoked = false;

  private boolean orInvoked = false;

  private boolean betweenInvoked = false;

  public SqlHelper() {
  }

  public SqlHelper(boolean includeSpaces) {
    this.includeSpaces = includeSpaces;
  }

  /**
   * Returns "where" the first time it is invoked, then "and" for subsequent invocations.
   *
   * @return "where" or "and".
   */
  public String whereAnd() {
    String str = whereInvoked ? "and" : "where";

    whereInvoked = true;

    return includeSpaces ? " " + str + " " : str;
  }

  /**
   * Returns "having" the first time it is invoked, then "and" for subsequent invocations.
   *
   * @return "having" or "and".
   */
  public String havingAnd() {
    String str = havingInvoked ? "and" : "having";

    havingInvoked = true;

    return includeSpaces ? " " + str + " " : str;
  }

  /**
   * Returns the empty string the first time it is invoked, then "or" for subsequent invocations.
   *
   * @return empty string or "or".
   */
  public String or() {
    String str = orInvoked ? "or" : "";

    orInvoked = true;

    return includeSpaces ? " " + str + " " : str;
  }

  /**
   * Returns the empty string the first time it is invoked, then "or" for subsequent invocations.
   *
   * @return empty or "or".
   */
  public String betweenAnd() {
    String str = betweenInvoked ? "and" : "between";

    betweenInvoked = true;

    return includeSpaces ? " " + str + " " : str;
  }
}
