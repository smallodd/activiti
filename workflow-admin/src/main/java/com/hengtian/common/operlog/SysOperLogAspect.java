package com.hengtian.common.operlog;

import java.lang.reflect.Method;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.IPAddressUtil;
import com.hengtian.system.model.SysOperLog;
import com.hengtian.system.service.SysOperLogService;


/**
 * 日志切面
 */
@Aspect
@Component
public class SysOperLogAspect {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private SysOperLogService logService;

	private static final Logger logger = Logger.getLogger(SysOperLogAspect.class);
	//Controller层切点
	//@annotation用于匹配当前执行方法持有指定注解的方法；
	@Pointcut("@annotation(com.hengtian.common.operlog.SysLog)")
	public void sysOperLogAspect() {
	}

	/**
	 * 后置通知 用于拦截Controller层记录用户的操作
	 *
	 * @param joinPoint
	 *            切点
     * @param rvt
     *            指定一个 returning 属性，该属性值为 rvt , 表示 允许在 增强处理方法中使用名为rvt的形参，该形参代表目标方法的返回值。
	 */
	@AfterReturning(returning = "rvt", pointcut = "sysOperLogAspect()")
	public void after(JoinPoint joinPoint, Object rvt) {
		try {
			ShiroUser loginUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			if(loginUser==null&&request.getRequestURI().contains("/rest/flow")){
				logger.error("系统没有认证信息!");
				return;
			}
			String targetName = joinPoint.getTarget().getClass().getName(); // 请求类名称
			String methodName = joinPoint.getSignature().getName(); // 请求方法
			Object[] arguments = joinPoint.getArgs();
			Class<?> targetClass = Class.forName(targetName);
			Method[] methods = targetClass.getMethods();
			String value = "";
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					@SuppressWarnings("rawtypes")
					Class[] clazzs = method.getParameterTypes();
					if (clazzs.length == arguments.length) {
						if(method.getAnnotation(SysLog.class) != null){ // 如果包含注解@log()
							value = method.getAnnotation(SysLog.class).value();
							break;
						}
					}
				}
			}
			SysOperLog operLog = new SysOperLog();
			if (request.getRequestURI().contains("/login") && "loginPost".equalsIgnoreCase(joinPoint.getSignature().getName())) {
				operLog.setOperUserId(loginUser.getId());
				operLog.setOperUserName(loginUser.getName());
				operLog.setOperStatus(ConstantUtils.operStatus.SUCCESS.getValue());
			}else if (request.getRequestURI().contains("/logout") && "logout".equalsIgnoreCase(joinPoint.getSignature().getName())) {
				operLog.setOperUserId(loginUser.getId());
				operLog.setOperUserName(loginUser.getName());
				operLog.setOperStatus(ConstantUtils.operStatus.SUCCESS.getValue());
			} else {
				operLog.setOperUserName(loginUser.getName());
				operLog.setOperUserId(loginUser.getId());
				operLog.setOperStatus(ConstantUtils.operStatus.SUCCESS.getValue());
			}
			
			if("0:0:0:0:0:0:0:1".equals(IPAddressUtil.getIpAddress(request))){
				operLog.setOperClientIp("127.0.0.1");
			}else{
				operLog.setOperClientIp(IPAddressUtil.getIpAddress(request));
			}
            operLog.setRequestUrl(request.getRequestURI());
            joinPoint.getSignature();
            operLog.setRequestMethod(joinPoint.getSignature().getDeclaringTypeName()+" [ "+joinPoint.getSignature().getName()+" ] ");
            operLog.setOperEvent(value);
            operLog.setOperTime(new Date());
            operLog.setLogDescription("[操作成功]");
			logService.insert(operLog);
		} catch (Exception e) {
			logger.error("后置通知异常:异常信息:"+e.getMessage());
			e.printStackTrace();
		}
	}
}