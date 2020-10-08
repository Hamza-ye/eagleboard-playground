package com.mass3d.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import com.mass3d.common.DxfNamespaces;

@JacksonXmlRootElement( localName = "rabbitmq", namespace = DxfNamespaces.DXF_2_0 )
public class RabbitMQ
{
    private String addresses;

    private String host;

    private String virtualHost;

    private Integer port;

    private String exchange;

    private String username;

    private String password;

    private int connectionTimeout;

    public RabbitMQ()
    {
    }

    public RabbitMQ( String host, Integer port, String username, String password )
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getAddresses()
    {
        return addresses;
    }

    public RabbitMQ setAddresses( String addresses )
    {
        this.addresses = addresses;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getHost()
    {
        return host;
    }

    public RabbitMQ setHost( String host )
    {
        this.host = host;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getVirtualHost()
    {
        return virtualHost;
    }

    public RabbitMQ setVirtualHost( String virtualHost )
    {
        this.virtualHost = virtualHost;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getPort()
    {
        return port;
    }

    public RabbitMQ setPort( Integer port )
    {
        this.port = port;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getExchange()
    {
        return exchange;
    }

    public RabbitMQ setExchange( String exchange )
    {
        this.exchange = exchange;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUsername()
    {
        return username;
    }

    public RabbitMQ setUsername( String username )
    {
        this.username = username;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getPassword()
    {
        return password;
    }

    public RabbitMQ setPassword( String password )
    {
        this.password = password;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public RabbitMQ setConnectionTimeout( int connectionTimeout )
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public boolean isValid()
    {
        return host != null;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "addresses", addresses )
            .add( "host", host )
            .add( "virtualHost", virtualHost )
            .add( "port", port )
            .add( "username", username )
            .add( "password", password )
            .add( "connectionTimeout", connectionTimeout )
            .toString();
    }
}