package com.hengtian.application.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.application.dao.AppModelDao;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import org.springframework.stereotype.Service;

/**
 * Created by ma on 2018/4/17.
 */
@Service
public class AppModelServiceImpl  extends ServiceImpl<AppModelDao, AppModel> implements AppModelService {
}
