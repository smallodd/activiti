package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.flow.dao.ExprDao;
import com.hengtian.flow.model.Expr;
import com.hengtian.flow.service.ExprService;
import org.springframework.stereotype.Service;

@Service
public class ExprServiceImpl extends ServiceImpl<ExprDao, Expr> implements ExprService {
}
