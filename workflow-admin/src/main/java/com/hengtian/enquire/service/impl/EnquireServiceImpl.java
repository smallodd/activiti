package com.hengtian.enquire.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.TaskEnquireParam;
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

    /**
     * 问询任务列表
     *
     * @param taskEnquireParam 查询参数
     *                         createId   操作人ID
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    @Override
    public PageInfo enquireTaskList(TaskEnquireParam taskEnquireParam) {
        PageInfo pageInfo = new PageInfo(taskEnquireParam.getPageNum(), taskEnquireParam.getPageSize());
        Page<EnquireTask> page = new Page(taskEnquireParam.getPageNum(), taskEnquireParam.getPageSize());
        List<EnquireTask> list = enquireTaskDao.enquireTaskList(page, taskEnquireParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    /**
     * 被问询任务列表
     *
     * @param taskEnquireParam 查询参数
     *                         askUserId   被问询的人id
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    @Override
    public PageInfo enquiredTaskList(TaskEnquireParam taskEnquireParam) {
        PageInfo pageInfo = new PageInfo(taskEnquireParam.getPageNum(), taskEnquireParam.getPageSize());
        Page<EnquireTask> page = new Page(taskEnquireParam.getPageNum(), taskEnquireParam.getPageSize());
        List<EnquireTask> list = enquireTaskDao.enquiredTaskList(page, taskEnquireParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }
}
