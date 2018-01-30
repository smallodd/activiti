package com.activiti.service;

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
public interface WorkTaskNewService {

    /**
     * 开启任务
     * @param commonVo
     * @param paramMap   流程定义中线上的参数，键是线上的键
     * @return  返回部署的任务id
     */
    String startTask(CommonVo commonVo,Map<String,Object> paramMap) throws WorkFlowException;

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
     * 通过用户id查询用户历史任务信息
     * @param userId      用户主键
     * @param startPage  起始页数
     * @param pageSize  每页显示数
     * @param  type      查询历史任务类型
     *                   0：未完成的历史任务
     *                   1：已完成的历史任务
     *                  -1：全部历史任务
     * @param taskQueryEntity  任务查询query
     * @return 历史审批任务信息列表
     */
    List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize,TaskQueryEntity taskQueryEntity,int type);

    /**
     * 审批接口
     * @param processId  proc_inst_id值
     * @param currentUser  当前审批人信息
     * @param commentResult 审批类型
     *                     2  审批通过
     *                     3 审批拒绝
     * @param commentContent    审批意见
     * @return   返回 processId
     * @exception  WorkFlowException 返回审批异常
     */
    String  completeTask(String processId,String currentUser ,String commentContent, String commentResult) throws WorkFlowException;

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
     * 获取用户涉及的审批历史任务
     * @param userid   审批人用户唯一标识
     * @param startPage   起始页数
     * @param pageSzie     查询多少条数
     * @param  taskQueryEntity  查询任务query
     * @return    返回参与用户的审批历史信息
     */
    List<HistoricProcessInstance> getInvolvedUserCompleteTasks(String userid,int startPage,int pageSzie,TaskQueryEntity taskQueryEntity);

    /**
     * 通过用户主键查询历史审批过的任务
     * @param userId   用户主键
     * @param startPage   开始页数
     * @param pagegSize   每页显示数
     * @param taskQueryEntity 查询任务query
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
     * 查询业务主键是否再流程钟
     * @param taskQueryEntity  任务查询query
     * @param bussinessKey 业务主键
     * @return   返回true or false
     */
    boolean checkBunessKeyIsInFlow(TaskQueryEntity taskQueryEntity,String bussinessKey);

    /**
     * 通过流程实例id查询任务审批历史信息
     * @param processId   流程任务中processId
     * @return  返回历史审批信息列表
     */
    List<HistoricTaskInstance> selectTaskHistory(String processId);

    /**
     * 获取任务审批意见列表
     * @param processInstanceId   流程任务中的processId
     * @return   返回审批意见列表
     */
    List<Comment> selectListComment(String processInstanceId);

    /**
     * 通过历史任务id查询历史任务
     * @param taskHistoryId 任务历史id
     * @return   返回历史任务信息
     */
    HistoricTaskInstance selectHistoryTask(String taskHistoryId);

    /**
     * 通过流程定义id获取定义变量
     * @param processId  流程定义id
     * @return  返回自定义变量map
     */
    Map<String, Object> getVariables(String processId);

    /**
     * 通过流程定义id查询下一流程
     * @param procInstanceId 流程任务中的processId
     * @return  返回下一节点名称
     */
    String getNextNode(String procInstanceId);

    /**
     * 查询所有待审批的任务
     * @param startPage  开始页
     * @param pageSize    每页显示数
     * @param  bussinessType  业务系统键
     * @return   分页显示审批任务列表
     */
    PageInfo<Task> selectAllWaitApprove(int startPage,int pageSize,String bussinessType);

    /**
     * 查询所有通过的任务
     * @param startPage  开始页
     * @param pageSize   每页显示数
     * @param  bussinessType  业务系统键
     * @return  分页显示审批通过任务列表
     */
    PageInfo<HistoricProcessInstance> selectAllPassApprove(int startPage, int pageSize,String bussinessType);

    /**
     * 查询所有拒绝的任务
     * @param startPage  开始页
     * @param pageSize   每页显示数
     * @param  bussinessType  业务系统键
     * @return  分页显示所有拒绝的任务列表
     */
    PageInfo<HistoricProcessInstance> selectAllRefuseApprove(int startPage,int pageSize,String bussinessType);

    /**
     * 通过流程定义id判断活动是否通过
     * @param processId   流程定义id
     * @return   true:通过；false:拒绝
     */
    boolean checkIsPass(String processId);


    /**
     * 通过流程定义id查询任务
     * @param processId   流程定义id
     * @return  返回任务
     */
    Task queryTaskByProcessId(String processId);

    /**
     * 通过id查询历史任务实例
     * @param processId  流程定义key
     * @return  返回历史任务实例
     */
    HistoricProcessInstance  queryProcessInstance(String processId);

    /**
     * 转办流程
     * @param taskId 当前任务节点ID
     * @param userCode 被转办人Code
     */
    void transferAssignee(String taskId, String userCode);

    /**
     * 会签操作
     * @param taskId 当前任务ID
     * @param userCodes 会签人账号集合
     * @throws Exception
     */
    void jointProcess(String taskId, List<String> userCodes) throws Exception;

    /**
     * 流程任务跟踪标识
     * @author houjinrong
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
     * @author houjinrong
     * @param appKey
     * @return
     */
    List<Model> getModelListByAppKey(String appKey);

    /**
     * 委派任务
     * @author houjinrong
     * @param userId 当前任务节点ID
     * @param taskId 被委派人工号
     * @return
     */
    boolean delegateTask(String userId, String taskId);

    /**
     * 转办任务
     * @author houjinrong
     * @param userId 当前任务节点ID
     * @param taskId 被转办人工号
     * @return
     */
    boolean transferTask(String userId, String taskId);

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
}
