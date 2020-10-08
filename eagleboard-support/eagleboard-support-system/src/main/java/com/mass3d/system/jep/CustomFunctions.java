package com.mass3d.system.jep;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

public class CustomFunctions {

  public static final Map<String, PostfixMathCommandI> AGGREGATE_FUNCTIONS =
      ImmutableMap.<String, PostfixMathCommandI>builder().
          put("AVG", new ArithmeticMean()).put("STDDEV", new StandardDeviationPopulation()).
          put("STDDEVPOP", new StandardDeviationPopulation())
          .put("STDDEVSAMP", new StandardDeviationSample()).
          put("PERCENTILECONT", new PercentileContinuous()).
          put("MEDIAN", new MedianValue()).put("MAX", new MaxValue()).
          put("MIN", new MinValue()).put("COUNT", new Count()).
          put("SUM", new VectorSum()).build();

  public static final Pattern AGGREGATE_PATTERN_PREFIX = Pattern.compile(
      "(AVG|STDDEV|STDDEVPOP|STDDEVSAMP|PERCENTILECONT|MEDIAN|MAX|MIN|COUNT|SUM)\\s*\\(",
      CASE_INSENSITIVE);

  public static final Map<String, PostfixMathCommandI> SCALAR_FUNCTIONS =
      ImmutableMap.<String, PostfixMathCommandI>builder().
          put("IF", new If()).build();

  public static final Pattern SCALAR_PATTERN_PREFIX = Pattern
      .compile("(IF)\\s*\\(", CASE_INSENSITIVE);

  public static final Map<String, PostfixMathCommandI> ALL_FUNCTIONS =
      ImmutableMap.<String, PostfixMathCommandI>builder().putAll(AGGREGATE_FUNCTIONS)
          .putAll(SCALAR_FUNCTIONS).build();

  public static void addFunctions(JEP parser) {
    for (Entry<String, PostfixMathCommandI> e : ALL_FUNCTIONS.entrySet()) {
      String fname = e.getKey();
      PostfixMathCommandI cmd = e.getValue();
      parser.addFunction(fname, cmd);
    }
  }

  @SuppressWarnings("unchecked")
  public static List<Double> checkVector(Object param)
      throws ParseException {
    if (param instanceof Double && (Double) param == 0.0) // Indicates empty list
    {
      return new ArrayList<Double>();
    }

    if (param instanceof List) {
      List<?> vals = (List<?>) param;

      for (Object val : vals) {
        if (!(val instanceof Double)) {
          throw new ParseException("Non numeric vector");
        }
      }

      return (List<Double>) param;
    } else {
      throw new ParseException("Invalid vector argument");
    }
  }

  @SuppressWarnings("unchecked")
  public static Double checkDouble(Object param)
      throws ParseException {
    if (!(param instanceof Double)) {
      throw new ParseException("Non numeric vector");
    }

    return (Double) param;
  }
}
