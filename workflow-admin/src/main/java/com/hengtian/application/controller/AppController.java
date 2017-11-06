package com.hengtian.application.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hengtian.application.model.App;
import com.hengtian.application.service.AppService;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.AutoCreateCodeUtil;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.system.model.SysResource;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/app")
public class AppController extends BaseController {

    @Autowired
    private AppService appService;

    @Autowired
    StrongUuidGenerator uuidGenerator;

    @RequestMapping("/manage")
    public String manage(){
        return "application/app/index";
    }

    /**
     * 应用列表
     * @author houjinrong
     * @return
     */
    @SysLog(value="查询应用")
    @PostMapping("/dataGrid")
    @ResponseBody
    public Object dataGrid() {
        return appService.selectListGrid();
    }

    @RequestMapping("/addPage")
    public String addPage(){
        return "application/app/add";
    }

    /**
     * 添加应用
     * @author houjinrong
     * @return
     */
    @SysLog(value="添加应用")
    @RequestMapping("/add")
    @ResponseBody
    public Object add(App app) {
        if(StringUtils.isNotEmpty(app.getName())) {
            EntityWrapper<App> wrapper =new EntityWrapper<App>();
            wrapper.isNotNull("name");
            App _app = appService.selectOne(wrapper);
            if(_app != null){
                return renderError("名称重复！");
            }
        }else{
            return renderError("名称为空！");
        }
        ShiroUser shiroUser = getShiroUser();
        app.setKey(uuidGenerator.getNextId());
        app.setCreateTime(new Date());
        app.setCreator(shiroUser.getId());

        app.setUpdater(shiroUser.getId());
        appService.insert(app);
        return renderSuccess("添加成功！");
    }

    /**
     * 编辑应用页
     * @return
     */
    @RequestMapping("/editPage")
    public String editPage(Model model, String id) {
        App app = appService.selectById(id);
        model.addAttribute("app", app);
        return "application/app/edit";
    }

    /**
     * 编辑应用
     * @param app
     * @return
     */
    @SysLog(value="编辑应用")
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(App app) {
        if(StringUtils.isNotEmpty(app.getId())) {
            if(StringUtils.isNotEmpty(app.getName())) {
                EntityWrapper<App> wrapper =new EntityWrapper<App>();
                wrapper.isNotNull("name");
                App _app = appService.selectOne(wrapper);
                if(_app != null && !_app.getId().equals(app.getId())){
                    return renderError("名称重复！");
                }
            }else{
                return renderError("名称为空！");
            }
        }else{
            return renderError("ID为空！");
        }
        ShiroUser shiroUser = getShiroUser();
        app.setUpdater(shiroUser.getId());
        app.setUpdateTime(new Date());
        appService.updateById(app);
        return renderSuccess("编辑成功！");
    }

    /**
     * 删除应用
     * @param id
     * @return
     */
    @SysLog(value="删除应用")
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String id) {
        appService.deleteById(id);
        return renderSuccess("删除成功！");
    }
}
