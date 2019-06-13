package com.hengtian.config;

import com.hengtian.common.workflow.activiti.CustomProcessEngineConfigurationImpl;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * activiti
 *
 * @ClassName MyProcessEngineFactoryBean
 * @Description
 * @Author hour
 * @Date2019/6/11 15:14
 * @Version V1.0
 */
@Configuration
@Primary
public class MyProcessEngineFactoryBean{

    @Autowired
    //private ProcessEngineFactoryBeanConfigure processEngineConfiguration;

    private CustomProcessEngineConfigurationImpl processEngineConfiguration;

    @Bean
    public RepositoryService repositoryService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getRepositoryService();
        return processEngineConfiguration.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getRuntimeService();
        return processEngineConfiguration.getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getTaskService();
        return processEngineConfiguration.getTaskService();
    }

    @Bean
    public HistoryService historyService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getHistoryService();
        return processEngineConfiguration.getHistoryService();
    }

    @Bean
    public ManagementService managementService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getManagementService();
        return processEngineConfiguration.getManagementService();
    }

    @Bean
    public IdentityService IdentityService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getIdentityService();
        return processEngineConfiguration.getIdentityService();
    }

    @Bean
    public FormService formService() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getFormService();
        return processEngineConfiguration.getFormService();
    }

    @Bean
    public FormService processEngineFactoryBean() {
        //return processEngineConfiguration.processEngine().getProcessEngineConfiguration().getFormService();
        return processEngineConfiguration.getFormService();
    }
}
