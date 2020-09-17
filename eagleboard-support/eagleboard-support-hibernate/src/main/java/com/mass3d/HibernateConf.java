package com.mass3d;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConf {

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan("com.mass3d");
    sessionFactory.setHibernateProperties(hibernateProperties());

    return sessionFactory;
  }

  @Bean
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("sa");

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager hibernateTransactionManager() {
    HibernateTransactionManager transactionManager
        = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory().getObject());
    return transactionManager;
  }

  private final Properties hibernateProperties() {
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty(
        "hibernate.hbm2ddl.auto", "create-drop");
    hibernateProperties.setProperty(
        "hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    hibernateProperties.setProperty("hibernate.show_sql", "true");
    hibernateProperties.setProperty("hibernate.format_sql", "true");

    return hibernateProperties;
  }

  @Bean(name = "jdbcTemplate")
  public JdbcTemplate jdbcTemplate() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    jdbcTemplate.setFetchSize(1000);
    return jdbcTemplate;
  }

//  @Bean(name = "readOnlyJdbcTemplate")
//  public JdbcTemplate readOnlyJdbcTemplate() {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(readOnlyDataSource());
//    jdbcTemplate.setFetchSize(1000);
//    return jdbcTemplate;
//  }
}