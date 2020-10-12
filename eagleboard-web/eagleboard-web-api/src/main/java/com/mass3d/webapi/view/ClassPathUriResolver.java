package com.mass3d.webapi.view;

import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.io.ClassPathResource;

public class ClassPathUriResolver implements URIResolver
{
    private String templatePath = "/templates/";

    public ClassPathUriResolver()
    {
    }

    public ClassPathUriResolver( String templatePath )
    {
        this.templatePath = templatePath;
    }

    public String getTemplatePath()
    {
        return templatePath;
    }

    public void setTemplatePath( String templatePath )
    {
        this.templatePath = templatePath;
    }

    @Override
    public Source resolve( String href, String base ) throws TransformerException
    {
        String url = getTemplatePath() + href;
        ClassPathResource classPathResource = new ClassPathResource( url );

        if ( !classPathResource.exists() )
        {
            throw new TransformerException( "Resource does not exist in classpath: " + url );
        }

        Source source = null;

        try
        {
            source = new StreamSource( classPathResource.getInputStream() );
        }
        catch ( IOException e )
        {
            throw new TransformerException( "IOException while reading URL: " + url );
        }

        return source;
    }
}
