package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.dao.RemindTaskDao;
import com.hengtian.flow.model.RemindTask;
import com.hengtian.flow.service.RemindTaskService;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.generator.plugins.RowBoundsPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemindTaskServiceImpl extends ServiceImpl<RemindTaskDao, RemindTask> implements RemindTaskService {

    @Autowired
    private RemindTaskDao remindTaskDao;

    @Override
    public PageInfo taskRemindList(TaskQueryParam taskQueryParam) {
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        Page<RemindTask> page = new Page(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        List<RemindTask> list = remindTaskDao.taskRemindList(page, taskQueryParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public PageInfo taskRemindedList(TaskQueryParam taskQueryParam) {
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        Page<RemindTask> page = new Page(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        List<RemindTask> list = remindTaskDao.taskRemindedList(page, taskQueryParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }
}
