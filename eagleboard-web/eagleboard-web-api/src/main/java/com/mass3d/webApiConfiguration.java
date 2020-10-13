package com.mass3d;

import javax.servlet.Filter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
//@ImportResource({"classpath*:META-INF/mass3d/beans.xml"})
//@EntityScan(basePackages = "com.mass3d")
@ComponentScan(basePackages = {"com.mass3d"})
@ImportResource({"classpath*:META-INF/mass3d/beans.xml"})
@EnableTransactionManagement
//@EnableSpringDataWebSupport
public class webApiConfiguration {

  @Bean
  public FilterRegistrationBean filter1() {

    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(openSessionInViewFilter());
//    registration.addUrlPatterns("/url/*");
//    registration.addInitParameter("paramName", "paramValue");
    registration.setName("OpenSessionInViewFilter");
//    registration.setOrder(1);
    registration.setAsyncSupported(true);
    return registration;
  }

  @Bean
  public FilterRegistrationBean filter2() {

    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(openSessionInViewFilter());
    registration.addUrlPatterns("/*");
//    registration.addInitParameter("paramName", "paramValue");
    registration.setName("OpenSessionInViewFilter");
//    registration.setOrder(1);
//    registration.setAsyncSupported(true);
    return registration;
  }

  @Bean(name = "OpenSessionInViewFilter")
  public Filter openSessionInViewFilter() {
    return new OpenSessionInViewFilter();
  }
}
