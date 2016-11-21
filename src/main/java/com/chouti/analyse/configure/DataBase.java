package com.chouti.analyse.configure;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-09.
 *******************************************************************************/
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.chouti.analyse.mybatis.mapper")
public class DataBase {

    @Value("${datasource.url}")
    private String connectUrl;

    @Value("${datasource.driverClassName}")
    private String driverClassName;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(connectUrl);
        datasource.setDriverClassName(driverClassName);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setInitialSize(10);  //初始化连接数
        datasource.setMaxActive(100);   //最大连接数
        datasource.setMinIdle(1);       //最小连接数
        datasource.setMaxWait(60000);   //60秒超时
        datasource.setRemoveAbandoned(true);
//        datasource.setConnectionProperties("config.decrypt=true;");//加密连接
        datasource.setFilters("config,stat,log4j,wall");
        return datasource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
        sessionFactory.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:com/chouti/analyse/mybatis/mapper/xml/*Mapper.xml"));
        org.apache.ibatis.session.Configuration configuration = sessionFactory.getObject().getConfiguration();
        configuration.setCallSettersOnNulls(true);  //使 mybatis 返回为 null 的字段
        return factoryBuilder.build(configuration);
    }

    @Bean
    public PlatformTransactionManager txManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }
}
