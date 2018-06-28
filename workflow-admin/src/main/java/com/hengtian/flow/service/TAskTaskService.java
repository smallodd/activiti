package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TAskTask;

/**
 * Created by ma on 2018/4/19.
 */
public interface TAskTaskService extends IService<TAskTask> {
    /**
     * 意见征询任务列表
     *
     * @param taskEnquireParam 查询参数
     *                         createId   意见征询人id
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    PageInfo enquireTaskList(AskTaskParam taskEnquireParam);


    /**
     * 被意见征询任务列表
     *
     * @param taskEnquireParam 查询参数
     *                         askUserId   被意见征询的人id
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    PageInfo enquiredTaskList(AskTaskParam taskEnquireParam);
}
