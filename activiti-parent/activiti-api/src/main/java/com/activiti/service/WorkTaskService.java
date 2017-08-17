package com.activiti.service;

import com.activiti.expection.WorkFlowException;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/7/18.
 */
public interface WorkTaskService {
    /**
     * 通过用户相关信息查询待审批任务
     * @param userId  用户信息 一般是id
     * @param  startPage  起始页数
     * @param  pageSize   每页显示数
     * @return  返回任务列表
     */
    PageInfo<Task> queryByAssign(String userId, int startPage, int pageSize);



    /**
     * 通过用户id查询用户历史任务信息
     * @param userId      用户主键
     * @param startPage  起始页数
     * @param pageSize  每页显示数
     * @return 历史审批任务信息列表
     */
    List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize);

    /**
     * 审批接口
     *         注：当下一个审批人的唯一标识为空或不传时，直接完成该任务
     * @param processInstanceId  流程定义id
     * @param nextUserId  下一个审批人的用户唯一标识 一般是编码或主键
     * @param note      审批意见
     * @param authName 审批人姓名
     * @exception  WorkFlowException 返回审批异常
     */
    Boolean  completeTask(String processInstanceId,String nextUserId ,String note, String authName) throws WorkFlowException;

    /**
     * 回退到上一节点
     * @param taskId  任务id
     * @param note    审批意见
     * @return  返回成功或失败
     *          true:成功
     *          false:失败
     */
    @Deprecated
    boolean rollBack(String taskId,String note);

    /**
     * 审批不通过
     * @param processId  流程任务中的processId
     * @param reason     拒绝理由
     * @return           返回成功或失败
     *                      true:成功
     *                      false:失败
     */
    Boolean refuseTask(String processId,String reason);
    /**
     * 获取申请人提交的任务
     * @param userid  申请人信息
     * @param startPage  起始页数
     * @param pageSzie    查询多少条数

     * @return    返回申请人提交的任务
     */
    List<HistoricProcessInstance> getApplyTasks(String userid, int startPage, int pageSzie, int status);
    /**
     * 获取参与审批用户的审批历史任务
     * @param userid   审批人用户唯一标识
     * @param startPage   起始页数
     * @param pageSzie     查询多少条数

     *
     * @return    返回参与用户的审批历史信息
     */
    List<HistoricProcessInstance> getInvolvedUserCompleteTasks(String userid,int startPage,int pageSzie);

    /**
     * 通过用户主键查询历史审批过的任务
     * @param userId   用户主键
     * @param startPage   开始页数
     * @param pagegSize   每页显示数
     * @return            返回审批历史人物信息列表
     */
    PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pagegSize);

    /**
     * 通过用户主键查询审批拒绝的信息
     * @param userId   用户主键
     * @param startPage 开始页数
     * @param pageSize   结束页数
     * @return            返回用户拒绝的信息
     */
    PageInfo<HistoricTaskInstance> selectMyRefuse(String userId,int startPage,int pageSize);
    /**
     *查询任务当所在节点
     * @param processId  流程定义id
     * @return  返回图片流 ，二进制
     */
    byte[] generateImage(String processId);

    /**
     * 查询业务主键是否再流程钟
     * @param bussinessKey   业务主键
     * @return   返回true or false
     */
    boolean checekBunessKeyIsInFlow(String bussinessKey);

    /**
     * 获取当前历史任务的审批意见
     * @param taskId   任务id
     * @return  返回审批意见
     */
    Comment selectComment(String taskId);

    /**
     * 通过流程实例id查询任务审批历史信息
     * @param processId   流程任务中processId
     * @return
     */
    List<HistoricTaskInstance> selectTaskHistory(String processId);
    /**
     * 获取任务审批意见列表
     * @param processInstanceId   流程任务中的processId
     * @return
     */
    List<Comment> selectListComment(String processInstanceId);

    /**
     * 通过历史任务id查询历史任务
     * @param taskHistoryId 任务历史id
     * @return
     */
    HistoricTaskInstance selectHistoryTask(String taskHistoryId);

    /**
     * 通过流程定义id获取定义变量
     * @param processId  流程定义id
     * @return
     */
    Map<String, Object> getVariables(String processId);

    /**
     * 通过流程定义id查询下一流程
     * @param procInstanceId 流程任务中的processId
     * @return
     */
    String getNextNode(String procInstanceId);

    /**
     * 查询所有待审批的任务
     * @param startPage  开始页
     * @param pageSize    每页显示数
     * @return
     */
    PageInfo<Task> selectAllWaitApprove(int startPage,int pageSize);

    /**
     * 查询所有通过的任务
     * @param startPage  开始页
     * @param pageSize   每页显示数
     * @return
     */
    PageInfo<HistoricProcessInstance> selectAllPassApprove(int startPage, int pageSize);

    /**
     * 查询所有拒绝的任务
     * @param startPage  开始页
     * @param pageSize   每页显示数
     * @return
     */
    PageInfo<HistoricProcessInstance> selectAllRefuseApprove(int startPage,int pageSize);

    /**
     * 通过流程定义id判断活动是否通过
     * @param processId   流程定义id
     * @return   true:通过；false:拒绝
     */
    boolean checkIsPass(String processId);

    /**
     * 获取最后审批人
     * @param processId  流程中processId
     * @return  返回最后审批人userCode
     */
    String getLastApprover(String processId);

    /**
     * 加入会签
     * @param taskId  任务id
     * @param list   人员userCode列表
     */
    void jointProcess(String taskId,List<String> list);

    /**
     * 通过流程定义id查询任务
     * @param processId   流程定义id
     * @return
     */
    Task queryTaskByProcessId(String processId);
}
