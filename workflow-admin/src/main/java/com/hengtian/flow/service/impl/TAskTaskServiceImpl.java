package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.utils.BeanUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.dao.TAskTaskDao;
import com.hengtian.flow.model.TAskTask;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.vo.AskTaskVo;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ma on 2018/4/19.
 */
@Service
public class TAskTaskServiceImpl extends ServiceImpl<TAskTaskDao, TAskTask> implements TAskTaskService {
    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TAskTaskDao tAskTaskDao;

    /**
     * 问询任务列表
     *
     * @param askTaskParam 查询参数
     *                         createId   操作人ID
     *                         pageNum 当前页数
     *                         pageSize 每页条数
     * @return
     */
    @Override
    public PageInfo enquireTaskList(AskTaskParam askTaskParam) {
        PageInfo pageInfo = new PageInfo(askTaskParam.getPageNum(), askTaskParam.getPageSize());
        Page<TAskTask> page = new Page(askTaskParam.getPageNum(), askTaskParam.getPageSize());
        List<TAskTask> list = tAskTaskDao.enquireTaskList(page, askTaskParam);
        fillEnquireVoList(pageInfo, page, list);
        return pageInfo;
    }

    private void fillEnquireVoList(PageInfo pageInfo, Page<TAskTask> page, List<TAskTask> list) {
        List<AskTaskVo> voList = new ArrayList<>();
        for (TAskTask enquireTask : list) {

            AskTaskVo vo = new AskTaskVo();
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
    public PageInfo enquiredTaskList(AskTaskParam taskEnquireParam) {
        PageInfo pageInfo = new PageInfo(taskEnquireParam.getPageNum(), taskEnquireParam.getPageSize());
        Page<TAskTask> page = new Page(taskEnquireParam.getPageNum(), taskEnquireParam.getPageSize());
        List<TAskTask> list = tAskTaskDao.enquiredTaskList(page, taskEnquireParam);
        fillEnquireVoList(pageInfo, page, list);
        return pageInfo;
    }
}
