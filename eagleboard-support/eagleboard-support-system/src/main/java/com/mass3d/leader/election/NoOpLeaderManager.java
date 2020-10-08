package com.mass3d.leader.election;

import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.scheduling.SchedulingManager;

/**
 * No operation leader election implementation which will be used when redis is not configured.
 * 
 */
public class NoOpLeaderManager implements LeaderManager
{

    private static final Log log = LogFactory.getLog( NoOpLeaderManager.class );

    public NoOpLeaderManager()
    {
        String nodeId = UUID.randomUUID().toString();
        log.info( "Setting up noop leader manager using dummy NodeId:" + nodeId );
    }

    @Override
    public void renewLeader()
    {
        //No operation
    }

    @Override
    public void electLeader()
    {
      //No operation
    }

    @Override
    public boolean isLeader()
    {
        return true;
    }

    @Override
    public void setSchedulingManager( SchedulingManager schedulingManager )
    {
      //No operation
    }

}