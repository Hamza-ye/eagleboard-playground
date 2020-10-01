package com.mass3d.webapi.mvc.annotation;

import com.mass3d.common.EagleboardApiVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target( { ElementType.TYPE, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface ApiVersion
{
    @AliasFor( "include" )
    EagleboardApiVersion[] value() default EagleboardApiVersion.ALL;

    @AliasFor( "value" )
    EagleboardApiVersion[] include() default EagleboardApiVersion.ALL;

    EagleboardApiVersion[] exclude() default {};
}
