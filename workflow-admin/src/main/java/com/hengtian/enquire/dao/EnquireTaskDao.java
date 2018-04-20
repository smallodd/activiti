package com.hengtian.enquire.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.enquire.model.EnquireTask;

import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/20.
 */
public interface EnquireTaskDao extends BaseMapper<EnquireTask> {
    List<EnquireTask> enquireTaskList(Page<EnquireTask> page, TaskQueryParam taskQueryParam);

    List<EnquireTask> enquiredTaskList(Page<EnquireTask> page, TaskQueryParam taskQueryParam);
}
