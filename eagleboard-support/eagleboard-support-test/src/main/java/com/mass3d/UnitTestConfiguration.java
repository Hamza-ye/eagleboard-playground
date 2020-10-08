package com.mass3d;

import com.mass3d.external.conf.DhisConfigurationProvider;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
//@ActiveProfiles( profiles = { "test" } )
@ComponentScan( "com.mass3d" )
//@ImportResource( locations = { "classpath*:/META-INF/dhis/beans.xml", "classpath*:/META-INF/dhis/security.xml" } )
public class UnitTestConfiguration
{
    @Bean( name = "dhisConfigurationProvider" )
    public DhisConfigurationProvider dhisConfigurationProvider()
    {
        TestDhisConfigurationProvider testDhisConfigurationProvider = new TestDhisConfigurationProvider();

        return testDhisConfigurationProvider;
    }
}
