package com.mass3d.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
// before the default configuration
//@Order(SecurityProperties.BASIC_AUTH_ORDER - 11)
class CustomH2ConsoleSecurityConfigurer extends WebSecurityConfigurerAdapter {


  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/h2-console/**").permitAll();

    http.csrf().disable();
    http.headers().frameOptions().disable();
  }

}