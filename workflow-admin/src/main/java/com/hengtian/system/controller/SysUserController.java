package com.hengtian.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.utils.DigestUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.system.model.SysRole;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysUserService;
import com.hengtian.system.vo.SysUserVo;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author junyang.liu
 */
@Controller
@RequestMapping("/sysUser")
public class SysUserController extends BaseController{
    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户管理页
     * @return
     */
    @GetMapping("/manager")
    public String manager() {
        return "system/user";
    }
    /**
     * 用户列表
     * @param userVo
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog(value="查询用户列表")
    @PostMapping("/selectDataGrid")
    @ResponseBody
    public Object selectDataGrid(SysUserVo userVo, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows);
        Map<String, Object> condition = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(userVo.getUserName())) {
            condition.put("userName", "%"+userVo.getUserName()+"%");
        }

        if(StringUtils.isNotBlank(userVo.getDepartmentId())){
            condition.put("departmentId", userVo.getDepartmentId());
        }
        pageInfo.setCondition(condition);
        sysUserService.selectDataGridAlert(pageInfo);
        return pageInfo;
    }
    /**
     * 用户管理列表
     * @param userVo
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog(value="查询用户列表")
    @PostMapping("/dataGrid")
    @ResponseBody
    public Object dataGrid(SysUserVo userVo, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows);
        Map<String, Object> condition = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(userVo.getUserName())) {
            condition.put("userName", "%"+userVo.getUserName()+"%");
        }
        
        if(StringUtils.isNotBlank(userVo.getDepartmentId())){
        	condition.put("departmentId", userVo.getDepartmentId());
        }
        pageInfo.setCondition(condition);
        sysUserService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 添加用户页
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "system/userAdd";
    }

    /**
     * 添加用户
     * @param userVo
     * @return
     */
    @SysLog(value="添加用户")
    @PostMapping("/add")
    @ResponseBody
    public Object add(SysUserVo userVo) {
        List<SysUser> list = sysUserService.selectByLoginName(userVo);
        if (list != null && !list.isEmpty()) {
            return renderError("用户名已存在!");
        }
        userVo.setLoginPwd(DigestUtils.md5Hex(userVo.getLoginPwd().toUpperCase()));
        sysUserService.insertByVo(userVo);
        return renderSuccess("添加成功");
    }

    /**
     * 编辑用户页
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/editPage")
    public String editPage(Model model, String id) {
        SysUserVo userVo = sysUserService.selectVoById(id);
        List<SysRole> rolesList = userVo.getRoleList();
        List<String> ids = new ArrayList<String>();
        for (SysRole role : rolesList) {
            ids.add(role.getId());
        }
        userVo.setRoleIds(String.valueOf(ids).replace("[", "").replace("]", "").replace(" ", ""));
        model.addAttribute("user", userVo);
        return "system/userEdit";
    }

    /**
     * 编辑用户
     * @param userVo
     * @return
     */
    @SysLog(value="编辑用户")
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(SysUserVo userVo) {
        List<SysUser> list = sysUserService.selectByLoginName(userVo);
        if (list != null && !list.isEmpty()) {
            return renderError("用户名已存在!");
        }
        if (StringUtils.isNotBlank(userVo.getLoginPwd())) {
            userVo.setLoginPwd(DigestUtils.md5Hex(userVo.getLoginPwd().toUpperCase()));
        }
        sysUserService.updateByVo(userVo);
        return renderSuccess("修改成功！");
    }

    /**
     * 修改密码页
     * @return
     */
    @GetMapping("/editPwdPage")
    public String editPwdPage() {
        return "system/userEditPwd";
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param pwd
     * @return
     */
    @RequestMapping("/editUserPwd")
    @ResponseBody
    public Object editUserPwd(String oldPwd, String pwd) {
        SysUser user = sysUserService.selectById(getUserId());
        if (!user.getLoginPwd().equals(DigestUtils.md5Hex(oldPwd).toUpperCase().toUpperCase())) {
            return renderError("旧密码不正确!");
        }
        sysUserService.updatePwdByUserId(getUserId(), DigestUtils.md5Hex(pwd).toUpperCase());
        return renderSuccess("密码修改成功！");
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @SysLog(value="删除用户")
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String id) {
        sysUserService.deleteUserById(id);
        return renderSuccess("删除成功！");
    }
}
