package com.hengtian.common.workflow.activiti;


import org.activiti.spring.SpringProcessEngineConfiguration;

/**
 * Created by ma on 2018/3/5.
 */
public  class CustomProcessEngineConfigurationImpl extends SpringProcessEngineConfiguration {
    @Override
    protected void initProcessDiagramGenerator() {
        processDiagramGenerator = new CustomProcessDiagramGenerator();
    }
}
