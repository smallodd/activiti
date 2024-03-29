package com.hengtian.common.base;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 
 * 防止xss攻击 Filter
 * 
 * @author zhouxy
 *
 */
@Order(1)
@WebFilter(filterName = "xssSqlFilter", urlPatterns = "/*")
public class XssFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		filterChain.doFilter(new XssHttpServletRequestWrapper(request), response);
	}
}