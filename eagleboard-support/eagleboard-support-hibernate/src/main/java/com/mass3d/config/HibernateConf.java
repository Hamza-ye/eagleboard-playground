package com.mass3d.config;

import com.mass3d.cache.DefaultHibernateCacheManager;
import com.mass3d.dbms.DbmsManager;
import com.mass3d.dbms.HibernateDbmsManager;
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

//@Configuration
//@EnableTransactionManagement
public class HibernateConf {

//  @Bean
//  public LocalSessionFactoryBean sessionFactory() {
//    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//    sessionFactory.setDataSource(dataSource());
//    sessionFactory.setPackagesToScan("com.mass3d");
//    sessionFactory.setHibernateProperties(hibernateProperties());
//
//    return sessionFactory;
//  }
//
//  @Bean
//  public DataSource dataSource() {
//    BasicDataSource dataSource = new BasicDataSource();
//    dataSource.setDriverClassName("org.h2.Driver");
//    dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
//    dataSource.setUsername("sa");
//    dataSource.setPassword("sa");
//
//    return dataSource;
//  }
//
//  @Bean
//  public PlatformTransactionManager hibernateTransactionManager() {
//    HibernateTransactionManager transactionManager
//        = new HibernateTransactionManager();
//    transactionManager.setSessionFactory(sessionFactory().getObject());
//    return transactionManager;
//  }
//
//  private final Properties hibernateProperties() {
//    Properties hibernateProperties = new Properties();
//    hibernateProperties.setProperty(
//        "hibernate.hbm2ddl.auto", "create-drop");
//    hibernateProperties.setProperty(
//        "hibernate.dialect", "org.hibernate.dialect.H2Dialect");
////    hibernateProperties.setProperty("hibernate.show_sql", "true");
////    hibernateProperties.setProperty("hibernate.format_sql", "true");
//
//    return hibernateProperties;
//  }
//
//  @Bean(name = "jdbcTemplate")
//  public JdbcTemplate jdbcTemplate() {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
//    jdbcTemplate.setFetchSize(1000);
//    return jdbcTemplate;
//  }
//
//  @Bean
//  public DefaultHibernateCacheManager cacheManager()
//      throws Exception
//  {
//    DefaultHibernateCacheManager cacheManager = new DefaultHibernateCacheManager();
//    cacheManager.setSessionFactory(sessionFactory().getObject());
//    return cacheManager;
//  }
//
//  @Bean
//  public DbmsManager dbmsManager()
//      throws Exception
//  {
//    HibernateDbmsManager hibernateDbmsManager = new HibernateDbmsManager();
//    hibernateDbmsManager.setCacheManager( cacheManager() );
//    hibernateDbmsManager.setSessionFactory(sessionFactory().getObject());
//    hibernateDbmsManager.setJdbcTemplate( jdbcTemplate() );
//    return hibernateDbmsManager;
//  }

}