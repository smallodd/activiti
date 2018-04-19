package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.flow.model.RemindTask;

import java.util.List;

public interface RemindTaskDao extends BaseMapper<RemindTask> {

    List<RemindTask> taskRemindList(Pagination page, TaskQueryParam taskQueryParam);

    List<RemindTask> taskRemindedList(Pagination page, TaskQueryParam taskQueryParam);
}
