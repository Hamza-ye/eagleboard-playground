package com.mass3d.api.common;

import com.mass3d.api.interpretation.Interpretation;
import java.util.Set;

public interface InterpretableObject
    extends IdentifiableObject {

  Set<Interpretation> getInterpretations();
}
