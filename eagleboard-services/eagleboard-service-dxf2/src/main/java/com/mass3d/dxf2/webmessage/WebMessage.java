package com.mass3d.dxf2.webmessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.feedback.Status;
import org.springframework.http.HttpStatus;

@JacksonXmlRootElement( localName = "webMessage", namespace = DxfNamespaces.DXF_2_0 )
@JsonPropertyOrder( {
    "httpStatus", "httpStatusCode", "status", "code", "message", "devMessage", "response"
} )
public class WebMessage
{
    /**
     * Message status, currently two statuses are available: OK, ERROR. Default
     * value is OK.
     *
     * @see Status
     */
    protected Status status = Status.OK;

    /**
     * Internal code for this message. Should be used to help with third party clients which
     * should not have to resort to string parsing of message to know what is happening.
     */
    protected Integer code;

    /**
     * HTTP status.
     */
    protected HttpStatus httpStatus = HttpStatus.OK;

    /**
     * Non-technical message, should be simple and could possibly be used to display message
     * to an end-user.
     */
    protected String message;

    /**
     * Technical message that should explain as much details as possible, mainly to be used
     * for debugging.
     */
    protected String devMessage;

    /**
     * When a simple text feedback is not enough, you can use this interface to implement your
     * own message responses.
     *
     * @see WebMessageResponse
     */
    protected WebMessageResponse response;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------     

    public WebMessage()
    {
    }

    public WebMessage( Status status )
    {
        this.status = status;
    }

    public WebMessage( Status status, HttpStatus httpStatus )
    {
        this.status = status;
        this.httpStatus = httpStatus;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------     

    public boolean isOk()
    {
        return Status.OK == status;
    }

    public boolean isWarning()
    {
        return Status.WARNING == status;
    }

    public boolean isError()
    {
        return Status.ERROR == status;
    }

    // -------------------------------------------------------------------------
    // Get and set methods
    // -------------------------------------------------------------------------     

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Status getStatus()
    {
        return status;
    }

    public void setStatus( Status status )
    {
        this.status = status;
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Integer getCode()
    {
        return code;
    }

    public void setCode( Integer code )
    {
        this.code = code;
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public String getHttpStatus()
    {
        return httpStatus.getReasonPhrase();
    }

    public void setHttpStatus( HttpStatus httpStatus )
    {
        this.httpStatus = httpStatus;
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Integer getHttpStatusCode()
    {
        return httpStatus.value();
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDevMessage()
    {
        return devMessage;
    }

    public void setDevMessage( String devMessage )
    {
        this.devMessage = devMessage;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public WebMessageResponse getResponse()
    {
        return response;
    }

    public void setResponse( WebMessageResponse response )
    {
        this.response = response;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "status", status )
            .add( "code", code )
            .add( "httpStatus", httpStatus )
            .add( "message", message )
            .add( "devMessage", devMessage )
            .add( "response", response )
            .toString();
    }
}
