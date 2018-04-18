package com.hengtian.flow.service;

import com.hengtian.common.result.Result;

public interface WorkflowService {

    /**
     * 跳转
     * @param actionType
     * @return
     */
    Result taskJump(String actionType);

    /**
     * 转办
     * @param actionType
     * @return
     */
    Result taskTransfer(String actionType);

    /**
     * 催办
     * @param actionType
     * @return
     */
    Result taskRemind(String actionType);

    /**
     * 问询
     * @param actionType
     * @return
     */
    Result taskEnquire(String actionType);

    /**
     * 确认问询
     * @param actionType
     * @return
     */
    Result taskConfirmEnquire(String actionType);

    /**
     * 撤回
     * @param actionType
     * @return
     */
    Result taskRevoke(String actionType);

    /**
     * 取消
     * @param actionType
     * @return
     */
    Result taskCancel(String actionType);

    /**
     * 挂起
     * @param actionType
     * @return
     */
    Result taskSuspend(String actionType);

    /**
     * 激活
     * @param actionType
     * @return
     */
    Result taskActivate(String actionType);
}
