package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.RemindTask;

/**
 * 任务催办
 * @author houjinrong@chtwm.com
 * date 2018/4/19 10:14
 */
public interface RemindTaskService extends IService<RemindTask> {

    /**
     * 催办任务列表
     * @param taskRemindQueryParam 催办任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:28
     */
    PageInfo remindTaskList(TaskRemindQueryParam taskRemindQueryParam);

    /**
     * 被催办任务列表
     * @param taskRemindQueryParam 催办任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:28
     */
    PageInfo remindedTaskList(TaskRemindQueryParam taskRemindQueryParam);
}
