package com.mass3d.dxf2.sync;

public enum SyncEndpoint
{
    TRACKED_ENTITY_INSTANCES( "/api/trackedEntityInstances" ),
    ENROLLMENTS( "/api/enrollments" ),
    EVENTS( "/api/events" ),
    DATA_VALUE_SETS( "/api/dataValueSets" );

    private String path;

    SyncEndpoint( String path )
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }
}
