package com.hengtian.common.generator;

import java.util.Map;

import com.hengtian.common.generator.config.builder.ConfigBuilder;

/**
 * <p>
 * 抽象的对外接口
 * </p>
 */
@Deprecated
public abstract class InjectionConfig {

	/**
	 * 全局配置
	 */
	private ConfigBuilder config;

	/**
	 * 自定义返回配置 Map 对象
	 */
	private Map<String, Object> map;

	/**
	 * 注入自定义 Map 对象
	 */
	public abstract void initMap();

	public ConfigBuilder getConfig() {
		return config;
	}

	public void setConfig(ConfigBuilder config) {
		this.config = config;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

}
