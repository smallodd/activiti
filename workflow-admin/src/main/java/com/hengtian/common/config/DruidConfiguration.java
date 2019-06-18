package com.hengtian.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class DruidConfiguration {

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
        return new DruidDataSource();
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
