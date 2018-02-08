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
     * 查询业务主键是否再流程中
     * @param taskQueryEntity  任务查询query
     * @param businessKey 业务主键
     * @return   返回true or false
     */
    public boolean checkBusinessKeyIsInFlow(TaskQueryEntity taskQueryEntity,String businessKey) ;

    /**
     * 开启任务；
     * 注：如是动态自己设置审批人，任务启动后要调用setApprove方法设置审批人，无法重复设置审批人
     * @param commonVo
     * @param paramMap   流程定义中线上的参数，键是线上的键
     * @return  返回部署的任务id
     */
    String startTask(CommonVo commonVo,Map<String,Object> paramMap) throws WorkFlowException;

    /**
     * 设置审批人
     * @param processId  流程id
     * @param userCodes  用户工号，用逗号隔开
     * @return
     */
    public boolean setApprove(String processId,String userCodes) throws WorkFlowException;

    /**
     * 通过用户相关信息查询待审批任务
     * @param userId  用户信息 一般是id
     * @param  startPage  起始页数
     * @param  pageSize   每页显示数
     * @param  taskQueryEntity  查询任务query
     * @return  返回任务列表
     */
    PageInfo<Task> queryByAssign(String userId, int startPage, int pageSize, TaskQueryEntity taskQueryEntity) throws WorkFlowException;


    /**
     * 审批接口
     * 注：如是动态自己设置审批人，任务审批后要调用setApprove方法设置审批人，无法重复设置审批人
     *  @param  approveVo  审批信息封装类，具体请看ApproveVo类说明
     *  @param  paramMap   自定义参数键值对
     * @return   注意：通过工作流平台设置审批人，此方法每次都会返回processId,流程实例的id
     *                  如是动态设置审批人，在审批后如任务还未完成继续返回processId,如任务已结束将返回null
     * @exception  WorkFlowException 返回审批异常
     */
    String  completeTask(ApproveVo approveVo,Map<String,Object> paramMap) throws WorkFlowException;

    /**
     * 获取申请人提交的任务
     * @param userid  申请人信息
     * @param startPage  起始页数
     * @param pageSzie    查询多少条数
     * @param status      0 :审批中的任务
     *                    1 ：审批完成的任务
     *@param taskQueryEntity  任务查询query
     * @return    返回申请人提交的任务
     */
    List<HistoricProcessInstance> getApplyTasks(String userid, int startPage, int pageSzie, int status,TaskQueryEntity taskQueryEntity);

    /**
     * 通过用户主键查询历史审批过的任务
     * @param userId   用户主键
     * @param startPage   开始页数
     * @param pagegSize   每页显示数
     * @param taskQueryEntity 查询任务query
     *
     * @return            返回审批历史人物信息列表
     */
    PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pagegSize,TaskQueryEntity taskQueryEntity);

    /**
     * 通过用户主键查询审批拒绝的信息
     * @param userId   用户主键
     * @param startPage 开始页数
     * @param pageSize   结束页数
     * @param taskQueryEntity  查询任务query
     * @return            返回用户拒绝的信息
     */
    PageInfo<HistoricTaskInstance> selectMyRefuse(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity);

    /**
     * 获取任务审批意见列表
     * @param processInstanceId   流程任务中的processId
     * @return   返回审批意见列表
     */
    List<Comment> selectListComment(String processInstanceId);

    /**
     * 通过流程定义id获取定义变量
     * @param processId  流程定义id
     * @return  返回自定义变量map
     */
    Map<String, Object> getVariables(String processId);

    /**
     * 转办流程
     * @param taskId
     *            当前任务节点ID
     * @param userCode
     *            被转办人Code
     */
    void transferAssignee(String taskId, String userCode);


    /**
     * 流程任务跟踪标识
     * @author houjinrong@chtwm.com
     * @param processInstanceId
     * @return
     */
    byte[] getTaskSchedule(String processInstanceId);

    /**
     * 根据流程实例ID查询历史任务信息
     * @author houjinrong
     * @param processInstanceId
     * @param variableNames
     * @return
     */
    HistoryTasksVo getTaskHistoryByProcessInstanceId(String processInstanceId,List<String> variableNames);

    /**
     * 获取应用列表
     * @return
     */
    List<App> getAppList();

    /**
     * 根据应用key获取应用所属的模型列表
     * @author houjinrong@chtwm.com
     * @param appKey
     * @return
     */
    List<Model> getModelListByAppKey(String appKey);

    /**
     * 委派任务
     * @author houjinrong@chtwm.com
     * @param userId 当前任务节点ID
     * @param taskId 被委派人工号
     * @return
     */
    boolean delegateTask(String userId, String taskId);

    /**
     * 转办任务
     * @author houjinrong@chtwm.com
     * @param userId 当前任务节点ID
     * @param taskId 被转办人工号
     * @return
     */
    boolean transferTask(String taskId, String userId, String transferUserId);

    /**
     * 通过流程实例id查询最后审批人
     * @param processId
     * @return
     */
    String  getLastApprover(String processId);

    /**
     * 通过任务id查询评论
     * @param taskid
     * @return
     */
    Comment selectComment(String taskid,String userName);
    
    /**
     * 任务跳转
     * @param taskId 当前任务ID
     * @param taskDefinitionKey 跳转到的任务节点KEY
     * @return 任务ID
     * @author houjinrong@chtwm.com
     * date 2018/2/1 20:32
     */
    String taskJump(String taskId, String taskDefinitionKey) throws WorkFlowException;

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
     * @return true：成功；false：失败
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:35
     */
    boolean rollBackWorkFlow(String processInstanceId);

    /**
     * 恢复驳回的流程
     * @param processInstanceId 流程实例ID
     * @param resumeType 0：恢复到开始任务节点；1：恢复到驳回前到达的任务节点
     * @return 任务ID
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:36
     */
    String resumeWorkFlow(String processInstanceId, int resumeType, Map<String,Object> variables, String userCodes) throws WorkFlowException;
}
