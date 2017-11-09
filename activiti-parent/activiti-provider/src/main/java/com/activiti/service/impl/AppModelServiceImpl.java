package com.activiti.service.impl;

import com.activiti.dao.AppModelDao;
import com.activiti.dao.TUserTaskDao;
import com.activiti.model.AppModel;
import com.activiti.model.TUserTask;
import com.activiti.service.AppModelService;
import com.activiti.service.TUserTaskService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Created by ma on 2017/11/9.
 */
@Service
public class AppModelServiceImpl  extends ServiceImpl<AppModelDao, AppModel> implements AppModelService {
}
