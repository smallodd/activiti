package com.hengtian.enquire.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.TaskEnquireParam;
import com.hengtian.common.utils.BeanUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.enquire.dao.EnquireTaskDao;
import com.hengtian.enquire.model.EnquireTask;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.enquire.vo.EnquireTaskVo;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/18.
 */
@Service
public class EnquireServiceImpl extends ServiceImpl<EnquireTaskDao, EnquireTask> implements EnquireService {
    @Autowired
    private EnquireTaskDao enquireTaskDao;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

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
        fillEnquireVoList(pageInfo, page, list);
        return pageInfo;
    }

    private void fillEnquireVoList(PageInfo pageInfo, Page<EnquireTask> page, List<EnquireTask> list) {
        List<EnquireTaskVo> voList = new ArrayList<>();
        for (EnquireTask enquireTask : list) {

            EnquireTaskVo vo = new EnquireTaskVo();
            BeanUtils.copy(enquireTask, vo);

            String currentTaskKey = vo.getCurrentTaskKey();
            TaskEntity currentTask = (TaskEntity) taskService.createTaskQuery().taskDefinitionKey(currentTaskKey).singleResult();
            vo.setCurrentTaskName(currentTask.getName());

            String askTaskKey = vo.getAskTaskKey();
            TaskEntity askTask = (TaskEntity) taskService.createTaskQuery().taskDefinitionKey(askTaskKey).singleResult();
            vo.setAskTaskName(askTask.getName());

            String procInstId = vo.getProcInstId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
            vo.setProcInstName(processInstance.getName());

            String createId = vo.getCreateId();
            String askUserId = vo.getAskUserId();
            voList.add(vo);
        }
        pageInfo.setRows(voList);
        pageInfo.setTotal(page.getTotal());
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
        fillEnquireVoList(pageInfo, page, list);
        return pageInfo;
    }
}
