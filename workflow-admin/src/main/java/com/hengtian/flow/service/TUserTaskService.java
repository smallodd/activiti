package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.result.Result;
import com.hengtian.flow.model.TUserTask;

/**
 * <p>
 * 用户任务表  服务类
 * </p>
 * @author junyang.liu
 */
public interface TUserTaskService extends IService<TUserTask> {

    /**
     * 任务节点配置（包括审批人，权限按钮）
     * @author houjinrong@chtwm.com
     * date 2018/5/9 10:28
     */
    Result config(String configJsonStr);

    long selectNotSetAssign(TUserTask tUserTask);
}
