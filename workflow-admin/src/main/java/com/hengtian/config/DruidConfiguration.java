package com.hengtian.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Date: 2018/11/23
 * Time: 13:49
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 * @author Kayle
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DruidConfiguration {

    /*spring.database.driverClassName=com.mysql.jdbc.Driver
    spring.database.url=jdbc:mysql://172.16.163.51:3307/activiti?useUnicode=true&characterEncoding=utf-8
    spring.database.username=root
    spring.database.password=lgb
    spring.database.initialSize=5
    spring.database.maxActive=100
    spring.database.minIdle=10
    spring.database.maxWait=60000
    spring.database.validationQuery=SELECT 'x'
    spring.database.testOnBorrow=true
    spring.database.testOnReturn=true
    spring.database.testWhileIdle=true
    spring.database.timeBetweenEvictionRunsMillis=60000
    spring.database.minEvictableIdleTimeMillis=300000
    spring.database.removeAbandoned=true
    spring.database.removeAbandonedTimeout=1800
    spring.database.logAbandoned=true*/


    private String url;

    private String username;

    private String password;

    private String driverClassName;

    private int initialSize;

    private int minIdle;

    private int maxActive;

    private int maxWait;

    private int timeBetweenEvictionRunsMillis;

    private int minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean poolPreparedStatements;

    private int maxPoolPreparedStatementPerConnectionSize;

    private String filters;

    private String connectionProperties;

    /**
     * 功能描述:
     * @param
     * @return: javax.sql.DataSource
     * @Author: hour
     * @Date: 2019/6/13 11:03
     */
    @Bean
    @Primary
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(connectionProperties);

        return datasource;
    }
}
