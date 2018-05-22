package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TTaskButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ma on 2018/5/8.
 */
public interface TTaskButtonDao extends BaseMapper<TTaskButton> {
    List<TButton> selectTaskButtons(@Param(value = "procDefKey") String procDefKey,@Param(value = "taskDefKey") String taskDefKey);
}
