package com.hengtian.enquire.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.enquire.model.EnquireTask;

/**
 * @author chenzhangyan  on 2018/4/18.
 */
public interface EnquireService extends IService<EnquireTask> {

    /**
     * 问询任务列表
     *
     * @param taskQueryParam 查询参数
     *                       userId   操作人ID
     *                       pageNum
     *                       当前页数 pageSize 每页条数
     * @return
     */
    PageInfo enquireTaskList(TaskQueryParam taskQueryParam);

    /**
     * 被问询任务列表
     *
     * @param taskQueryParam 查询参数
     *                       userId   操作人ID
     *                       pageNum
     *                       当前页数 pageSize 每页条数
     * @return
     */
    PageInfo enquiredTaskList(TaskQueryParam taskQueryParam);

}
