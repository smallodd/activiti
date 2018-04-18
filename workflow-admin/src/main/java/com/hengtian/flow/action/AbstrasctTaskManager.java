package com.hengtian.flow.action;

import com.hengtian.common.result.Result;

public interface AbstrasctTaskManager {

    /**
     * 跳转
     * @param actionType
     * @return
     */
    Result actionJump(String actionType);

    /**
     * 转办
     * @param actionType
     * @return
     */
    Result actionTransfer(String actionType);

    /**
     * 催办
     * @param actionType
     * @return
     */
    Result actionRemind(String actionType);

    /**
     * 问询
     * @param actionType
     * @return
     */
    Result actionEnquire(String actionType);

    /**
     * 确认问询
     * @param actionType
     * @return
     */
    Result actionConfirmEnquire(String actionType);

    /**
     * 撤回
     * @param actionType
     * @return
     */
    Result actionRevoke(String actionType);

    /**
     * 取消
     * @param actionType
     * @return
     */
    Result actionCancel(String actionType);

    /**
     * 挂起
     * @param actionType
     * @return
     */
    Result actionSuspend(String actionType);

    /**
     * 激活
     * @param actionType
     * @return
     */
    Result actionActivate(String actionType);
}
