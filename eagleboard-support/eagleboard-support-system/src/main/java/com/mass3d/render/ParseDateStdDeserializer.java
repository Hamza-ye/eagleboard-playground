package com.mass3d.render;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;
import com.mass3d.system.util.DateUtils;

public class ParseDateStdDeserializer extends JsonDeserializer<Date>
{
    @Override
    public Date deserialize( JsonParser parser, DeserializationContext context ) throws IOException
    {
        return DateUtils.parseDate( parser.getValueAsString() );
    }
}
