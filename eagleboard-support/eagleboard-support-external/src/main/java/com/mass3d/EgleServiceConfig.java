package com.mass3d;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = {"com.mass3d"})
@ImportResource({"classpath*:META-INF/mass3d/beans.xml"})
public class EgleServiceConfig {

}
