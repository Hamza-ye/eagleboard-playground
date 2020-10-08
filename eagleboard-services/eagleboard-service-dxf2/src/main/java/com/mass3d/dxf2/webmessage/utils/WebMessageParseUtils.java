package com.mass3d.dxf2.webmessage.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import com.mass3d.dxf2.webmessage.WebMessageParseException;
import com.mass3d.render.EmptyStringToNullStdDeserializer;
import com.mass3d.render.ParseDateStdDeserializer;
import com.mass3d.render.WriteDateStdSerializer;

public class WebMessageParseUtils
{
    private final static ObjectMapper JSON_MAPPER = new ObjectMapper();
    private final static ObjectMapper XML_MAPPER = new XmlMapper();


    public static <T> T fromWebMessageResponse( InputStream input, Class<T> klass ) throws WebMessageParseException
    {
        StringWriter writer = new StringWriter();
        try
        {
            IOUtils.copy( input, writer, "UTF-8" );
        }
        catch ( IOException e )
        {
            throw new WebMessageParseException( "Could not read the InputStream" + e.getMessage(), e );
        }
        return parseJson( writer.toString(), klass );
    }

    public static <T> T fromWebMessageResponse( String input, Class<T> klass ) throws WebMessageParseException
    {
        return parseJson( input, klass );
    }

    private static <T> T parseJson( String input, Class<T> klass ) throws WebMessageParseException
    {
        JsonNode objectNode = null;
        try
        {
            objectNode = JSON_MAPPER.readTree( input );
        }
        catch ( IOException e )
        {
            throw new WebMessageParseException( "Invalid JSON String. " + e.getMessage(), e );
        }

        JsonNode responseNode = null;

        if ( objectNode != null )
        {
            responseNode = objectNode.get( "response" );
        }
        else
        {
            throw new WebMessageParseException( "The object node is null. Could not parse the JSON." );
        }
        try
        {
            return JSON_MAPPER.readValue( responseNode.toString(), klass );
        }
        catch ( IOException e )
        {
            throw new WebMessageParseException( "Could not parse the JSON." + e.getMessage(), e );
        }
    }


    static
    {
        SimpleModule module = new SimpleModule();
        module.addDeserializer( String.class, new EmptyStringToNullStdDeserializer() );
        module.addDeserializer( Date.class, new ParseDateStdDeserializer() );
        module.addSerializer( Date.class, new WriteDateStdSerializer() );

        XML_MAPPER.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        XML_MAPPER.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        XML_MAPPER.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
        JSON_MAPPER.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        JSON_MAPPER.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        JSON_MAPPER.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );

        XML_MAPPER.disable( MapperFeature.AUTO_DETECT_FIELDS );
        XML_MAPPER.disable( MapperFeature.AUTO_DETECT_CREATORS );
        XML_MAPPER.disable( MapperFeature.AUTO_DETECT_GETTERS );
        XML_MAPPER.disable( MapperFeature.AUTO_DETECT_SETTERS );
        XML_MAPPER.disable( MapperFeature.AUTO_DETECT_IS_GETTERS );

        JSON_MAPPER.disable( MapperFeature.AUTO_DETECT_FIELDS );
        JSON_MAPPER.disable( MapperFeature.AUTO_DETECT_CREATORS );
        JSON_MAPPER.disable( MapperFeature.AUTO_DETECT_GETTERS );
        JSON_MAPPER.disable( MapperFeature.AUTO_DETECT_SETTERS );
        JSON_MAPPER.disable( MapperFeature.AUTO_DETECT_IS_GETTERS );

        JSON_MAPPER.registerModule( module );
        XML_MAPPER.registerModule( module );
    }
}
