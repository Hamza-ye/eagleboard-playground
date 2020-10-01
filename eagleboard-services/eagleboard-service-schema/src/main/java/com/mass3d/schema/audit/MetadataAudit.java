package com.mass3d.schema.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import java.util.Date;
import com.mass3d.common.AuditType;
import com.mass3d.common.DxfNamespaces;

@JacksonXmlRootElement( localName = "metadataAudit", namespace = DxfNamespaces.DXF_2_0 )
public class MetadataAudit
{
    private int id;

    private Date createdAt = new Date();

    private String createdBy;

    private String klass;

    private String uid;

    private String code;

    private AuditType type;

    private String value;

    public MetadataAudit()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt )
    {
        this.createdAt = createdAt;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getKlass()
    {
        return klass;
    }

    public void setKlass( String klass )
    {
        this.klass = klass;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUid()
    {
        return uid;
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public AuditType getType()
    {
        return type;
    }

    public void setType( AuditType type )
    {
        this.type = type;
    }

    @JsonProperty
    @JsonRawValue
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "createdAt", createdAt )
            .add( "createdBy", createdBy )
            .add( "uid", uid )
            .add( "code", code )
            .add( "type", type )
            .add( "value", value )
            .toString();
    }
}
