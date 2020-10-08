package com.mass3d.webapi.mvc.interceptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mass3d.logging.Log;
import com.mass3d.logging.LogLevel;

public class WebRequestLog extends Log
{
    private final long requestTime;
    private final String url;

    public WebRequestLog( LogLevel level, long requestTime, String url )
    {
        this.logLevel = level;
        this.requestTime = requestTime;
        this.url = url;
    }

    @JsonProperty
    public long getRequestTime()
    {
        return requestTime;
    }

    @JsonProperty
    public String getUrl()
    {
        return url;
    }
}
