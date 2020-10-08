package com.mass3d.webapi.mvc.messageconverter;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import com.mass3d.node.NodeService;
import com.mass3d.node.types.RootNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

@Component
public class PdfMessageConverter extends AbstractHttpMessageConverter<RootNode>
{
    public static final ImmutableList<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "pdf" ) )
        .build();

    @Autowired
    private NodeService nodeService;

    public PdfMessageConverter()
    {
        setSupportedMediaTypes( SUPPORTED_MEDIA_TYPES );
    }

    @Override
    protected boolean supports( Class<?> clazz )
    {
        return RootNode.class.equals( clazz );
    }

    @Override
    protected boolean canRead( MediaType mediaType )
    {
        return false;
    }

    @Override
    protected RootNode readInternal( Class<? extends RootNode> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException
    {
        return null;
    }

    @Override
    protected void writeInternal( RootNode rootNode, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException
    {
        nodeService.serialize( rootNode, "application/pdf", outputMessage.getBody() );
    }
}
