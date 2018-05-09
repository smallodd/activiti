package com.hengtian.flow.controller.manage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.service.TButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/button")
public class TaskButtonController {

    @Autowired
    private TButtonService tButtonService;

    /**
     * 选择按钮权限页面
     * @author houjinrong@chtwm.com
     * date 2018/5/9 9:50
     */
    public String selectButtonPage(){
        return "workflow/config/select_button";
    }

    /**
     * 所有任务功能按钮列表
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/9 10:24
     */
    @RequestMapping("/list")
    public Object queryButton(){
        EntityWrapper<TButton> wrapper = new EntityWrapper();
        wrapper.where("status={0}",1);
        return tButtonService.selectList(wrapper);
    }
}
