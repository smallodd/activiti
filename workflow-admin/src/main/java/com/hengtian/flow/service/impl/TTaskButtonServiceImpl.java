package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.flow.dao.TTaskButtonDao;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TTaskButton;
import com.hengtian.flow.service.TTaskButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ma on 2018/5/8.
 */
@Service
public class TTaskButtonServiceImpl extends ServiceImpl<TTaskButtonDao, TTaskButton> implements TTaskButtonService{
    @Autowired
    TTaskButtonDao tTaskButtonDao;
    @Override
    public List<TButton> selectTaskButtons(String procDefKey, String taskDefKey) {
        return tTaskButtonDao.selectTaskButtons(procDefKey,taskDefKey);
    }
}
