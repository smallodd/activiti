package com.hengtian.config;

import com.hengtian.common.workflow.activiti.CustomProcessEngineConfigurationImpl;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ProcessEngineFactoryBeanConfigure
 * @Description
 * @Author hour
 * @Date2019/6/11 17:03
 * @Version V1.0
 */
@Configuration
public class ProcessEngineFactoryBeanConfigure {

    @Autowired
    CustomProcessEngineConfigurationImpl processEngineConfiguration = new CustomProcessEngineConfigurationImpl();

    @Bean
    public ProcessEngineFactoryBean processEngine(){
        ProcessEngineFactoryBean processEngine = new ProcessEngineFactoryBean();
        processEngine.setProcessEngineConfiguration(processEngineConfiguration);
        return processEngine;
    }
}
