package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.flow.model.TAskTask;

import java.util.List;


/**
 * Created by ma on 2018/4/19.
 */
public interface TAskTaskDao extends BaseMapper<TAskTask> {
    List<TAskTask> enquireTaskList(Page<TAskTask> page, AskTaskParam taskEnquireParam);

    List<TAskTask> enquiredTaskList(Page<TAskTask> page, AskTaskParam taskEnquireParam);
}
