package com.mass3d.dxf2.common;

import java.io.IOException;
import com.mass3d.dxf2.importsummary.ImportSummary;
import com.mass3d.render.DefaultRenderService;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

/**
 * Converts a response into an ImportSummary instance.
 *
 * @throws IOException if the response status code is different
 * from 200 OK or 201 Created.
 * @throws IOException if converting the response into an ImportSummary failed.
 */
public class ImportSummaryResponseExtractor
    implements ResponseExtractor<ImportSummary>
{
    @Override
    public ImportSummary extractData( ClientHttpResponse response ) throws IOException
    {
        return DefaultRenderService.getJsonMapper().readValue( response.getBody(), ImportSummary.class );
    }
}
