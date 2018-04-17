package com.hengtian.flow.controller;

import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2018/4/12.
 * 所有涉及到操作类的功能都放到这里
 */
@Controller
@RequestMapping("/rest/flow/operate")
public class WorkflowOperateController {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    /**
     * 任务创建接口
     * @param processParam
     * @param variables
     * @return
     */
        @RequestMapping("create")
        @ResponseBody
        @SysLog("接口创建任务操作")
        public Result startProcessInstance(@RequestParam(value = "processParam",required = true) ProcessParam processParam, @RequestParam(value = "variables",required = false)Map<String,Object> variables){
            //校验参数是否合法
            Result result=processParam.validate();
            if(!result.isSuccess()){
                return result;
            }else{
                //校验当前业务主键是否已经在系统中存在
                boolean isInFlow=checekBunessKeyIsInFlow(processParam.getProcessDefinitionKey(),processParam.getAppKey(),processParam.getBussinessKey());

                if(isInFlow){
                    //已经创建过则返回错误信息
                    result.setSuccess(false);
                    result.setMsg("此条信息已经提交过任务");
                    result.setCode(Constant.BUSSINESSKEY_EXIST);
                    return result;
                }else{
                    //生成任务
                   ProcessInstance processInstance= runtimeService.startProcessInstanceByKeyAndTenantId(processParam.getProcessDefinitionKey(),processParam.getBussinessKey(),  variables,processParam.getAppKey());
                   //给对应实例生成标题
                   runtimeService.setProcessInstanceName(processInstance.getId(),processParam.getTitle());

                }

            }
            return null;
        }

        private Boolean checekBunessKeyIsInFlow(String processDefiniKey,String bussinessKey,String appKey){
           TaskQuery taskQuery= taskService.createTaskQuery().processDefinitionKey(processDefiniKey).processInstanceBusinessKey(bussinessKey).taskTenantId(appKey);

           Task task= taskQuery.singleResult();

            if(task!=null){
                return  true;
            }
            return false;
        }

}
