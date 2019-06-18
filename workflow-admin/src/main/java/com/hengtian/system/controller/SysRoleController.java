package com.hengtian.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.utils.AutoCreateCodeUtil;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.system.model.SysRole;
import com.hengtian.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 * @author junyang.liu
 */
@Controller
@RequestMapping("/sysRole")
public class SysRoleController extends BaseController{

    @Autowired
    private SysRoleService roleService;

    /**
     * 权限管理页
     * @return
     */
    
    @GetMapping("/manager")
    public String manager() {
        return "system/role";
    }

    /**
     * 权限列表
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog(value="权限列表")
    @PostMapping("/dataGrid")
    @ResponseBody
    public Object dataGrid(Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);
        Map<String, Object> condition = new HashMap<String, Object>();
        pageInfo.setCondition(condition);

        roleService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 权限树
     * @return
     */
    @PostMapping("/tree")
    @ResponseBody
    public Object tree() {
        return roleService.selectTree();
    }

    /**
     * 添加权限页
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "system/roleAdd";
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    @SysLog(value="添加角色")
    @PostMapping("/add")
    @ResponseBody
    public Object add(SysRole role) {
    	EntityWrapper<SysRole> wrapper =new EntityWrapper<SysRole>();
        wrapper.isNotNull("role_code").orderBy("role_code", false);
        SysRole sysRole= roleService.selectList(wrapper).get(0);
        String roleCode = AutoCreateCodeUtil.autoCreateSysCode(ConstantUtils.prefixCode.NO.getValue(),sysRole.getRoleCode());
        role.setRoleCode(roleCode);
        role.setCreateTime(new Date());
        roleService.insert(role);
        return renderSuccess("添加成功！");
    }

    /**
     * 删除权限
     * @param id
     * @return
     */
    @SysLog(value="删除权限")
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String id) {
        roleService.deleteById(id);
        return renderSuccess("删除成功！");
    }

    /**
     * 编辑权限页
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/editPage")
    public String editPage(Model model, String id) {
        SysRole role = roleService.selectById(id);
        model.addAttribute("role", role);
        return "system/roleEdit";
    }

    /**
     * 编辑权限
     * @param role
     * @return
     */
    @SysLog(value="编辑权限")
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(SysRole role) {
        roleService.updateById(role);
        return renderSuccess("编辑成功！");
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
        return "system/roleGrant";
    }

    /**
     * 授权页面页面根据角色查询资源
     * @param id
     * @return
     */
    @RequestMapping("/findResourceIdListByRoleId")
    @ResponseBody
    public Object findResourceByRoleId(String id) {
        List<String> resources = roleService.selectResourceIdListByRoleId(id);
        return renderSuccess(resources);
    }

    /**
     * 授权
     * @param id
     * @param resourceIds
     * @return
     */
    @SysLog(value="授权")
    @RequestMapping("/grant")
    @ResponseBody
    public Object grant(String id, String resourceIds) {
    	roleService.updateRoleResource(id, resourceIds);
        return renderSuccess("授权成功！");
    }

}
