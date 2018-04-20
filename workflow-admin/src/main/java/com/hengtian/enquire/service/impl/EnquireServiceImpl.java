package com.hengtian.enquire.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.enquire.dao.EnquireTaskDao;
import com.hengtian.enquire.model.EnquireTask;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.flow.model.RemindTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/18.
 */
@Service
public class EnquireServiceImpl extends ServiceImpl<EnquireTaskDao, EnquireTask> implements EnquireService {
    @Autowired
    private EnquireTaskDao enquireTaskDao;

    @Override
    public PageInfo enquireTaskList(TaskQueryParam taskQueryParam) {
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        Page<EnquireTask> page = new Page(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        List<EnquireTask> list = enquireTaskDao.enquireTaskList(page, taskQueryParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public PageInfo enquiredTaskList(TaskQueryParam taskQueryParam) {
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        Page<EnquireTask> page = new Page(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        List<EnquireTask> list = enquireTaskDao.enquiredTaskList(page, taskQueryParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }
}
