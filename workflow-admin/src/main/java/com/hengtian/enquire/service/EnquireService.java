package com.hengtian.enquire.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.TaskEnquireParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.enquire.model.EnquireTask;

/**
 * @author chenzhangyan  on 2018/4/18.
 */
public interface EnquireService extends IService<EnquireTask> {

    /**
     * 问询任务列表
     *
     * @param taskEnquireParam 查询参数
     *                         createId   问询人id
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    PageInfo enquireTaskList(TaskEnquireParam taskEnquireParam);


    /**
     * 被问询任务列表
     *
     * @param taskEnquireParam 查询参数
     *                         askUserId   被问询的人id
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    PageInfo enquiredTaskList(TaskEnquireParam taskEnquireParam);
}
