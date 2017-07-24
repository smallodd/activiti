package com.activiti.service;

import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/7/20.
 */
public interface ProcessCoreService {
    /**
     * 审批通过(驳回直接跳回功能需后续扩展)
     *
     * @param taskId
     *            当前任务ID
     * @param variables
     *            流程存储参数
     * @throws Exception
     */
    void passProcess(String taskId, Map<String, Object> variables)
            throws Exception;
    /**
     * 根据当前任务ID，查询可以驳回的任务节点
     *
     * @param taskId
     *            当前任务ID
     */
    @Deprecated
     List<ActivityImpl> findBackAvtivity(String taskId) throws Exception;
    @Deprecated
     void backCurrentProcess(String taskId,Map<String,Object> varoables);
    /**
     * 取回流程
     *
     * @param taskId
     *            当前任务ID
     * @param activityId
     *            取回节点ID
     * @throws Exception
     */
    @Deprecated
    void callBackProcess(String taskId, String activityId)
            throws Exception;
    /**
     * 中止流程(特权人直接审批通过等)
     *
     * @param taskId
     */
    @Deprecated
     void endProcess(String taskId) throws Exception;

    /**
     * 转办流程
     *
     * @param taskId
     *            当前任务节点ID
     * @param userid
     *            被转办人的用户唯一标识
     */
    @Deprecated
    void transferAssignee(String taskId, String userid);
}
