package com.hengtian;

import com.rbac.conf.DomainMapProperties;
import com.rbac.util.CommonUtil;
import com.richgo.config.RedisProperties;
import com.richgo.redis.RedisClusterUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @Description 启动器
 * @ClassName WorkflowAdminApplication
 * @Author hour
 * @Date2019/6/11 10:44
 * @Version V1.0
 */
@Slf4j
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@MapperScan("com.hengtian.**.dao")
@Import({ DomainMapProperties.class, RedisProperties.class, RedisClusterUtil.class, CommonUtil.class})
@ComponentScan(basePackages = {"com.hengtian","org.activiti"})
@ServletComponentScan
public class WorkflowAdminApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WorkflowAdminApplication.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(WorkflowAdminApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
        log.info("工作流系统启动成功");
    }
}
