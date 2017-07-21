package com.activiti.dateSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 获取数据源（依赖于spring）
 * 
 * @author zhouxy
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	/**
	 * Determine the current lookup key. This will typically be implemented to
	 * check a thread-bound transaction context.
	 * <p>
	 * Allows for arbitrary keys. The returned key needs to match the stored
	 * lookup key type, as resolved by the {@link #resolveSpecifiedLookupKey}
	 * method.
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceHolder.getDataSource();
	}

}