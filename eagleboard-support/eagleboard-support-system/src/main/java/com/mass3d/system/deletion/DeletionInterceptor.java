package com.mass3d.system.deletion;

import org.aspectj.lang.JoinPoint;

/**
 * @version $Id$
 */
public class DeletionInterceptor {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private DeletionManager deletionManager;

  public void setDeletionManager(DeletionManager deletionManager) {
    this.deletionManager = deletionManager;
  }

  public void intercept(JoinPoint joinPoint) {
    if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
      deletionManager.execute(joinPoint.getArgs()[0]);
    }
  }
}
