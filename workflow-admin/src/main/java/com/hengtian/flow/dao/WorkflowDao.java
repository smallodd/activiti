package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hengtian.flow.model.ProcessInstanceResult;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TaskResult;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 我发起的流程实例
     *
     * @param params 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/18 9:51
     */
    List<ProcessInstanceResult> queryProcessInstance(Pagination page, Map<String, Object> params);

    /**
     * 待处理任务总数（包括待认领和待办任务）
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    Long activeTaskCount(Map<String,Object> paraMap);

    /**
     * 通过业务主键查询流程实例
     * @param appKey 系统应用KEy
     * @param businessKey 业务主键
     * @param suspensionState 挂起状态：1-激活；2-挂起
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/24 11:36
     */
    RuProcinst queryProcessInstanceByBusinessKey(@Param("appKey") Integer appKey,@Param("businessKey") String businessKey,@Param("suspensionState") Integer suspensionState);
}
