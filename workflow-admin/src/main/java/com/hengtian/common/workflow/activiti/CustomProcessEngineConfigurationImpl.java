package com.hengtian.common.workflow.activiti;


import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.stereotype.Component;

/**
 * Created by ma on 2018/3/5.
 */
@Component
public class CustomProcessEngineConfigurationImpl extends SpringProcessEngineConfiguration {
    @Override
    protected void initProcessDiagramGenerator() {
        processDiagramGenerator = new CustomProcessDiagramGenerator();
    }
}
