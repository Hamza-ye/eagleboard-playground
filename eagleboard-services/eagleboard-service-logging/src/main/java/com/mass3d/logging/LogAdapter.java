package com.mass3d.logging;

import org.springframework.context.ApplicationListener;

public interface LogAdapter extends ApplicationListener<LogEvent>
{
    @Override
    default void onApplicationEvent(LogEvent event)
    {
        if ( isEnabled( event ) )
        {
            log( event.getLog(), event.getConfig() );
        }
    }

    default boolean isEnabled(LogEvent event)
    {
        if ( event == null || event.getLog() == null )
        {
            return false;
        }

        LoggingConfig config = event.getConfig();

        return config.getLevel().isEnabled( event.getLog().getLogLevel() );
    }

    default void log(Log log, LoggingConfig config)
    {
        log( log );
    }

    default void log(Log log)
    {
    }
}
