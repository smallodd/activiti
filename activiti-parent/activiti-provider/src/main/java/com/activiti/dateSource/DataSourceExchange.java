package com.activiti.dateSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 切换数据源，执行数据库操作
 * 
 * @author zhouxy
 *
 */
public class DataSourceExchange implements MethodInterceptor {
	private static final Logger log = LoggerFactory.getLogger(DataSourceExchange.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 获取当前执行方法DataSourceAnnotation注解
		DataSourceAnnotation dataSource = invocation.getMethod().getAnnotation(DataSourceAnnotation.class);
		// 设置数据源名称，切换数据源
		DataSourceHolder.setDataSource(dataSource.name());

		if (log.isDebugEnabled()) {
			log.debug("当前使用数据源名称：" + dataSource.name());
		}
		try {
			return invocation.proceed();
		} catch (Exception ex) {
			log.error("切换完数据源，执行数据库操作异常", ex);
			return ex;
		}
	}

}
