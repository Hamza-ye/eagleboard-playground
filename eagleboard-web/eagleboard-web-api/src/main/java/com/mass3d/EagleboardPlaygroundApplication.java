package com.mass3d;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication /* (exclude = {
    SecurityAutoConfiguration.class,
    RedisAutoConfiguration.class}) */
// , exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class
@EnableTransactionManagement
public class EagleboardPlaygroundApplication {

  public static void main(String[] args) {
    SpringApplication.run(EagleboardPlaygroundApplication.class, args);
  }

}
