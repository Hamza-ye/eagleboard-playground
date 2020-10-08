package com.mass3d.expression;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.io.Serializable;
import com.mass3d.common.DxfNamespaces;

/**
 * An Expression is the expression of e.g. a validation rule. It consist of a String representation
 * of the rule as well as references to the data elements and category option combos included in the
 * expression.
 * <p/>
 * The expression can contain numbers and mathematical operators and contain references to data
 * elements and category option combos on the form:
 * <p/>
 * i) [1.2] where 1 refers to the data element identifier and 2 refers to the category option combo
 * identifier.
 * <p/>
 * ii) [1] where 1 refers to the data element identifier, in this case the formula represents the
 * total value for all category option combos for that data element.
 *
 * @version $Id: Expression.java 5011 2008-04-24 20:41:28Z larshelg $
 */
@JacksonXmlRootElement(localName = "expression", namespace = DxfNamespaces.DXF_2_0)
public class Expression
    implements Serializable {

  public static final String SEPARATOR = ".";
  public static final String EXP_OPEN = "#{";
  public static final String EXP_CLOSE = "}";
  public static final String PAR_OPEN = "(";
  public static final String PAR_CLOSE = ")";
  /**
   * Determines if a de-serialized file is compatible with this class.
   */
  private static final long serialVersionUID = -4868682510629094282L;
  /**
   * The unique identifier for this Expression.
   */
  private int id;

  /**
   * The Expression.
   */
  private String expression;

  /**
   * A description of the Expression.
   */
  private String description;

  /**
   * This expression should be given sliding window based data
   */
  private Boolean slidingWindow = false;

  /**
   * Indicates whether the expression should evaluate to null if all or any data values are missing
   * in the expression.
   */
  private MissingValueStrategy missingValueStrategy = MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  /**
   * Default empty Expression
   */
  public Expression() {
  }

  /**
   * @param expression The expression as a String
   * @param description A description of the Expression.
   */
  public Expression(String expression, String description) {
    this.expression = expression;
    this.description = description;
  }

  /**
   * Constructor with all the parameters.
   *
   * @param expression The expression as a String
   * @param description A description of the Expression.
   * @param missingValueStrategy Strategy for handling missing values.
   */
  public Expression(String expression, String description,
      MissingValueStrategy missingValueStrategy) {
    this.expression = expression;
    this.description = description;
    this.missingValueStrategy = missingValueStrategy;
  }

  // -------------------------------------------------------------------------
  // Equals and hashCode
  // -------------------------------------------------------------------------

  public static int matchExpression(String s, int start) {
    int i = start, depth = 0, len = s.length();

    while (i < len) {
      char c = s.charAt(i);

      if ((c == ')') || (c == ']')) {
        if (depth == 0) {
          return i;
        } else {
          depth--;
        }
      } else if ((c == '(') || (c == '[')) {
        depth++;
      } else if (c == ',') {
        if (depth == 0) {
          return i;
        }
      } else {
      }

      i++;
    }

    return -1;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    final Expression other = (Expression) obj;

    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }

    if (expression == null) {
      return other.expression == null;
    } else {
      return expression.equals(other.expression);
    }

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((expression == null) ? 0 : expression.hashCode());

    return result;
  }

  @Override
  public String toString() {
    return "{" +
        "\"class\":\"" + getClass() + "\", " +
        "\"id\":\"" + id + "\", " +
        "\"expression\":\"" + expression + "\", " +
        "\"description\":\"" + description + "\" " +
        "}";
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public MissingValueStrategy getMissingValueStrategy() {
    return missingValueStrategy;
  }

  public void setMissingValueStrategy(MissingValueStrategy missingValueStrategy) {
    this.missingValueStrategy = missingValueStrategy;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public Boolean getSlidingWindow() {
    return slidingWindow;
  }

  public void setSlidingWindow(Boolean slidingWindow) {
    this.slidingWindow = slidingWindow;
  }
}
