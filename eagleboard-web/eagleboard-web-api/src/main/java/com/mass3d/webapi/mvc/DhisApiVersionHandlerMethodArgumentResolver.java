package com.mass3d.webapi.mvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import com.mass3d.common.EagleboardApiVersion;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class DhisApiVersionHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private Pattern API_VERSION_PATTERN = Pattern.compile( "/api/(?<version>[0-9]{2})/" );

    @Override
    public boolean supportsParameter( MethodParameter parameter )
    {
        return EagleboardApiVersion.class.isAssignableFrom( parameter.getParameterType() );
    }

    @Override
    public Object resolveArgument( MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory ) throws Exception
    {
        String requestURI = ((HttpServletRequest) webRequest.getNativeRequest()).getRequestURI();
        Matcher matcher = API_VERSION_PATTERN.matcher( requestURI );

        if ( matcher.find() )
        {
            Integer version = Integer.valueOf( matcher.group( "version" ) );
            return EagleboardApiVersion.getVersion( version );
        }

        return EagleboardApiVersion.DEFAULT;
    }
}
