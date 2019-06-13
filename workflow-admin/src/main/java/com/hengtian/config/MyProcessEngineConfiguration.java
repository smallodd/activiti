package com.hengtian.config;

import com.hengtian.common.workflow.activiti.CustomProcessEngineConfigurationImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @ClassName MyProcessEngineConfiguration
 * @Description
 * @Author hour
 * @Date2019/6/11 16:20
 * @Version V1.0
 */
@Data
@Configuration
public class MyProcessEngineConfiguration {

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
}
