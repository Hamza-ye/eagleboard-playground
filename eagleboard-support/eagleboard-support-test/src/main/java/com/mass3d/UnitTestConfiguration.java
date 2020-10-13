package com.mass3d;

import com.mass3d.external.conf.DhisConfigurationProvider;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
//@ActiveProfiles( profiles = { "test" } )
@ComponentScan( "com.mass3d" )
//@ImportResource( locations = { "classpath*:/META-INF/mass3d/beans.xml" } )
@Profile("test")
@EnableTransactionManagement(proxyTargetClass = true) // to fix BeanNotOfRequiredTypeException: Bean named X is expected to be of type X but was actually of type 'com.sun.proxy.$Proxy
public class UnitTestConfiguration
{
    @Bean( name = "dhisConfigurationProvider" )
    public DhisConfigurationProvider dhisConfigurationProvider()
    {
        return new H2DhisConfigurationProvider();
    }

//    @Bean( name = "dhisConfigurationProvider" )
//    public DhisConfigurationProvider dhisConfigurationProvider()
//    {
//        TestDhisConfigurationProvider testDhisConfigurationProvider = new TestDhisConfigurationProvider();
//
//        return testDhisConfigurationProvider;
//    }
}
