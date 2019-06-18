package com.hengtian.common.config;
import com.alibaba.druid.pool.DruidDataSource;
import com.hengtian.common.workflow.activiti.CustomProcessEngineConfigurationImpl;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    private PlatformTransactionManager transactionManager;
    @Autowired
    private DruidDataSource datasource;

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

    @Value("${spring.datasource.driverClassName}")
    private String jdbcDriver;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String jdbcUsername;
    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Bean
    @Primary
    public DataSource activitiDataSource(){
        return datasource;
    }

    @Bean
    @Primary
    public ProcessEngineFactoryBean processEngine(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration){
        ProcessEngineFactoryBean processEngine = new ProcessEngineFactoryBean();
        processEngine.setProcessEngineConfiguration(processEngineConfiguration);
        return processEngine;
    }

    @Bean
    @Primary
    public CustomProcessEngineConfigurationImpl processEngineConfiguration(@Qualifier("dataSource") DataSource dataSource){
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

        processEngineConfiguration.setJdbcDriver(jdbcDriver);
        processEngineConfiguration.setJdbcUrl(jdbcUrl);
        processEngineConfiguration.setJdbcUsername(jdbcUsername);
        processEngineConfiguration.setJdbcPassword(jdbcPassword);

        return processEngineConfiguration;
    }

    @Bean
    public RepositoryService repositoryService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getRuntimeService();
    }

    @Bean
    public TaskService taskService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getTaskService();
    }

    @Bean
    public HistoryService historyService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getHistoryService();
    }

    @Bean
    public ManagementService managementService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getManagementService();
    }

    @Bean
    public IdentityService IdentityService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getIdentityService();
    }

    @Bean
    public FormService formService(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration) {
        return processEngineConfiguration.getFormService();
    }

    @Bean
    public StrongUuidGenerator uuidGenerator(@Qualifier("processEngineConfiguration") CustomProcessEngineConfigurationImpl processEngineConfiguration){
        return new StrongUuidGenerator();
    }
}
