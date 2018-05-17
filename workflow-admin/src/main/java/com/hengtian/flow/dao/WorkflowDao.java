package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hengtian.flow.model.TaskResult;

import java.util.List;
import java.util.Map;

public interface WorkflowDao extends BaseMapper<TaskResult> {

    /**
     * 待处理任务（包括待认领和待办任务）
     *
     * @param params 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    List<TaskResult> queryActiveTask(Pagination page, Map<String, Object> params);

    /**
     * 未办任务列表
     *
     * @param params 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    List<TaskResult> queryOpenTask(Pagination page, Map<String, Object> params);

    /**
     * 已办任务列表
     *
     * @param params 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    List<TaskResult> queryCloseTask(Pagination page, Map<String, Object> params);

    /**
     * 待认领任务列表， 任务签收后变为待办任务，待办任务可取消签认领
     *
     * @param params 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    List<TaskResult> queryClaimTask(Pagination page, Map<String, Object> params);
}
