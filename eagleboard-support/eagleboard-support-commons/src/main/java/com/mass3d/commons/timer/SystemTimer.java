package com.mass3d.commons.timer;

import java.util.concurrent.TimeUnit;

/**
 * Timer implementation which uses system time.
 *
 */
public class SystemTimer
    implements Timer
{
    private long internalStart = 0;

    private Long internalEnd;

    @Override
    public Timer start()
    {
        internalStart = System.nanoTime();
        return this;
    }

    @Override
    public Timer stop()
    {
        internalEnd = System.nanoTime();
        return this;
    }

    @Override
    public Long duration()
    {
        return internalEnd != null ? internalEnd - internalStart : System.nanoTime() - internalStart;
    }

    @Override
    public String toString()
    {
        double seconds = TimeUnit.MILLISECONDS.convert( duration(), TimeUnit.NANOSECONDS ) / 1000.0f;
        return String.format( "%.2f seconds", seconds );
    }
}
