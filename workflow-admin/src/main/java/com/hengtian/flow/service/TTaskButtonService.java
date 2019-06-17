package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TTaskButton;

import java.util.List;

/**
 * Created by ma on 2018/5/8.
 */
public interface TTaskButtonService extends IService<TTaskButton> {

    List<TButton> selectTaskButtons(String procDefKey, String taskDefKey);
}
