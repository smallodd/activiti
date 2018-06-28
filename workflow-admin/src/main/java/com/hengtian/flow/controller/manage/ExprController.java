package com.hengtian.flow.controller.manage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.enums.ExprStatusEnum;
import com.hengtian.flow.model.Expr;
import com.hengtian.flow.service.ExprService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 表达式
 * @author houjinrong@chtwm.com
 * date 2018/5/11 9:48
 */
@Controller
@RequestMapping("/expr")
public class ExprController {

    @Autowired
    private ExprService exprService;

    /**
     * 表达式选择页面
     * @author houjinrong@chtwm.com
     * date 2018/5/11 9:51
     */
    @GetMapping("/select")
    public String selectExprPage(){
        return "workflow/config/select_expr";
    }

    /**
     * 表达式列表
     * @author houjinrong@chtwm.com
     * date 2018/5/11 10:02
     */
    @PostMapping("/list")
    @ResponseBody
    public Object queryExpr(){
        EntityWrapper<Expr> wrapper = new EntityWrapper();
        wrapper.where("status={0}", ExprStatusEnum.USABLE.status);
        List<Expr> exprs = exprService.selectList(wrapper);
        return exprs;
    }
}
