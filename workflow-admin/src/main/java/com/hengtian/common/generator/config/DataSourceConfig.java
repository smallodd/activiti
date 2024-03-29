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

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.hengtian.common.generator.config.rules.DbType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * <p>
 * 数据库配置
 * </p>
 */
public class DataSourceConfig {

	/**
	 * 数据库类型
	 */
	private DbType dbType;
	/**
	 * 驱动连接的URL
	 */
	private String url;
	/**
	 * 驱动名称
	 */
	private String driverName;
	/**
	 * 数据库连接用户名
	 */
	private String username;
	/**
	 * 数据库连接密码
	 */
	private String password;

	/**
	 * 判断数据库类型
	 *
	 * @return 类型枚举值
	 */
	public DbType getDbType() {
		if (null == dbType) {
			if (driverName.contains("mysql")) {
				dbType = DbType.MYSQL;
			} else if (driverName.contains("oracle")) {
				dbType = DbType.ORACLE;
			} else {
				throw new MybatisPlusException("Unknown type of database!");
			}
		}
		return dbType;
	}

	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}

	/**
	 * 创建数据库连接对象
	 *
	 * @return Connection
	 */
	public Connection getConn() {
		Connection conn = null;
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
