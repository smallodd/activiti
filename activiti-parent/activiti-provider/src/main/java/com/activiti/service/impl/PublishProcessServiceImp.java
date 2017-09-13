package com.activiti.service.impl;

import com.activiti.common.CodeConts;
import com.activiti.expection.WorkFlowException;
import com.activiti.service.PublishProcessService;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Created by ma on 2017/7/18.
 */

public class PublishProcessServiceImp implements PublishProcessService {

    private static  Logger logger=Logger.getLogger(PublishProcessServiceImp.class);
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

    return   repositoryService.createDeployment().addClasspathResource(name).deploy().getId();
    }
    /**
     * 上传部署流程
     * @param zipInputStream
     * @return
     */
    @Override
    public String publish(ZipInputStream zipInputStream) throws WorkFlowException {
        try {
            Deployment deployment= repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
            return deployment.getId();
        }catch (Exception e){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PUBLISH_ERROR,"部署失败，请联系管理员");
        }


    }

    @Override
    public String startProcess(String publishUserID,String processId,String bussnissKey,Map<String,Object> map) throws WorkFlowException {
        if(StringUtils.isBlank(publishUserID)){
           throw new WorkFlowException(CodeConts.WORK_FLOW_APPLY_USER,"申请人id不能为空");
        }
        if(StringUtils.isBlank(processId)){
          throw new WorkFlowException(CodeConts.WORK_FLOW_DEFINED_ERROR,"流程定义id不能为空");
        }
        if(StringUtils.isBlank(bussnissKey)){
            throw new WorkFlowException(CodeConts.WORK_FLOW_BUSSINESS_KEY_ERROR,"业务主键不能为空");
        }
        identityService.setAuthenticatedUserId(publishUserID);

        ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(processId,bussnissKey,map);
        runtimeService.setVariablesLocal(processInstance.getId(),map);
        return processInstance.getId();
    }

    @Override
    public List<ProcessDefinition> queryList(int startPage,int pageSize) {
        List<ProcessDefinition> list=repositoryService.createProcessDefinitionQuery().listPage((startPage-1)*pageSize,pageSize);
        return list;
    }
    @Override
    public void deleteById(String processId){
        repositoryService.deleteDeployment(processId);
    }

    /**
     * 通过流程之间查询流程定义key
     * @param processId
     * @return
     */
    public String selectProcessKey(String processId){
       ProcessInstance processInstance= runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
       if(processInstance==null){
           return null;
       }
       return processInstance.getProcessDefinitionKey();
    }

}
