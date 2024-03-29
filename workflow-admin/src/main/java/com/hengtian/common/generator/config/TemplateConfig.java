/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hengtian.common.generator.config;

/**
 * <p>
 * 模板路径配置项
 * </p>
 */
public class TemplateConfig {

	private String entity = ConstVal.TEMPLATE_ENTITY;

	private String service = ConstVal.TEMPLATE_SERVICE;

	private String serviceImpl = ConstVal.TEMPLATE_SERVICEIMPL;

	private String mapper = ConstVal.TEMPLATE_MAPPER;

	private String xml = ConstVal.TEMPLATE_XML;

	private String controller = ConstVal.TEMPLATE_CONTROLLER;
	
	//新增jsp生成
	private String list = ConstVal.TEMPLATE_LIST;
	private String add = ConstVal.TEMPLATE_ADD;
	private String edit = ConstVal.TEMPLATE_EDIT;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceImpl(String serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	public String getMapper() {
		return mapper;
	}

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	
	
}
