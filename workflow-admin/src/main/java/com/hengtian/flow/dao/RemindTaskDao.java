package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.flow.model.RemindTask;

import java.util.List;

public interface RemindTaskDao extends BaseMapper<RemindTask> {

    List<RemindTask> remindTaskList(Pagination page, TaskRemindQueryParam taskRemindQueryParam);

    List<RemindTask> remindedTaskList(Pagination page, TaskRemindQueryParam taskRemindQueryParam);
}
