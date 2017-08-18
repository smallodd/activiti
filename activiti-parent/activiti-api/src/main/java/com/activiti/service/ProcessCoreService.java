package com.activiti.service;

import com.activiti.expection.WorkFlowException;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.List;
import java.util.Map;

/**
 * 此接口不对外暴露 内部接口
 * Created by ma on 2017/7/20.
 */
public interface ProcessCoreService {
    /**
     * 审批通过(驳回直接跳回功能需后续扩展)
     *
     * @param taskId    当前任务ID
     * @param variables 流程存储参数
     * @throws Exception
     */
    void passProcess(String taskId, Map<String, Object> variables)
            throws WorkFlowException;

    /**
     * 根据当前任务ID，查询可以驳回的任务节点
     *
     * @param taskId 当前任务ID

     *  @return   返回所有可驳回的任务节点
     */
    @Deprecated
     List<ActivityImpl> findBackAvtivity(String taskId) throws Exception;
    /**
     * 驳回当前流程
     * @param taskId  流程任务id
     * @param varoables   自定义的键值
     */
    @Deprecated
     void backCurrentProcess(String taskId,Map<String,Object> varoables);
    /**
     * 取回流程
     *
     * @param taskId     当前任务ID
     * @param activityId 取回节点ID
     * @throws Exception
     */
    @Deprecated
    void callBackProcess(String taskId, String activityId)
            throws Exception;

    /**
     * 中止流程(特权人直接审批通过等)
     *
     * @param taskId   任务id
     */
    @Deprecated
    void endProcess(String taskId) throws WorkFlowException;

    /**
     * 转办流程
     *
     * @param taskId 当前任务节点ID
     * @param userid 被转办人的用户唯一标识
     */
    @Deprecated
    void transferAssignee(String taskId, String userid);

    /**
     * 加入会签
     * @param taskId  任务id
     * @param userCodes   用户code列表
     */
    @Deprecated
    void jointProcess(String taskId, List<String> userCodes) throws WorkFlowException;
}
