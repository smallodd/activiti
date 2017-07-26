package com.activiti.service;

import com.activiti.expection.WorkFlowException;
import com.github.pagehelper.PageInfo;
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
     * @return  返回任务列表
     */
    PageInfo<Task> queryByAssign(String userId, int startPage, int pageSize);



    /**
     * 通过用户id查询用户历史任务信息
     * @param userId
     * @param startPage
     * @param pageSize
     * @return
     */
    List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize);

    /**
     * 审批接口
     *         注：当下一个审批人的唯一标识为空或不传时，直接完成该任务
     * @param processInstanceId  流程定义id
     * @param nextUserId  下一个审批人的用户唯一标识 一般是编码或主键
     * @param note 审批意见
     * @param authName 审批人姓名
     */
     void  completeTask(String processInstanceId,String nextUserId ,String note, String authName) throws WorkFlowException;

    /**
     * 回退到上一节点
     * @param taskId  任务id
     * @param note    审批意见
     * @return
     */
    @Deprecated
    boolean rollBack(String taskId,String note);

    /**
     * 审批不通过
     * @param processId
     * @param reason
     * @return
     */
    Boolean refuseTask(String processId,String reason);
    /**
     * 获取申请人提交的任务
     * @param userid  申请人信息
     * @param startCloum  数据库起始行数
     * @param pageSzie    查询多少条数

     * @return
     */
    List<HistoricProcessInstance> getApplyTasks(String userid, int startCloum, int pageSzie, int status);
    /**
     * 获取参与审批用户的审批历史信息
     * @param userid   审批人用户唯一标识
     * @param startCloum   数据库开始行数
     * @param pageSzie     查询多少条数

     *
     * @return
     */
    List<HistoricProcessInstance> getInvolvedUserTasks(String userid,int startCloum,int pageSzie);
    /**
     *查询任务当所在节点
     * @param taskid  任务id
     * @return  返回图片流
     */
    InputStream generateImage(String taskid);

    /**
     * 查询业务主键是否再流程钟
     * @param bussinessKey
     * @return
     */
    boolean checekBunessKeyIsInFlow(String bussinessKey);

    /**
     * 获取上一次审批意见信息
     * @param processInstanceId
     * @return
     */
    Comment selectComment(String processInstanceId);

    /**
     * 获取任务审批意见列表
     * @param processInstanceId
     * @return
     */
    List<Comment> selectListComment(String processInstanceId);

    /**
     * 通过流程定义id获取定义变量
     * @param processId  流程定义id
     * @return
     */
    Map<String, Object> getVariables(String processId);
}
