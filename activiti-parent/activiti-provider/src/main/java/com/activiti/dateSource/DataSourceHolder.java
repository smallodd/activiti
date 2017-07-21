package com.activiti.dateSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 数据源操作
 * 
 * @author zhouxy
 *
 */
public class DataSourceHolder {
	private static final Logger log = LoggerFactory.getLogger(DataSourceHolder.class);

	/** 线程本地环境 */
	private static final ThreadLocal<String> dataSources = new ThreadLocal<String>();

	/**
	 * 
	 * 设置数据源
	 * 
	 * @param customerType
	 */
	public static void setDataSource(String customerType) {
		if(StringUtils.isBlank(customerType)){
			log.error("参数customerType为null或空");
		}
		dataSources.set(customerType);
	}

	/**
	 * 
	 * 获取数据源
	 * 
	 * @return
	 */
	public static String getDataSource() {
		return dataSources.get();
	}

	/**
	 * 
	 * 清除数据源
	 * 
	 */
	public static void clearDataSource() {
		dataSources.remove();
	}
}