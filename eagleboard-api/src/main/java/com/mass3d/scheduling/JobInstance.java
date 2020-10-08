package com.mass3d.scheduling;

import com.mass3d.leader.election.LeaderManager;
import com.mass3d.message.MessageService;

/**
 * This interface is an abstraction for the actual execution of jobs based on a job configuration.
 *
 */
public interface JobInstance {

  /**
   * This method will try to execute the actual job. It will verify a set of parameters, such as no
   * other jobs of the same JobType is running.
   * <p>
   * If the JobConfiguration is disabled it will not run.
   *
   * @param jobConfiguration the configuration of the job
   * @param schedulingManager manager of scheduling
   */
  void execute(JobConfiguration jobConfiguration, SchedulingManager schedulingManager,
      MessageService messageService, LeaderManager leaderManager)
      throws Exception;
}
