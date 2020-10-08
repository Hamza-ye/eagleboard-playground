package com.mass3d.webapi.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mass3d.logging.LogLevel;
import com.mass3d.logging.LoggingManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class WebRequestInterceptor extends HandlerInterceptorAdapter
{
    private final LoggingManager.Logger log = LoggingManager.createLogger( WebRequestInterceptor.class );

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception
    {
        long startTime = System.currentTimeMillis();
        request.setAttribute( "log:startTime", startTime );

        return true;
    }

    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView ) throws Exception
    {
        long startTime = (Long) request.getAttribute( "log:startTime" );
        long requestTime = System.currentTimeMillis() - startTime;

        log.log( new WebRequestLog( LogLevel.INFO, requestTime, request.getRequestURL().toString() ) );
    }

    @Override
    public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex ) throws Exception
    {
    }
}
