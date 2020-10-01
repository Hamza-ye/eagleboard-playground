package com.mass3d.logging;

import org.springframework.context.ApplicationEvent;

public class LogEvent extends ApplicationEvent
{
    private final Log log;
    private final LoggingConfig config;

    public LogEvent( Object source, Log log, LoggingConfig config )
    {
        super( source );
        this.log = log;
        this.config = config;
    }

    public Log getLog()
    {
        return log;
    }

    public LoggingConfig getConfig()
    {
        return config;
    }
}
