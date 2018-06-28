package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.dao.RemindTaskDao;
import com.hengtian.flow.model.RemindTask;
import com.hengtian.flow.service.RemindTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemindTaskServiceImpl extends ServiceImpl<RemindTaskDao, RemindTask> implements RemindTaskService {

    @Autowired
    private RemindTaskDao remindTaskDao;

    /**
     * 催办任务列表
     * @param taskRemindQueryParam 催办任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:28
     */
    @Override
    public PageInfo remindTaskList(TaskRemindQueryParam taskRemindQueryParam) {
        PageInfo pageInfo = new PageInfo(taskRemindQueryParam.getPageNum(), taskRemindQueryParam.getPageSize());
        Page<RemindTask> page = new Page(taskRemindQueryParam.getPageNum(), taskRemindQueryParam.getPageSize());
        List<RemindTask> list = remindTaskDao.remindTaskList(page, taskRemindQueryParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    /**
     * 被催办任务列表
     * @param taskRemindQueryParam 催办任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:28
     */
    @Override
    public PageInfo remindedTaskList(TaskRemindQueryParam taskRemindQueryParam) {
        PageInfo pageInfo = new PageInfo(taskRemindQueryParam.getPageNum(), taskRemindQueryParam.getPageSize());
        Page<RemindTask> page = new Page(taskRemindQueryParam.getPageNum(), taskRemindQueryParam.getPageSize());
        List<RemindTask> list = remindTaskDao.remindedTaskList(page, taskRemindQueryParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }
}
