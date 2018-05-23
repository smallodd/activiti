package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hengtian.flow.model.TUserTask;

/**
 * 任务配置属性
 * @author houjinrong@chtwm.com
 * date 2018/5/9 10:37
 */
public interface TUserTaskDao extends BaseMapper<TUserTask> {

    long selectNotSetAssign(TUserTask tUserTask);
}