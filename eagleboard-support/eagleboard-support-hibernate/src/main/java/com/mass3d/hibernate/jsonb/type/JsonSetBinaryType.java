package com.mass3d.hibernate.jsonb.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;

public class JsonSetBinaryType
    extends JsonBinaryType
{
    static final ObjectMapper MAPPER = new ObjectMapper();

    static
    {
        MAPPER.setSerializationInclusion( JsonInclude.Include.NON_NULL );
    }

    @Override
    protected String convertObjectToJson( Object value )
    {
        try
        {
            return MAPPER.writeValueAsString( value );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    protected Object convertJsonToObject( String content )
    {
        try
        {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType( Set.class, returnedClass() );

            return MAPPER.readValue( content, type );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
