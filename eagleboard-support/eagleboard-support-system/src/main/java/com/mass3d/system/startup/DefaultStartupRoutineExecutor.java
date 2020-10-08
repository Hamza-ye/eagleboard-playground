package com.mass3d.system.startup;

import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.external.conf.DhisConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default implementation of StartupRoutineExecutor. The execute method will execute the added
 * StartupRoutines ordered by their run levels. Startup routines can be ignored from the command
 * line by appending the below.
 *
 * <code>-Ddhis.skip.startup=true</code>
 *
 */
public class DefaultStartupRoutineExecutor
    implements StartupRoutineExecutor {

  private static final Log log = LogFactory.getLog(DefaultStartupRoutineExecutor.class);

  private static final String TRUE = "true";
  private static final String SKIP_PROP = "dhis.skip.startup";

  @Autowired
  private DhisConfigurationProvider config;

  @Autowired(required = false)
  private List<StartupRoutine> startupRoutines;

  // -------------------------------------------------------------------------
  // Execute
  // -------------------------------------------------------------------------

  @Override
  public void execute()
      throws Exception {
    execute(false);
  }

  @Override
  public void executeForTesting()
      throws Exception {
    execute(true);
  }

  private void execute(boolean testing)
      throws Exception {
    if (startupRoutines == null || startupRoutines.isEmpty()) {
      log.debug("No startup routines found");
      return;
    }

    if (TRUE.equalsIgnoreCase(System.getProperty(SKIP_PROP))) {
      log.info("Skipping startup routines, system property " + SKIP_PROP + " is true");
      return;
    }

    if (config.isReadOnlyMode()) {
      log.info("Skipping startup routines, read-only mode is enabled");
      return;
    }

    Collections.sort(startupRoutines, new StartupRoutineComparator());

    int total = startupRoutines.size();
    int index = 1;

    for (StartupRoutine routine : startupRoutines) {
      if (!(testing && routine.skipInTests())) {
        log.info("Executing startup routine [" + index + " of " + total + ", runlevel " + routine
            .getRunlevel()
            + "]: " + routine.getName());

        routine.execute();

        ++index;
      }
    }

    log.info("All startup routines done");
  }
}
