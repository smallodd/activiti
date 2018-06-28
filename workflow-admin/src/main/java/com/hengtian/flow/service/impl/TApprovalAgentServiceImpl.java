package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.flow.dao.ExprDao;
import com.hengtian.flow.dao.TApprovalAgentDao;
import com.hengtian.flow.model.Expr;
import com.hengtian.flow.model.TApprovalAgent;
import com.hengtian.flow.service.ExprService;
import com.hengtian.flow.service.TApprovalAgentService;
import org.springframework.stereotype.Service;

@Service
public class TApprovalAgentServiceImpl extends ServiceImpl<TApprovalAgentDao, TApprovalAgent> implements TApprovalAgentService {
}
