package com.mass3d.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Log
{
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss,SS" );

    protected final ZonedDateTime loggedAt = ZonedDateTime.now();

    protected LogLevel logLevel = LogLevel.INFO;

    protected String message;

    protected Class<?> source;

    protected String username;

    protected Map<String, Object> metadata = new HashMap<>();

    protected LogData data;

    public Log()
    {
    }

    public Log( String message )
    {
        this.message = message;
    }

    @JsonProperty
    public ZonedDateTime getLoggedAt()
    {
        return loggedAt;
    }

    @JsonProperty
    public LogLevel getLogLevel()
    {
        return logLevel;
    }

    public Log setLogLevel( LogLevel logLevel )
    {
        this.logLevel = logLevel;
        return this;
    }

    @JsonProperty
    public Class<?> getSource()
    {
        return source;
    }

    public Log setSource( Class<?> source )
    {
        this.source = source;
        return this;
    }

    @JsonProperty
    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    @JsonProperty
    public String getUsername()
    {
        return username;
    }

    public Log setUsername( String username )
    {
        this.username = username;
        return this;
    }

    @JsonProperty
    public Map<String, Object> getMetadata()
    {
        return metadata;
    }

    public void setMetadata( Map<String, Object> metadata )
    {
        this.metadata = metadata;
    }

    @JsonProperty
    public LogData getData()
    {
        return data;
    }

    public void setData( LogData data )
    {
        this.data = data;
    }

    // ~ Utility methods
    // ========================================================================================================

    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append( "* " ).append( logLevel ).append( " " );
        builder.append( formatter.format( loggedAt ) ).append( " " );
        builder.append( message );

        if ( source != null )
        {
            builder.append( " (" ).append( source.getSimpleName() ).append( ".java)" );
        }

        if ( username != null )
        {
            builder.append( " [" ).append( username ).append( "]" );
        }

        return builder.toString();
    }

    public boolean isFatal()
    {
        return LogLevel.FATAL == logLevel;
    }

    public boolean isError()
    {
        return LogLevel.ERROR == logLevel;
    }

    public boolean isWarn()
    {
        return LogLevel.WARN == logLevel;
    }

    public boolean isInfo()
    {
        return LogLevel.INFO == logLevel;
    }

    public boolean isDebug()
    {
        return LogLevel.DEBUG == logLevel;
    }

    public boolean isTrace()
    {
        return LogLevel.TRACE == logLevel;
    }

    public boolean isEnabled( LogLevel logLevel )
    {
        return logLevel.isEnabled( logLevel );
    }
}
