package com.mass3d.system.startup;

/**
 * Convenience class for creating startup routines. Contains a setter for the runlevel property
 * which should be used in bean mappings.
 *
 */
public abstract class AbstractStartupRoutine
    implements StartupRoutine {

  private String name = this.getClass().getSimpleName();
  private int runlevel = 0;
  private boolean skipInTests = false;

  public void setSkipInTests(boolean skipInTests) {
    this.skipInTests = skipInTests;
  }

  @Override
  public int getRunlevel() {
    return runlevel;
  }

  public void setRunlevel(int runlevel) {
    this.runlevel = runlevel;
  }

  // -------------------------------------------------------------------------
  // StartupRoutine implementation
  // -------------------------------------------------------------------------

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean skipInTests() {
    return skipInTests;
  }
}
