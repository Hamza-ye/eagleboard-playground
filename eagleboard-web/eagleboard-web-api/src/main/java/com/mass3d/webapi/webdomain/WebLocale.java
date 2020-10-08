package com.mass3d.webapi.webdomain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Locale;

@JacksonXmlRootElement( localName = "locale" )
public class WebLocale
{
    private String locale;
    
    private String name;
    
    public static WebLocale fromLocale( Locale locale )
    {
        WebLocale loc = new WebLocale();
        
        loc.setLocale( locale.toString() );
        loc.setName( locale.getDisplayName() );
        
        return loc;
    }
    
    @JsonProperty
    @JacksonXmlProperty
    public String getLocale()
    {
        return locale;
    }

    public void setLocale( String locale )
    {
        this.locale = locale;
    }

    @JsonProperty
    @JacksonXmlProperty
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
}
