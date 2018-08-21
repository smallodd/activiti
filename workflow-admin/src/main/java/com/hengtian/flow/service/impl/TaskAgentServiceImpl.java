package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.flow.dao.TaskAgentDao;
import com.hengtian.flow.model.TaskAgent;
import com.hengtian.flow.service.TaskAgentService;
import org.springframework.stereotype.Service;

@Service
public class TaskAgentServiceImpl extends ServiceImpl<TaskAgentDao, TaskAgent> implements TaskAgentService{
}
