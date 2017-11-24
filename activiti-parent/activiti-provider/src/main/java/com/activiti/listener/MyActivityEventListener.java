package com.activiti.listener;

import com.activiti.common.EmailUtil;
import com.activiti.model.SysUser;
import com.activiti.model.TUserTask;
import com.activiti.service.SysUserService;
import com.activiti.service.TUserTaskService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.util.ConfigUtil;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/11/23.
 */

public class MyActivityEventListener implements ActivitiEventListener {

    private  TUserTaskService tUserTaskService;

    private   SysUserService sysUserService;
    public MyActivityEventListener(TUserTaskService tUserTaskService,SysUserService sysUserService){
        this.sysUserService=sysUserService;
        this.tUserTaskService=tUserTaskService;
    }

    @Override
    public void onEvent(ActivitiEvent activitiEvent) {

        switch (activitiEvent.getType()) {

            case ACTIVITY_COMPENSATE:
                // 一个节点将要被补偿。事件包含了将要执行补偿的节点id。
                break;
            case ACTIVITY_COMPLETED:
                System.out.println("一个节点结束了");
                // 一个节点成功结束
                break;
            case ACTIVITY_ERROR_RECEIVED:
                // 一个节点收到了一个错误事件。在节点实际处理错误之前触发。 事件的activityId对应着处理错误的节点。 这个事件后续会是ACTIVITY_SIGNALLED或ACTIVITY_COMPLETE， 如果错误发送成功的话。
                break;
            case ACTIVITY_MESSAGE_RECEIVED:
                // 一个节点收到了一个消息。在节点收到消息之前触发。收到后，会触发ACTIVITY_SIGNAL或ACTIVITY_STARTED，这会根据节点的类型（边界事件，事件子流程开始事件）
                break;
            case ACTIVITY_SIGNALED:
                // 一个节点收到了一个信号
                break;
            case ACTIVITY_STARTED:
                System.out.println("任务开始了");
                TaskService taskService= activitiEvent.getEngineServices().getTaskService();
                List<Task> tasks=taskService.createTaskQuery().processInstanceId(activitiEvent.getProcessInstanceId()).list();
                ProcessDefinition processDefinition=activitiEvent.getEngineServices().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(activitiEvent.getProcessDefinitionId()).singleResult();
                EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
                wrapper.where("proc_def_key= {0}",processDefinition.getKey()).andNew("version_={0}",processDefinition.getVersion());
                Map<String,Object> map=activitiEvent.getEngineServices().getRuntimeService().getVariables(activitiEvent.getExecutionId());
                List<TUserTask> tUserTasks=tUserTaskService.selectList(wrapper);
                if(tUserTasks==null||tasks.size()==0){
                    throw new RuntimeException("操作失败，请在工作流管理平台设置审批人后在创建任务");
                }
                for(Task task:tasks){
                    for(TUserTask tUserTask:tUserTasks){
                        if(StringUtils.isBlank(tUserTask.getCandidateIds())){
                            throw  new RuntimeException("操作失败，请在工作流管理平台将任务节点：'"+tUserTask.getTaskName()+"'设置审批人后在创建任务");
                        }
                        if(task.getTaskDefinitionKey().trim().equals(tUserTask.getTaskDefKey().trim())){
                            if ("candidateGroup".equals(tUserTask.getTaskType())) {
                                taskService.addCandidateGroup(task.getId(), tUserTask.getCandidateIds());
                            } else if ("candidateUser".equals(tUserTask.getTaskType())) {
                                taskService.addCandidateUser(task.getId(), tUserTask.getCandidateIds());
                            } else {

                                taskService.setAssignee(task.getId(), tUserTask.getCandidateIds());
                            }
                        }
                        Boolean flag=Boolean.valueOf(ConfigUtil.getValue("isSendMail"));
                        if(flag) {
                            String[] strs = tUserTask.getCandidateIds().split(",");
                            for (String str : strs) {
                                SysUser sysUser = sysUserService.selectById(str);
                                if (StringUtils.isNotBlank(sysUser.getUserEmail())) {
                                    EmailUtil emailUtil = EmailUtil.getEmailUtil();
                                    try {
                                        emailUtil.sendEmail(
                                                ConfigUtil.getValue("email.send.account"),
                                                "System emmail",
                                                sysUser.getUserEmail(),
                                                "您有一个待审批邮件待处理",
                                                map.get("applyUserName").toString() + "填写一个审批申请，标题为：" +map.get("applyTitle").toString()  + ",请到<a href='http://core.chtwm.com/login.html'>综合业务平台系统</a>中进行审批!");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }


                // 一个节点开始执行
                break;
            case CUSTOM:
                break;
            case ENGINE_CLOSED:
                // 监听器监听的流程引擎已经关闭，不再接受API调用。
                break;
            case ENGINE_CREATED:
                // 监听器监听的流程引擎已经创建完毕，并准备好接受API调用。
                break;
            case ENTITY_ACTIVATED:
                // 激活了已存在的实体，实体包含在事件中。会被ProcessDefinitions, ProcessInstances 和 Tasks抛出。
                break;
            case ENTITY_CREATED:
                // 创建了一个新实体。实体包含在事件中。
                break;
            case ENTITY_DELETED:
                // 删除了已存在的实体。实体包含在事件中
                break;
            case ENTITY_INITIALIZED:
                // 创建了一个新实体，初始化也完成了。如果这个实体的创建会包含子实体的创建，这个事件会在子实体都创建/初始化完成后被触发，这是与ENTITY_CREATED的区别。
                break;
            case ENTITY_SUSPENDED:
                // 暂停了已存在的实体。实体包含在事件中。会被ProcessDefinitions, ProcessInstances 和 Tasks抛出。
                break;
            case ENTITY_UPDATED:
                // 更新了已存在的实体。实体包含在事件中。
                break;
            case JOB_EXECUTION_FAILURE:
                // 作业执行失败。作业和异常信息包含在事件中。
                break;
            case JOB_EXECUTION_SUCCESS:
                // 作业执行成功。job包含在事件中。
                break;
            case JOB_RETRIES_DECREMENTED:
                // 因为作业执行失败，导致重试次数减少。作业包含在事件中。
                break;
            case MEMBERSHIPS_DELETED:
                // 所有成员被从一个组中删除。在成员删除之前触发这个事件，所以他们都是可以访问的。 因为性能方面的考虑，不会为每个成员触发单独的MEMBERSHIP_DELETED事件。
                break;
            case MEMBERSHIP_CREATED:
                // 用户被添加到一个组里。事件包含了用户和组的id。
                break;
            case MEMBERSHIP_DELETED:
                // 用户被从一个组中删除。事件包含了用户和组的id。
                break;
            case TASK_ASSIGNED:
                // 任务被分配给了一个人员。事件包含任务。
                break;
            case TASK_COMPLETED:
                // 任务被完成了。它会在ENTITY_DELETE事件之前触发。当任务是流程一部分时，事件会在流程继续运行之前， 后续事件将是ACTIVITY_COMPLETE，对应着完成任务的节点。
                break;
            case TIMER_FIRED:
                // 触发了定时器。job包含在事件中。
                break;
            case UNCAUGHT_BPMN_ERROR:
                break;
            case VARIABLE_CREATED:
                break;
            case VARIABLE_DELETED:
                break;
            case VARIABLE_UPDATED:
                break;
            case TASK_CREATED:
                System.out.println("创建一个任务");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
