package com.activiti.cmd;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.CommandInterceptor;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;

/**
 * Created by ma on 2018/3/5.
 */
public  class WorkFlowProcessEngineConfigurationImpl extends SpringProcessEngineConfiguration {
    @Override
    protected void initProcessDiagramGenerator() {
        processDiagramGenerator = new WorkFlowProcessDiagramGenerator();
    }
}
