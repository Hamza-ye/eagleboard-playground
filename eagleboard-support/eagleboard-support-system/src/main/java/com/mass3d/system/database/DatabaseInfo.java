package com.mass3d.system.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.mass3d.common.DxfNamespaces;

public class DatabaseInfo
{    
    private String name;
    
    private String user;
    
    private String password;
    
    private String url;
    
    private boolean spatialSupport;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DatabaseInfo()
    {   
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void clearSensitiveInfo()
    {
        this.name = null;
        this.user = null;
        this.password = null;
        this.url = null;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUser()
    {
        return user;
    }

    public void setUser( String user )
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isSpatialSupport()
    {
        return spatialSupport;
    }

    public void setSpatialSupport( boolean spatialSupport )
    {
        this.spatialSupport = spatialSupport;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    public String toString()
    {
        return "[Name: " + name + ", User: " + user + ", Password: " + password +
            ", URL: " + url + "]";
    }
}
