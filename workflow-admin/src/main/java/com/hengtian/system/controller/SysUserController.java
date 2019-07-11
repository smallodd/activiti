package com.hengtian.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.utils.ConfigUtil;
import com.hengtian.common.utils.DigestUtils;
import com.hengtian.common.utils.EmailUtil;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.system.model.SysRole;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysUserService;
import com.hengtian.system.vo.SysUserVo;
import com.richgo.redis.RedisClusterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            condition.put("userName", "%"+userVo.getUserName().trim()+"%");
        }

        if(StringUtils.isNotBlank(userVo.getDepartmentId())){
            condition.put("departmentId", userVo.getDepartmentId().trim());
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
        userVo.setLoginPwd(DigestUtils.md5Hex(userVo.getLoginPwd()).toUpperCase());
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
            userVo.setLoginPwd(DigestUtils.md5Hex(userVo.getLoginPwd()).toUpperCase());
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
        if (!user.getLoginPwd().equals(DigestUtils.md5Hex(oldPwd).toUpperCase())) {
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

    /**
     * 密码管理页
     * @return
     */
    @RequestMapping("/password")
    public String passwordManager(){
        return "system/password/passwordUpdate";
    }

    /**
     * 密码管理页
     * @return
     */
    @RequestMapping("/updatePassword")
    @ResponseBody
    public Object updatePassword(String oldPassword,String newPassword){
        if(StringUtils.isBlank(oldPassword)){
            return renderError("旧密码不能为空");
        }else if(StringUtils.isBlank(newPassword)){
            return renderError("新密码不能为空");
        }
        SysUser sysUser = sysUserService.selectById(getUserId());

        if(sysUser != null && sysUser.getId() != null){
            if(DigestUtils.md5Hex(oldPassword).toUpperCase().equals(sysUser.getLoginPwd())){
                sysUserService.updatePwdByUserId(sysUser.getId(),DigestUtils.md5Hex(newPassword).toUpperCase());
            }else{
                return renderError("旧密码不正确");
            }
        }else{
            return renderError("用户不存在或未登录，不可进行修改密码操作");
        }
        return renderSuccess("密码修改成功");
    }

    /**
     * 忘记密码
     * @return
     */
    @RequestMapping("/passwordForget")
    public String passwordForget(){
        return "system/password/passwordForget";
    }

    /**
     * 重置密码邮箱验证
     * @return
     */
    @RequestMapping("/password/mailValidate")
    @ResponseBody
    public Object validateMail(HttpServletRequest request,String loginName, String code){
        if(StringUtils.isBlank(loginName) || StringUtils.isBlank(code)){
            return renderError("登录名称或验证码为空");
        }
        String redisCode = RedisClusterUtil.get(loginName);
        if(StringUtils.isBlank(redisCode)){
            return renderError("图形验证码不存在或已过期，请重试");
        }else if(!redisCode.equals(code.toLowerCase())){
            return renderError("图形验证码不正确");
        }
        EntityWrapper<SysUser> wrapper = new EntityWrapper<SysUser>();
        wrapper.where("login_name={0}",loginName);
        SysUser user = sysUserService.selectOne(wrapper);
        if(user == null || user.getId() == null){
            return renderError("用户【"+loginName+"】不存在");
        }

        try {
            String update_password_url = getRequestBaseUrl(request) + "/sysUser/passwordReset?loginName="+loginName+"&code="+DigestUtils.md5Hex(redisCode);
            String subject = "工作流管理平台-修改密码";
            String msg = "<a href='"+ update_password_url +"'>点击进入修改密码页面（有效期10分钟）</a>";

            EmailUtil.getEmailUtil().sendEmail(ConfigUtil.getValue("email.send.account"), "工作流管理平台", user.getUserEmail(), subject, msg);
        } catch (Exception e) {
            e.printStackTrace();
            return renderError("邮箱验证失败，请联系管理员");
        }

        return renderSuccess("邮件已发送，请进入邮箱修改");
    }

    /**
     * 重置密码页
     * @return
     */
    @RequestMapping("/passwordReset")
    public String passwordReset(HttpServletRequest request,String loginName,String code){
        int flag = 1;
        if(StringUtils.isBlank(loginName) || StringUtils.isBlank(code)){//请求不合法
            flag = -1;
        }

        String redisCode = RedisClusterUtil.get(loginName);
        if(StringUtils.isBlank(redisCode) || !DigestUtils.md5Hex(redisCode).equals(code)){//链接已过期
            flag = 0;
        }
        request.setAttribute("loginName",loginName);
        request.setAttribute("flag",flag);

        return "system/password/passwordReset";
    }

    /**
     * 重置密码
     * @return
     */
    @RequestMapping("/resetPassword")
    @ResponseBody
    public Object resetPassword(String loginName,String password){
        if(StringUtils.isBlank(loginName) || StringUtils.isBlank(password)){
            return renderError("密码为空");
        }
        EntityWrapper<SysUser> wrapper = new EntityWrapper<SysUser>();
        wrapper.where("login_name={0}",loginName);
        SysUser user = sysUserService.selectOne(wrapper);

        if(user != null && user.getId() != null){
            sysUserService.updatePwdByUserId(user.getId(),DigestUtils.md5Hex(password).toUpperCase());
        }else{
            return renderError("用户不存在或未登录，不可进行修改密码操作");
        }
        RedisClusterUtil.del(loginName);
        return renderSuccess("密码修改成功");
    }

    private String getRequestBaseUrl(HttpServletRequest request){
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
