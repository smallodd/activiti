package com.hengtian.common.config;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * mybatis配置
 * @ClassName MybatisConfiguration
 * @Description
 * @Author hour
 * @Date2019/6/13 10:06
 * @Version V1.0
 */
@Slf4j
@Configuration
public class MybatisConfiguration {

    /**
     * Spring整合Mybatis
     */
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource,
                                                          @Qualifier("globalConfig") GlobalConfiguration globalConfig){
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.hengtian.*.model");
        // 设置mybatis的主配置文件
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            bean.setMapperLocations(resolver.getResources("classpath:mapper/**/*.xml"));
        } catch (IOException e) {
            log.info("Mybatis配置出错", e);
        }

        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDialectType("mysql");
        Interceptor[] plugins = {paginationInterceptor};
        bean.setPlugins(plugins);
        bean.setGlobalConfig(globalConfig);
        return bean;
    }

    /**
     * 定义 MP 全局策略
     */
    @Bean
    public GlobalConfiguration globalConfig(){
        GlobalConfiguration  globalConfig = new GlobalConfiguration();
        globalConfig.setIdType(3);
        return globalConfig;
    }

    /**
     * MyBatis 动态实现
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.hengtian.*.dao");
        return mapperScannerConfigurer;
    }
}
