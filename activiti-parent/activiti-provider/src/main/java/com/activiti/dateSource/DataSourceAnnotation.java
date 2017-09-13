package com.activiti.dateSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 数据库注解类型
 * 
 * @author zhouxy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSourceAnnotation {
	// 默认多金数据源
	String name() default DataSourceAnnotation.dataSourceDJ;

	// 多金对应数据源
	public static String dataSourceDJ1 = "dataSourceDJ1";
	// 多金对应数据源
	public static String dataSourceDJ = "dataSourceDJ";
	// 理顾宝对应数据源
	public static String dataSourceRG = "dataSourceRG";
	//老带新数据源
	public static String dataSourceCF = "dataSourceCF";

}
