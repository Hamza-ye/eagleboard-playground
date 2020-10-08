package com.mass3d.webapi.mvc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.mass3d.common.EagleboardApiVersion;
import com.mass3d.webapi.mvc.annotation.ApiVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class CustomRequestMappingHandlerMapping
    extends RequestMappingHandlerMapping
{
    @Override
    protected RequestMappingInfo getMappingForMethod( Method method, Class<?> handlerType )
    {
        RequestMappingInfo info = super.getMappingForMethod( method, handlerType );

        if ( info == null )
        {
            return null;
        }

        ApiVersion typeApiVersion = AnnotationUtils.findAnnotation( handlerType, ApiVersion.class );
        ApiVersion methodApiVersion = AnnotationUtils.findAnnotation( method, ApiVersion.class );

        if ( typeApiVersion == null && methodApiVersion == null )
        {
            return info;
        }

        RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();

        if ( methodsCondition.getMethods().isEmpty() )
        {
            methodsCondition = new RequestMethodsRequestCondition( RequestMethod.GET );
        }

        Set<String> rqmPatterns = info.getPatternsCondition().getPatterns();
        Set<String> patterns = new HashSet<>();

        Set<EagleboardApiVersion> versions = getVersions( typeApiVersion, methodApiVersion );

        for ( String pattern : rqmPatterns )
        {
            versions.stream()
                .filter( version -> !version.isIgnore() )
                .forEach( version ->
                {
                    if ( !pattern.startsWith( version.getVersionString() ) )
                    {
                        if ( pattern.startsWith( "/" ) ) patterns.add( "/" + version.getVersion() + pattern );
                        else patterns.add( "/" + version.getVersion() + "/" + pattern );
                    }
                    else
                    {
                        patterns.add( pattern );
                    }
                } );
        }

        PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition(
            patterns.toArray( new String[]{} ), null, null, true, true, null );

        return new RequestMappingInfo(
            null, patternsRequestCondition, methodsCondition, info.getParamsCondition(), info.getHeadersCondition(), info.getConsumesCondition(),
            info.getProducesCondition(), info.getCustomCondition()
        );
    }

    private Set<EagleboardApiVersion> getVersions( ApiVersion typeApiVersion, ApiVersion methodApiVersion )
    {
        Set<EagleboardApiVersion> includes = new HashSet<>();
        Set<EagleboardApiVersion> excludes = new HashSet<>();

        if ( typeApiVersion != null )
        {
            includes.addAll( Arrays.asList( typeApiVersion.include() ) );
            excludes.addAll( Arrays.asList( typeApiVersion.exclude() ) );
        }

        if ( methodApiVersion != null )
        {
            includes.addAll( Arrays.asList( methodApiVersion.include() ) );
            excludes.addAll( Arrays.asList( methodApiVersion.exclude() ) );
        }

        if ( includes.contains( EagleboardApiVersion.ALL ) )
        {
            boolean includeDefault = includes.contains( EagleboardApiVersion.DEFAULT );
            includes = new HashSet<>( Arrays.asList( EagleboardApiVersion.values() ) );

            if ( !includeDefault )
            {
                includes.remove( EagleboardApiVersion.DEFAULT );
            }
        }

        includes.removeAll( excludes );

        return includes;
    }
}
