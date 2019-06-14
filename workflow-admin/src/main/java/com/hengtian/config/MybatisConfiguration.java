package com.hengtian.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * mybatis配置
 * @ClassName MybatisConfiguration
 * @Description
 * @Author hour
 * @Date2019/6/13 10:06
 * @Version V1.0
 */
@Configuration
public class MybatisConfiguration {

    /**
     * Spring整合Mybatis
     */
    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource){
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDialectType("mysql");
        Interceptor[] plugins = {paginationInterceptor};
        bean.setPlugins(plugins);
        return bean;
    }


}
