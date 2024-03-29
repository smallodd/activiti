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
package com.hengtian.common.generator.config.rules;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hengtian.common.generator.config.ConstVal;

/**
 * 从数据库表到文件的命名策略
 *
 * @author YangHu, tangguo
 * @since 2016/8/30
 */
public enum NamingStrategy {
	/**
	 * 不做任何改变，原样输出
	 */
	nochange,
	/**
	 * 下划线转驼峰命名
	 */
	underline_to_camel,
	/**
	 * 仅去掉前缀
	 */
	remove_prefix,
	/**
	 * 去掉前缀并且转驼峰
	 */
	remove_prefix_and_camel;

	public static String underlineToCamel(String name) {
		// 快速检查
		if (StringUtils.isEmpty(name)) {
			// 没必要转换
			return "";
		}
		StringBuilder result = new StringBuilder();
		// 用下划线将原始字符串分割
		String camels[] = name.toLowerCase().split(ConstVal.UNDERLINE);
		for (String camel : camels) {
			// 跳过原始字符串中开头、结尾的下换线或双重下划线
			if (StringUtils.isEmpty(camel)) {
				continue;
			}
			// 处理真正的驼峰片段
			if (result.length() == 0) {
				// 第一个驼峰片段，全部字母都小写
				result.append(camel);
			} else {
				// 其他的驼峰片段，首字母大写
				result.append(capitalFirst(camel));
			}
		}
		return result.toString();
	}

	/**
	 * 去掉下划线前缀
	 *
	 * @param name
	 * @return
	 */
	public static String removePrefix(String name) {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		int idx = name.indexOf(ConstVal.UNDERLINE);
		if (idx == -1) {
			return name;
		}
		return name.substring(idx + 1);
	}

	/**
	 * 去掉指定的前缀
	 * 
	 * @param name
	 * @param prefix
	 * @return
	 */
	public static String removePrefix(String name, String prefix) {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		int idx = name.indexOf(ConstVal.UNDERLINE);
		if (prefix != null && !"".equals(prefix.trim())) {
			if (name.toLowerCase().matches("^" + prefix.toLowerCase() + ".*")) { // 判断是否有匹配的前缀，然后截取前缀
				idx = prefix.length() - 1;
			}
		}
		if (idx == -1) {
			return name;
		}
		return name.substring(idx + 1);
	}

	/**
	 * 去掉下划线前缀且将后半部分转成驼峰格式
	 *
	 * @param name
	 * @param tablePrefix
	 * @return
	 */
	public static String removePrefixAndCamel(String name, String tablePrefix) {
		return underlineToCamel(removePrefix(name, tablePrefix));
	}

	/**
	 * 实体首字母大写
	 *
	 * @param name
	 *            待转换的字符串
	 * @return 转换后的字符串
	 */
	public static String capitalFirst(String name) {
		if (StringUtils.isNotEmpty(name)) {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
			/*char[] array = name.toCharArray();
			array[0] -= 32;
			return String.valueOf(array);*/
		}
		return "";
	}

}
