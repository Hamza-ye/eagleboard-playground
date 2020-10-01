package com.mass3d.logging;

public enum LogLevel
{
    FATAL( 0 ), ERROR( 1 ), WARN( 2 ), INFO( 3 ), DEBUG( 4 ), TRACE( 5 );

    private final int level;

    LogLevel( int level )
    {
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    public boolean isEnabled( LogLevel logLevel )
    {
        return level >= logLevel.getLevel();
    }
}
