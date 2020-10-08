package com.mass3d.scheduling;

import com.mass3d.feedback.ErrorReport;
import java.io.Serializable;

/**
 * Interface for job specific parameters. Serializable so that we can store the object in the
 * database.
 *
 */
public interface JobParameters
    extends Serializable {

  ErrorReport validate();
}
