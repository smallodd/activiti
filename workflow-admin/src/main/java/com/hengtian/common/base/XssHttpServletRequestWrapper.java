package com.hengtian.common.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * 防止xss攻击过滤类
 * 
 * @author zhouxy
 *
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}
    @Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}
    @Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}
    @Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}

	private String cleanXSS(String value) {
		//value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		//value = value.replaceAll("'", "&#39;");
		//value = value.replaceAll("\"", "&quot;");
//		value = value.replaceAll("eval\\((.*)\\)", "");
//		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");

		 //value = value.replaceAll("<", "").replaceAll(">", "");
//		 value = value.replaceAll("\\(", "").replaceAll("\\)", "");
		 value = value.replaceAll("script", "");
		 value = value.replaceAll("document.cookie", "");
//		 value = value.replaceAll("eval\\((.*)\\)", "");
//		 value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
//		 "\"\"");
		return value;
	}
}