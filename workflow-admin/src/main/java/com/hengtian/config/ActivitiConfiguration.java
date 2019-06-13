package com.hengtian.config;

import com.hengtian.common.workflow.activiti.CustomProcessEngineConfigurationImpl;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * activiti配置
 * @ClassName ActivitiConfiguration
 * @Description
 * @Author hour
 * @Date2019/6/13 10:59
 * @Version V1.0
 */
@Slf4j
@Configuration
public class ActivitiConfiguration {

    @Autowired
    CustomProcessEngineConfigurationImpl processEngineConfiguration = new CustomProcessEngineConfigurationImpl();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Value("${activiti.database.schema.update}")
    private String databaseSchemaUpdate;

    @Value("${activiti.mail.server.host}")
    private String mailServerHost;

    @Value("${activiti.mail.server.port}")
    private int mailServerPort;

    @Value("${activiti.mail.server.default.from}")
    private String mailServerDefaultFrom;

    @Value("${activiti.mail.server.username}")
    private String mailServerUsername;

    @Value("${activiti.mail.server.password}")
    private String mailServerPassword;

    @Value("${activiti.mail.server.use.SSL}")
    private boolean mailServerUseSSL;

    @Value("${activiti.activity.font.name}")
    private String activityFontName;

    @Value("${activiti.label.font.name}")
    private String labelFontName;

    @Value("${activiti.annotation.font.name}")
    private String annotationFontName;

    @Bean
    public ProcessEngineFactoryBean processEngine(){
        ProcessEngineFactoryBean processEngine = new ProcessEngineFactoryBean();
        processEngine.setProcessEngineConfiguration(processEngineConfiguration);
        return processEngine;
    }

    @Bean
    @Primary
    public CustomProcessEngineConfigurationImpl processEngineConfiguration(){
        CustomProcessEngineConfigurationImpl processEngineConfiguration = new CustomProcessEngineConfigurationImpl();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setTransactionManager(transactionManager);
        processEngineConfiguration.setDatabaseSchemaUpdate(databaseSchemaUpdate);
        processEngineConfiguration.setMailServerHost(mailServerHost);
        processEngineConfiguration.setMailServerPort(mailServerPort);
        processEngineConfiguration.setMailServerDefaultFrom(mailServerDefaultFrom);
        processEngineConfiguration.setMailServerUsername(mailServerUsername);
        processEngineConfiguration.setMailServerPassword(mailServerPassword);
        processEngineConfiguration.setMailServerUseSSL(mailServerUseSSL);
        processEngineConfiguration.setActivityFontName(activityFontName);
        processEngineConfiguration.setLabelFontName(labelFontName);
        processEngineConfiguration.setAnnotationFontName(annotationFontName);
        return processEngineConfiguration;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(@Qualifier("dataSource") DataSource dataSource) {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        configuration.setJobExecutorActivate(true);
        configuration.setTransactionManager(transactionManager(dataSource));
        return configuration;
    }

    @Bean
    public RepositoryService repositoryService() {
        return processEngineConfiguration.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngineConfiguration.getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        return processEngineConfiguration.getTaskService();
    }

    @Bean
    public HistoryService historyService() {
        return processEngineConfiguration.getHistoryService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngineConfiguration.getManagementService();
    }

    @Bean
    public IdentityService IdentityService() {
        return processEngineConfiguration.getIdentityService();
    }

    @Bean
    public FormService formService() {
        return processEngineConfiguration.getFormService();
    }

    @Bean
    public FormService processEngineFactoryBean() {
        return processEngineConfiguration.getFormService();
    }
}
