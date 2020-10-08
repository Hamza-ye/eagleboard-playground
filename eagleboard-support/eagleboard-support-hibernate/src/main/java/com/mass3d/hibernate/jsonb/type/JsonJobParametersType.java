package com.mass3d.hibernate.jsonb.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class JsonJobParametersType extends JsonBinaryType
{
    @Override
    public void setParameterValues( Properties parameters )
    {
        final String clazz = (String) parameters.get( "clazz" );

        if ( clazz == null )
        {
            throw new IllegalArgumentException(
                String.format( "Required parameter '%s' is not configured", "clazz" ) );
        }

        try
        {
            init( classForName( clazz ) );
        }
        catch ( ClassNotFoundException e )
        {
            throw new IllegalArgumentException( "Class: " + clazz + " is not a known class type." );
        }
    }

    @Override
    protected void init( Class klass )
    {
        ObjectMapper MAPPER = new ObjectMapper();
        MAPPER.enableDefaultTyping();
        MAPPER.setSerializationInclusion( JsonInclude.Include.NON_NULL );

        returnedClass = klass;
        reader = MAPPER.readerFor( klass );
        writer = MAPPER.writerFor( klass );
    }
}
