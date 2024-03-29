package com.hengtian.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 功能描述:数据库连接池配置
 * @Author: hour
 * @Date: 2019/6/13 16:35
 */
@Slf4j
@Configuration
@EnableConfigurationProperties

public class DruidConfiguration {



    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.initialSize}")
    private int initialSize;
    @Value("${spring.datasource.maxActive}")
    private int maxActive;
    @Value("${spring.datasource.minIdle}")
    private int minldle;
    @Value("${spring.datasource.maxWait}")
    private long maxWait;
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;
    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;
    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhiledle;
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private long minEvictabledleTimeMillis;
    @Value("${spring.datasource.removeAbandoned}")
    private boolean removeAbandoned;
    @Value("${spring.datasource.removeAbandonedTimeout}")
    private int  removeAbandonedTimeout;
    @Value("${spring.datasource.logAbandoned}")
    private boolean logAbandoned;
    @Value("${spring.datasource.name}")
    private String datasourceName;
    @Value("${spring.datasource.type}")
    private String datasourceType;
    @Value("${spring.datasource.maxOpenPreparedStatements}")
    private int maxOpenPreparedStatements;






    /**
     * 功能描述:数据源
     * @return: javax.sql.DataSource
     * @Author: hour
     * @Date: 2019/6/13 11:03
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setInitialSize(initialSize);
        log.info("initialSize：{}",initialSize);
        druidDataSource.setMaxActive(maxActive);
        log.info("maxActive：{}",maxActive);
        druidDataSource.setMinIdle(minldle);
        log.info("minldle：{}",minldle);
        druidDataSource.setMaxWait(maxWait);
        log.info("maxWait：{}",maxWait);
        druidDataSource.setValidationQuery(validationQuery);
        log.info("validationQuery：{}",validationQuery);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        log.info("testOnBorrow：{}",testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        log.info("testOnReturn：{}",testOnReturn);
        druidDataSource.setTestWhileIdle(testWhiledle);
        log.info("testWhiledle：{}",testWhiledle);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        log.info("timeBetweenEvictionRunsMillis：{}",timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictabledleTimeMillis);
        log.info("minEvictabledleTimeMillis：{}",minEvictabledleTimeMillis);
        druidDataSource.setRemoveAbandoned(removeAbandoned);
        log.info("removeAbandoned：{}",removeAbandoned);
        druidDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        log.info("removeAbandonedTimeout：{}",removeAbandonedTimeout);
        druidDataSource.setLogAbandoned(logAbandoned);
        log.info("logAbandoned：{}",logAbandoned);
        druidDataSource.setName(datasourceName);
        log.info("datasourceName：{}",datasourceName);
        druidDataSource.setDbType(datasourceType);
        log.info("datasourceType：{}",datasourceType);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        log.info("maxOpenPreparedStatements：{}",maxOpenPreparedStatements);

        return druidDataSource;
    }

    /**
     * 功能描述:事务
     * @param dataSource
     * @return: org.springframework.transaction.PlatformTransactionManager
     * @Author: hour
     * @Date: 2019/6/13 16:36
     */
    @Bean(name="transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
