package com.hengtian.flow.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.application.dao.AppModelDao;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.flow.service.TUserTaskService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
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
public class WorkflowOperateController extends WorkflowBaseController{
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    @Autowired
    TUserTaskService tUserTaskService;
    @Autowired
    AppModelService appModelService;
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
                EntityWrapper<AppModel> wrapperApp=new EntityWrapper();

                wrapperApp.where("app_key={0}",processParam.getAppKey()).andNew("model_key={0}",processParam.getProcessDefinitionKey());
                AppModel appModelResult=appModelService.selectOne(wrapperApp);
                //系统与流程定义之间没有配置关系
                if(appModelResult==null){
                    result.setCode(Constant.RELATION_NOT_EXIT);
                    result.setMsg("系统键值：【"+processParam.getAppKey()+"】对应的modelkey:【"+processParam.getProcessDefinitionKey()+"】关系不存在!");
                    return result;

                }
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

                   //查询创建完任务之后生成的任务信息
                   List<Task> taskList=taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

                   for(int i=0;i<taskList.size();i++){

                   }
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


    /**
     * 任务操作接口：包括
     * @param taskActionParam
     * @return result
     * @author houjinrong@chtwm.com
     * date 2018/4/18 9:38
     */
    public Object taskAction(TaskActionParam taskActionParam){
        String actionType = taskActionParam.getActionType();
        if(StringUtils.isBlank(actionType)){
            return renderError("操作类型不能为空");
        }

        Result result = new Result();
        if(true){

        }

        return result;
    }
}
