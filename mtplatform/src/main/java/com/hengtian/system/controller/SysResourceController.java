package com.hengtian.system.controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.AutoCreateCodeUtil;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.system.model.SysResource;
import com.hengtian.system.service.SysResourceService;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author junyang.liu
 */
@Controller
@RequestMapping("/sysResource")
public class SysResourceController extends BaseController{

    @Autowired
    private SysResourceService resourceService;

    /**
     * 菜单树
     * @return
     */
    @SysLog(value="菜单树")
    @PostMapping("/tree")
    @ResponseBody
    public Object tree() {
        ShiroUser shiroUser = getShiroUser();
        return resourceService.selectTree(shiroUser);
    }

    /**
     * 资源管理页
     * @return
     */
    
    @GetMapping("/manager")
    public String manager() {
        return "system/resource";
    }

    /**
     * 资源管理列表
     * @return
     */
    @SysLog(value="资源管理列表")
    @PostMapping("/treeGrid")
    @ResponseBody
    public Object treeGrid() {
        return resourceService.selectAll();
    }

    /**
     * 添加资源页
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "system/resourceAdd";
    }

    /**
     * 添加资源
     * @param resource
     * @return
     */
    @SysLog(value="添加资源")
    @RequestMapping("/add")
    @ResponseBody
    public Object add(SysResource resource) {
        resource.setCreateTime(new Date());
        EntityWrapper<SysResource> wrapper =new EntityWrapper<SysResource>();
        wrapper.isNotNull("resource_code").orderBy("resource_code", false);
        SysResource sysResource= resourceService.selectList(wrapper).get(0);
        String resourceCode = AutoCreateCodeUtil.autoCreateSysCode(ConstantUtils.prefixCode.NO.getValue(),sysResource.getResourceCode());
        resource.setResourceCode(resourceCode);
        resourceService.insert(resource);
        return renderSuccess("添加成功！");
    }

    /**
     * 查询所有的菜单
     */
    @SysLog(value="查询所有的菜单")
    @RequestMapping("/allTree")
    @ResponseBody
    public Object allMenu() {
        return resourceService.selectAllMenu();
    }

    /**
     * 查询所有的资源tree
     */
    @RequestMapping("/allTrees")
    @ResponseBody
    public Object allTree() {
        return resourceService.selectAllTree();
    }

    /**
     * 编辑资源页
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/editPage")
    public String editPage(Model model, String id) {
    	SysResource resource = resourceService.selectById(id);
        model.addAttribute("resource", resource);
        return "system/resourceEdit";
    }

    /**
     * 编辑资源
     * @param resource
     * @return
     */
    @SysLog(value="编辑资源")
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(SysResource resource) {
        resourceService.updateById(resource);
        return renderSuccess("编辑成功！");
    }

    /**
     * 删除资源
     * @param id
     * @return
     */
    @SysLog(value="删除资源")
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String id) {
        resourceService.deleteById(id);
        return renderSuccess("删除成功！");
    }

}
