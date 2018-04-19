package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.flow.dao.TAskTaskDao;
import com.hengtian.flow.dao.TRuTaskDao;
import com.hengtian.flow.model.TAskTask;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.service.TAskTaskService;
import org.springframework.stereotype.Service;

/**
 * Created by ma on 2018/4/19.
 */
@Service
public class TAskTaskServiceImpl extends ServiceImpl<TAskTaskDao, TAskTask> implements TAskTaskService {
}
