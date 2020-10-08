package com.mass3d.webapi.mvc.messageconverter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import com.mass3d.node.NodeService;
import com.mass3d.node.serializers.Jackson2JsonNodeSerializer;
import com.mass3d.node.types.RootNode;
import com.mass3d.webapi.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class JsonPMessageConverter extends AbstractHttpMessageConverter<RootNode>
{
    public static final String DEFAULT_CALLBACK_PARAMETER = "callback";

    public static final ImmutableList<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "javascript" ) )
        .add( new MediaType( "application", "x-javascript" ) )
        .add( new MediaType( "text", "javascript" ) )
        .build();

    @Autowired
    private NodeService nodeService;

    @Autowired
    private ContextService contextService;

    public JsonPMessageConverter()
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
        List<String> callbacks = Lists.newArrayList( contextService.getParameterValues( DEFAULT_CALLBACK_PARAMETER ) );

        String callbackParam;

        if ( callbacks.isEmpty() )
        {
            callbackParam = DEFAULT_CALLBACK_PARAMETER;
        }
        else
        {
            callbackParam = callbacks.get( 0 );
        }

        rootNode.getConfig().getProperties().put( Jackson2JsonNodeSerializer.JSONP_CALLBACK, callbackParam );

        nodeService.serialize( rootNode, "application/json", outputMessage.getBody() );
    }
}
