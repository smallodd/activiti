package com.hengtian.application.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hengtian.application.model.App;
import com.hengtian.application.service.AppService;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.shiro.ShiroUser;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

/**
 * 功能描述:系统应用
 * @Author: houjinrong@chtwm.com
 * @Date: 2019/6/17 16:30
 */
@Controller
@RequestMapping("/app")
public class AppController extends BaseController {

    @Autowired
    private AppService appService;

    @Autowired
    private RepositoryService repositoryService;

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
            App _app = new App();
            _app.setName(app.getName());
            EntityWrapper<App> wrapper =new EntityWrapper<App>(_app);
            wrapper.isNotNull("name");
            _app = appService.selectOne(wrapper);
            if(_app != null){
                return renderError("名称重复！");
            }
        }else{
            return renderError("名称为空！");
        }
        if(StringUtils.isNotEmpty(app.getKey())){
            App _app = new App();
            _app.setKey(app.getKey());
            EntityWrapper<App> wrapper =new EntityWrapper<App>(_app);
            wrapper.isNotNull("`key`");
            _app = appService.selectOne(wrapper);
            if(_app != null){
                return renderError("KEY重复！");
            }
        }else{
            return renderError("KEY为空！");
        }
        ShiroUser shiroUser = getShiroUser();
        app.setCreateTime(new Date());
        app.setCreator(shiroUser.getId());
        app.setStatus(1);
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
                App _app = new App();
                _app.setName(app.getName());
                EntityWrapper<App> wrapper =new EntityWrapper<App>(_app);
                wrapper.isNotNull("name");
                _app = appService.selectOne(wrapper);
                if(_app != null && !_app.getId().equals(app.getId())){
                    return renderError("名称重复！");
                }
            }else{
                return renderError("名称为空！");
            }
            if(StringUtils.isNotEmpty(app.getKey())) {
                App _app = new App();
                _app.setKey(app.getKey());
                EntityWrapper<App> wrapper =new EntityWrapper<App>(_app);
                wrapper.isNotNull("`key`");
                _app = appService.selectOne(wrapper);
                if(_app != null && !_app.getId().equals(app.getId())){
                    return renderError("KEY重复！");
                }
            }else{
                return renderError("KEY为空！");
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
        if(StringUtils.isEmpty(id)){
            return renderError("ID为空，删除失败！");
        }
        String sql = "SELECT COUNT(*) FROM `ACT_RE_MODEL` AS arm,`t_app_model` AS tam,`t_app` AS ta WHERE ta.ID='"+id+"' AND ta.KEY=tam.APP_KEY AND arm.KEY_=tam.MODEL_KEY ";
        sql = "SELECT COUNT(*) FROM `t_app` AS ta,`t_app_model` AS tam WHERE ta.ID='"+id+"' AND ta.KEY=tam.APP_KEY ";
        long count = repositoryService.createNativeModelQuery().sql(sql).count();

        if(count == 0){
            boolean b = appService.deleteById(id);
            if(b){
                return renderSuccess("删除成功！");
            }else{
                return renderError("删除失败！");
            }
        }else{
            return renderError("该应用使用中，无法进行删除操作");
        }
    }

    /**
     * 授权页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/grantPage")
    public String grantPage(Model model, String id) {
        model.addAttribute("id", id);
        return "application/app/grant";
    }

    /**
     * 授权页面页面根据应用查询模型
     * @param id
     * @return
     */
    @RequestMapping("/findModelKeyListByAppId")
    @ResponseBody
    public Object findModelKeyListByAppId(String id) {
        Map resources = appService.findModelKeyListByAppId(id);
        return renderSuccess(resources);
    }

    /**
     * 授权
     * @param id
     * @param modelKeys
     * @return
     */
    @SysLog(value="授权")
    @RequestMapping("/grant")
    @ResponseBody
    public Object grant(String id, String modelKeys) {
        App app = appService.selectById(id);
        if(app == null){
            return renderError("授权失败！");
        }
        appService.updateAppModel(app.getKey(), modelKeys);
        return renderSuccess("授权成功！");
    }

    /**
     * 启用
     * @param id
     * @return
     */
    @SysLog(value="启用")
    @RequestMapping("/active")
    @ResponseBody
    public Object active(String id) {
        if(StringUtils.isEmpty(id)){
            return renderError("启用失败！");
        }
        App app = new App();
        app.setId(id);
        app.setStatus(1);
        boolean b = appService.updateById(app);
        if(b){
            return renderSuccess("启用成功！");
        }else{
            return renderError("启用失败！");
        }
    }

    /**
     * 禁用
     * @param id
     * @return
     */
    @SysLog(value="禁用")
    @RequestMapping("/sleep")
    @ResponseBody
    public Object sleep(String id) {
        if(StringUtils.isEmpty(id)){
            return renderError("禁用失败！");
        }
        App app = new App();
        app.setId(id);
        app.setStatus(0);
        boolean b = appService.updateById(app);
        if(b){
            return renderSuccess("禁用成功！");
        }else{
            return renderError("禁用失败！");
        }
    }
}
