package com.mass3d.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.DxfNamespaces;

@JacksonXmlRootElement( localName = "metadataAudit", namespace = DxfNamespaces.DXF_2_0 )
public class MetadataAudit
{
    private boolean persist;

    private boolean log;

    public MetadataAudit()
    {
    }

    public MetadataAudit( boolean persist, boolean log )
    {
        this.persist = persist;
        this.log = log;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isPersist()
    {
        return persist;
    }

    public void setPersist( boolean persist )
    {
        this.persist = persist;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isLog()
    {
        return log;
    }

    public void setLog( boolean log )
    {
        this.log = log;
    }

    public boolean isAudit()
    {
        return log || persist;
    }
}
