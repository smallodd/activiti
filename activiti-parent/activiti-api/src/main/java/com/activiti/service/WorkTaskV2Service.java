package com.activiti.service;

import com.activiti.entity.ApproveVo;
import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTasksVo;
import com.activiti.entity.TaskQueryEntity;
import com.activiti.expection.WorkFlowException;
import com.activiti.model.App;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 工作流
 * @return
 * @author houjinrong@chtwm.com
 * date 2018/1/29 15:15
 */
public interface WorkTaskV2Service {

    /**
     * 开启任务
     * 注：如是动态自己设置审批人，任务启动后要调用setApprove方法设置审批人，无法重复设置审批人
     * @param commonVo
     * @param paramMap 流程定义中线上的参数，键是线上的键
     * @return 返回部署的任务id,建议存储到业务系统
     */
    String startTask(CommonVo commonVo,Map<String,Object> paramMap) throws WorkFlowException;

    /**
     * 查询业务主键是否在流程中
     * @param taskQueryEntity 任务查询query
     * @param businessKey 业务主键
     * @return 返回true or false
     */
    boolean checkBusinessKeyIsInFlow(TaskQueryEntity taskQueryEntity,String businessKey) ;

    /**
     * 设置审批人
     * @param processInstanceId 流程id
     * @param userCodes 用户工号，用逗号隔开
     * @return
     */
    boolean setApprove(String processInstanceId,String userCodes) throws WorkFlowException;

    /**
     * 通过用户相关信息查询待审批任务
     * @param userId  用户信息，一般是id
     * @param startPage 起始页数
     * @param pageSize 每页显示数
     * @param taskQueryEntity 查询任务query
     * @return 返回任务列表
     */
    PageInfo<Task> queryTaskByAssign(String userId, int startPage, int pageSize, TaskQueryEntity taskQueryEntity) throws WorkFlowException;


    /**
     * 审批接口
     * 注：如是动态自己设置审批人，任务审批后要调用setApprove方法设置审批人，无法重复设置审批人
     * @param approveVo 审批信息封装类，具体请看ApproveVo类说明
     * @param paramMap 自定义参数键值对
     * @return 注意：通过工作流平台设置审批人，此方法每次都会返回processId,流程实例的id
     *              如是动态设置审批人，在审批后如任务还未完成继续返回processId,如任务已结束将返回null
     * @exception WorkFlowException 返回审批异常
     */
    boolean completeTask(ApproveVo approveVo,Map<String,Object> paramMap) throws WorkFlowException;

    /**
     * 获取申请人提交的任务
     * @param userId 申请人信息
     * @param startPage 起始页数
     * @param pageSize 查询多少条数
     * @param status 0 :审批中的任务
     *               1 ：审批完成的任务
     * @param taskQueryEntity 任务查询query
     * @return 返回申请人提交的任务
     */
    List<HistoricProcessInstance> getApplyTasks(String userId, int startPage, int pageSize, int status,TaskQueryEntity taskQueryEntity);

    /**
     * 通过用户主键查询历史审批过的任务
     * @param userId 用户主键
     * @param startPage 开始页数
     * @param pagegSize 每页显示数
     * @param taskQueryEntity 查询任务query
     *
     * @return 返回审批历史人物信息列表
     */
    PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pagegSize,TaskQueryEntity taskQueryEntity);

    /**
     * 通过用户主键查询审批拒绝的信息
     * @param userId 用户主键
     * @param startPage 开始页数
     * @param pageSize 结束页数
     * @param taskQueryEntity 查询任务query
     * @return 返回用户拒绝的信息
     */
    PageInfo<HistoricTaskInstance> selectMyRefuse(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity);

    /**
     * 获取任务审批意见列表
     * @param processInstanceId 流程实例ID
     * @return 返回审批意见列表
     */
    List<Comment> selectCommentList(String processInstanceId);

    /**
     * 通过流程定义id获取定义变量
     * @param processInstanceId 流程实例ID
     * @return 返回自定义变量map
     */
    Map<String, Object> getVariables(String processInstanceId);

    /**
     * 流程任务跟踪标识
     * @author houjinrong@chtwm.com
     * @param processInstanceId 流程实例id
     * @return
     */
    byte[] getTaskSchedule(String processInstanceId);

    /**
     * 根据流程实例ID查询历史任务信息
     * @author houjinrong
     * @param processInstanceId 流程实例id
     * @param variableNames 自定义的键集合
     * @return 返回历史任务信息
     */
    HistoryTasksVo getTaskHistoryByProcessInstanceId(String processInstanceId,List<String> variableNames);

    /**
     * 获取应用列表
     * @return  返回app列表
     */
    List<App> getAppList();

    /**
     * 根据应用key获取应用所属的模型列表
     * @author houjinrong@chtwm.com
     * @param appKey 定义的appkey
     * @return 返回app关联的模型
     */
    List<Model> getModelListByAppKey(String appKey);

    /**
     * 委派任务（不建议使用）
     * @author houjinrong@chtwm.com
     * @param userId 当前任务节点ID
     * @param taskId 被委派人工号
     * @return
     */
    @Deprecated
    boolean delegateTask(String userId, String taskId);

    /**
     * 转办任务
     * @author houjinrong@chtwm.com
     * @param processInstanceId 流程实例ID
     * @param userId 被转办人工号
     * @param transferUserId 转办人工号
     * @return 返回转办成功或失败
     */
    boolean transferTask(String processInstanceId, String userId, String transferUserId) throws WorkFlowException;

    /**
     * 通过流程实例id查询最后审批人
     * @param processInstanceId  流程实例id
     * @return 返回当前任务的最后审批人
     */
    String getLastApprover(String processInstanceId);
    
    /**
     * 任务跳转
     * @param processInstanceId 流程实例ID
     * @param taskDefinitionKey 跳转到的任务节点KEY
     * @param userCodes 要设置的审批人，动态审批时不为空，多个用逗号隔开
     * @return 流程实例ID
     * @author houjinrong@chtwm.com
     * date 2018/2/1 20:32
     */
    boolean taskJump(String processInstanceId, String taskDefinitionKey, String userCodes) throws WorkFlowException;

    /**
     * 删除一个流程实例
     * @param processInstanceId 流程实例ID
     * @param description 删除原因
     * @author houjinrong@chtwm.com
     * @return
     */
    boolean deleteProcessInstance(String processInstanceId, String description);

    /**
     * 流程驳回
     * @param processInstanceId 流程实例ID
     * @param rollBackType 0：恢复到开始任务节点；1：恢复到驳回前到达的任务节点
     * @return true：成功；false：失败
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:35
     */
    boolean rollBackProcess(String processInstanceId, int rollBackType) throws WorkFlowException;

    /**
     * 恢复驳回的流程
     * @param processInstanceId 流程实例ID
     * @param operator 回复驳回操作人
     * @param variables 属性值
     * @return 流程实例ID
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:36
     */
    boolean resumeProcess(String processInstanceId, String operator, Map<String,Object> variables) throws WorkFlowException;

    /**
     * 获取当前任务的审批人
     * @param processInstanceId 流程实例ID
     * @return 当前任务审批人，多个逗号隔开
     * @author houjinrong@chtwm.com
     * date 2018/3/5 17:25
     */
    String getCurrentApprover(String processInstanceId);

    /**
     * 设置属性值
     * @param processInstanceId 流程实例ID
     * @return
     */
    boolean setVariables(String processInstanceId, Map<String,Object> variables) throws WorkFlowException;
}
