package com.mass3d.system.jep;

import java.util.List;
import java.util.Stack;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.function.PostfixMathCommandI;

public class ArithmeticMean
    extends PostfixMathCommand
    implements PostfixMathCommandI {

  public ArithmeticMean() {
    numberOfParameters = 1;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void run(Stack inStack)
      throws ParseException {
    checkStack(inStack);

    Object param = inStack.pop();
    List<Double> vals = CustomFunctions.checkVector(param);
    int n = vals.size();

    if (n == 0) {
      inStack.push(0);
      return;
    }

    double sum = 0;

    for (Double v : vals) {
      sum = sum + v;
    }

    inStack.push(new Double(sum / n));
  }
}
