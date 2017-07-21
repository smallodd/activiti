package com.activiti.service.impl;

import com.activiti.service.PublishProcessService;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Created by ma on 2017/7/18.
 */

public class PublishProcessServiceImp implements PublishProcessService {


    @Resource
    RepositoryService repositoryService;
    @Resource
    RuntimeService runtimeService;
    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;
    @Resource
    IdentityService identityService;
@Override
    public String publish(String name){

    return   repositoryService.createDeployment().addClasspathResource("test.bpmn").deploy().getId();
    }
    /**
     * 上传部署流程
     * @param zipInputStream
     * @return
     */
    @Override
    public String publish(ZipInputStream zipInputStream) {
        Deployment deployment= repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        return deployment.getId();
    }

    @Override
    public String startProcess(String publishUserID,String processId,String bussnissKey,Map<String,Object> map) {
        identityService.setAuthenticatedUserId(publishUserID);

        ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(processId,bussnissKey,map);
        runtimeService.setVariablesLocal(processInstance.getId(),map);
        return  processInstance.getBusinessKey();
    }

    @Override
    public List<ProcessDefinition> queryList(int startPage,int pageSize) {
        List<ProcessDefinition> list=repositoryService.createProcessDefinitionQuery().listPage(startPage,pageSize);
        return list;
    }
    @Override
    public void deleteById(String processId){
        repositoryService.deleteDeployment(processId);
    }


}
