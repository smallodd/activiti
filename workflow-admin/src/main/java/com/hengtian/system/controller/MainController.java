package com.hengtian.system.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.utils.CaptchaUtil;
import com.hengtian.common.utils.DigestUtils;

import com.hengtian.common.utils.StringUtils;
import com.richgo.redis.RedisClusterUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Main
 * @author liu.junyang
 */
@Controller
public class MainController extends BaseController {
    /**
     * 首页
     * @return
     */
    @GetMapping("/")
    public String index() {
    	if (!SecurityUtils.getSubject().isAuthenticated()) {
            return "login";
        }
        return "index";
    }

    /**
     * GET 登录
     * @return {String}
     */
    @GetMapping("/login")
    public String login() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return "index";
        }
        return "login";
    }

    /**
     * POST 登录 shiro 写法
     * @param loginName 用户名
     * @param loginPwd 密码
     * @return {Object}
     */
    @SysLog(value="用户登录")
    @PostMapping(value="/login")
    @ResponseBody
    public Object loginPost(HttpServletRequest request, String loginName, String loginPwd) {
        if (StringUtils.isBlank(loginName)) {
        	return renderError("用户名不能为空");
        }
        if (StringUtils.isBlank(loginPwd)) {
        	return renderError("密码不能为空");
        }
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return renderError("浏览器已登录请退出后再登陆！");
        }
        Subject user = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, DigestUtils.md5Hex(loginPwd).toUpperCase());
        try {
            user.login(token);
            return renderSuccess();
        } catch (UnknownAccountException e) {
        	return renderError("账号不存在！");
        } catch (DisabledAccountException e) {
        	return renderError("账号未启用！");
        } catch (IncorrectCredentialsException e) {
        	return renderError("密码错误！");
        } catch (Throwable e) {
        	return renderError("未知异常！");
        }
    }

    /**
     * 未授权
     * @return {String}
     */
    @GetMapping("/unauth")
    public String unauth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return "login";
        }
        return "error/unauth";
    }
    
    /**
     * 404
     * @return {String}
     */
    @GetMapping("/404")
    public String error404() {
        return "error/404";
    }
    
    /**
     * 500
     * @return {String}
     */
    @GetMapping("/500")
    public String error500() {
        return "error/500";
    }

    /**
     * 退出
     * @return {Result}
     */
    @PostMapping("/logout")
    @ResponseBody
    public Object logout() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return "login";
        }
        SecurityUtils.getSubject().logout();
        return renderSuccess();
    }

    /**
     * 系统图标
     */
    @GetMapping("/icons")
    public String icons() {
       return "icons";
    }

    /**
     * 生成图形验证码
     */
    @RequestMapping("/createCaptcha")
    public void createCaptcha(HttpServletResponse response,String loginName){
        try {
            //生成随机字串
            String verifyCode = CaptchaUtil.generateVerifyCode(4);
            //存入redis
            RedisClusterUtil.set(loginName, verifyCode.toLowerCase(), 10*60);//有效期十分钟
            String ss = RedisClusterUtil.get(loginName);
            //生成图片
            int w = 100, h = 40;
            CaptchaUtil.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
