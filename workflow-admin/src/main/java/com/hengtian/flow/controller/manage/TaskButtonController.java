package com.hengtian.flow.controller.manage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.enums.ButtonStatusEnum;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.service.TButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    @GetMapping("/select")
    public String selectButtonPage(){
        return "workflow/config/select_button";
    }

    /**
     * 所有任务功能按钮列表
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/9 10:24
     */
    @PostMapping("/list")
    @ResponseBody
    public Object queryButton(){
        EntityWrapper<TButton> wrapper = new EntityWrapper();
        wrapper.where("status={0}", ButtonStatusEnum.USABLE.status);
        List<TButton> tButtons = tButtonService.selectList(wrapper);
        return tButtons;
    }
}
