package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.flow.dao.TButtonDao;
import com.hengtian.flow.dao.TUserTaskDao;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TButtonService;
import com.hengtian.flow.service.TUserTaskService;
import org.springframework.stereotype.Service;

/**
 * Created by ma on 2018/5/8.
 */
@Service
public class TButtonServiceImpl extends ServiceImpl<TButtonDao, TButton> implements TButtonService {
}
