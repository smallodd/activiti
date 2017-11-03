package com.hengtian.common.generator;

import com.hengtian.common.generator.config.DataSourceConfig;
import com.hengtian.common.generator.config.GlobalConfig;
import com.hengtian.common.generator.config.PackageConfig;
import com.hengtian.common.generator.config.StrategyConfig;
import com.hengtian.common.generator.config.TemplateConfig;
import com.hengtian.common.generator.config.builder.ConfigBuilder;

/**
 * <p>
 * 插件基类，用于属性配置 设计成抽象类主要是用于后期可扩展，共享参数配置。
 * </p>
 */
public abstract class AbstractGenerator {

	/**
	 * 数据源配置
	 */
	private DataSourceConfig dataSource;

	/**
	 * 数据库表配置
	 */
	private StrategyConfig strategy;

	/**
	 * 包 相关配置
	 */
	private PackageConfig packageInfo;

	/**
	 * 模板 相关配置
	 */
	private TemplateConfig template;

	/**
	 * 全局 相关配置
	 */
	private GlobalConfig globalConfig;

	protected ConfigBuilder config;

	protected InjectionConfig cfg;

	/**
	 * 初始化配置
	 */
	protected void initConfig() {
		if (null == config) {
			config = new ConfigBuilder(packageInfo, dataSource, strategy, template, globalConfig);
			if (null != cfg) {
				cfg.setConfig(config);
			}
		}
	}

	public DataSourceConfig getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceConfig dataSource) {
		this.dataSource = dataSource;
	}

	public StrategyConfig getStrategy() {
		return strategy;
	}

	public void setStrategy(StrategyConfig strategy) {
		this.strategy = strategy;
	}

	public PackageConfig getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(PackageConfig packageInfo) {
		this.packageInfo = packageInfo;
	}

	public TemplateConfig getTemplate() {
		return template;
	}

	public void setTemplate(TemplateConfig template) {
		this.template = template;
	}

	public ConfigBuilder getConfig() {
		return config;
	}

	public void setConfig(ConfigBuilder config) {
		this.config = config;
	}

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	public InjectionConfig getCfg() {
		return cfg;
	}

	public void setCfg(InjectionConfig cfg) {
		this.cfg = cfg;
	}

}
