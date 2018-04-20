package com.hengtian.enquire.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.enquire.dao.EnquireTaskDao;
import com.hengtian.enquire.model.EnquireTask;
import com.hengtian.enquire.service.EnquireService;
import org.springframework.stereotype.Service;

/**
 * @author chenzhangyan  on 2018/4/18.
 */
@Service
public class EnquireServiceImpl extends ServiceImpl<EnquireTaskDao, EnquireTask> implements EnquireService {
}
