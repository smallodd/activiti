package com.hengtian.common.interceptor;


import com.common.common.CodeConts;
import com.common.common.ResultJson;
import com.common.interceptor.comment.SensitiveWords;
import com.common.interceptor.comment.SensitivewordManage;
import com.common.util.ConfigUtil;
import com.rbac.dubbo.RbacDomainContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 功能描述:拦截器
 * @Author: hour
 * @Date: 2019/6/12 16:09
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

	private static final String contentType = "application/json;charset=utf-8";
	/**
	 * 敏感词返回信息
	 */
	private static String existsSensitiveWord = null;

	/**
	 * swagger相关请求都不拦截
	 */
	private String[] swaggerUrls;

	/* 敏感词管理对象 */
	private static SensitivewordManage sensitivewordManage = null;

	public AuthInterceptor() {
		sensitivewordManage = new SensitivewordManage();
	}

	/**
	 * 在业务处理器处理请求之前被调用，在该方法中对用户请求request进行处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		RbacDomainContext.getContext().setDomain("chtwm");
		String contextPath = request.getContextPath();
		String requestUrl = request.getRequestURI().replace(contextPath, "");

		if (log.isDebugEnabled()) {
			log.debug("preHandle:{}", requestUrl);
		}
		// 敏感词判断
		boolean ret = existsSensitiveWord(request, response, handler);
		if (ret) {
			// 存在敏感词
			return false;
		}

		// swagger请求不拦截
		if (null != swaggerUrls && swaggerUrls.length >= 1) {
			for (int i = 0; i < swaggerUrls.length; i++) {
				String swaggerUrl = swaggerUrls[i];
				if (requestUrl.startsWith(swaggerUrl)) {
					return true;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * 登录失败
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	private void returnLoginFail(HttpServletResponse response) throws IOException {
		response.getWriter().write(ResultJson.getResultFail(CodeConts.LOGIN_FAILURE));
	}

	/**
	 * 在DispatcherServlet完全处理完请求后被调用，可以在该方法中进行一些资源清理的操作。
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	/**
	 * 在业务处理器处理完请求后，但是DispatcherServlet向客户端返回请求前被调用，在该方法中对用户请求request进行处理。
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * 防盗链处理
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return boolean true:来源属于本网站，false：来源不属于本网站
	 * @throws IOException
	 */
	private boolean headerPro(HttpServletRequest request) throws IOException {
		return ConfigUtil.checkReferer(request);
	}

	/**
	 *
	 * 敏感词判断
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            Object
	 * @return boolean true：存在敏感词；false:不需要敏感词判断或判断不存在敏感词
	 * @throws IOException
	 *
	 */
	private boolean existsSensitiveWord(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			// 获取Controller方法
			Method method = handlerMethod.getMethod();
			// 获取违禁词注解
			SensitiveWords annotation = method.getAnnotation(SensitiveWords.class);

			if (annotation != null) {
				if (annotation.check()) {
					// 需要检查敏感词
					if (log.isDebugEnabled()) {
						log.debug("需要检查敏感词是否存在，方法名称={}", method);
					}

					// 获取参数值
					String value = String.valueOf(request.getAttribute(annotation.requestParamName()));
					// 检查是否存在敏感词
					boolean ret = sensitivewordManage.isContaintSensitiveWord(value, SensitivewordManage.minMatchTYpe);

					if (ret) {
						response.setContentType(contentType);
						if (existsSensitiveWord == null) {
							existsSensitiveWord = ResultJson.getResultFail(CodeConts.EXISTS_SENSITIVE_WORD);
						}
						response.getWriter().write(existsSensitiveWord);
						return true;
					}
				}
			}
		}
		return false;
	}
}
