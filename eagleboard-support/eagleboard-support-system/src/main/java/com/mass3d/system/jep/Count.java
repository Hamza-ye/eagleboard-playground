package com.mass3d.system.jep;

import java.util.List;
import java.util.Stack;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.function.PostfixMathCommandI;

public class Count
    extends PostfixMathCommand
    implements PostfixMathCommandI {

  public Count() {
    numberOfParameters = 1;
  }

  // nFunk's JEP run() method uses the raw Stack type
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void run(Stack inStack)
      throws ParseException {
    checkStack(inStack);

    Object param = inStack.pop();
    List<Double> vals = CustomFunctions.checkVector(param);
    inStack.push(vals.size());
  }
}
