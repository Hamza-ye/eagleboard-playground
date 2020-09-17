package com.mass3d;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
//@ImportResource({"classpath*:META-INF/dhis/beans.xml"})
//@EntityScan(basePackages = "com.mass3d")
@ComponentScan(basePackages = {"com.mass3d"})
//@EnableSpringDataWebSupport
public class webApiConfiguration {

}
