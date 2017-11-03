package com.hengtian.common.operlog;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;

import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.IPAddressUtil;
import com.hengtian.system.model.SysOperLog;
import com.hengtian.system.service.SysOperLogService;

/**
 * aop：异常处理
 */
public class ExceptionHandler implements ThrowsAdvice {
    private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SysOperLogService logService;

    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        LOG.error("出现Exception:url为" + request.getRequestURI() + ";错误类型为"+e.getStackTrace()[0]+"");
        SysOperLog operLog = new SysOperLog();
        StringBuffer operEvent = new StringBuffer();
        String descr4Exception = "";   // 具体错误信息

        try {
            String targetName = joinPoint.getTarget().getClass().getName(); // 请求类名称
            String methodName = joinPoint.getSignature().getName(); // 请求方法
            Object[] arguments = joinPoint.getArgs();
            Class<?> targetClass = null;
            targetClass = Class.forName(targetName);

            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    @SuppressWarnings("rawtypes")
					Class[] clazzs = method.getParameterTypes();
                    if (clazzs.length == arguments.length) {
                        if(method.getAnnotation(SysLog.class) != null){
                            operEvent.append(method.getAnnotation(SysLog.class).value());
                            operEvent.append("。");
                            break;
                        }
                    }
                }
            }
            operEvent.append("该方法实际入参为：");
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                operEvent.append(joinPoint.getArgs()[i]);
                operEvent.append(",");
            }
            operEvent.deleteCharAt(operEvent.length()-1); //删除最后一个 ","
            operEvent.append("。Exception类型为：");
            operEvent.append(e.getClass());
            descr4Exception = createExceptionDetail(e);

            Subject curUser = SecurityUtils.getSubject();
            if (request.getRequestURI().contains("/logout")
                    && "logout".equalsIgnoreCase(joinPoint.getSignature().getName())) {
                // 退出日志
                String userId = (String) arguments[0];
                operLog.setOperUserId(userId);
            }
            if(curUser.getPrincipal()!=null){
                //从session中获取当前登录用户的User对象
            	ShiroUser loginUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
                operLog.setOperUserName(loginUser.getName());
                operLog.setOperUserId(loginUser.getId());
            }
            if("0:0:0:0:0:0:0:1".equals(IPAddressUtil.getIpAddress(request))){
				operLog.setOperClientIp("127.0.0.1");
			}else{
				operLog.setOperClientIp(IPAddressUtil.getIpAddress(request));
			}
        }catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            LOG.error("实例化失败：ClassNotFoundException");
        }catch (IOException e2) {
            e2.printStackTrace();
            operLog.setOperClientIp("未知IP：IOException");
        }

        operLog.setRequestUrl(request.getRequestURI());
        operLog.setRequestMethod(joinPoint.getSignature().getDeclaringTypeName()+" [ "+joinPoint.getSignature().getName()+" ] ");
        operLog.setOperEvent((operEvent.toString()).length()>255?(operEvent.toString()).substring(0,255):operEvent.toString());
        operLog.setOperStatus(ConstantUtils.operStatus.FAIL.getValue());
        operLog.setOperTime(new Date());
        operLog.setLogDescription("[操作异常]具体Exception信息为："+ descr4Exception);
        try{
            // 保存到数据库
            logService.insert(operLog);
        }catch (Exception ex){
            ex.printStackTrace();
            LOG.error("log保存数据库失败");
        }
    }

    /**
     * 异常数组转成字符串
     */
    private String createExceptionDetail(Exception e) {
        StackTraceElement[] stackTraceArray = e.getStackTrace();
        StringBuilder detail = new StringBuilder();
        for (int i = 0; i < stackTraceArray.length; i++) {
            //255位，此处是考虑数据库相应字段的大小限制
            if((detail.toString()+stackTraceArray[i]).length() > 255){
                return detail.toString();
            }
            detail.append(stackTraceArray[i] + "\r\n");
        }
        return detail.toString();
    }
}

