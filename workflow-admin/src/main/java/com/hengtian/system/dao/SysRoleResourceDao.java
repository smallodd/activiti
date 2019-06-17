package com.hengtian.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hengtian.system.model.SysRoleResource;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
  * 角色菜单表 Mapper 接口
 * </p>
 * @author junyang.liu
 */
public interface SysRoleResourceDao extends BaseMapper<SysRoleResource> {
	
	String selectIdListByRoleId(@Param("id") String id);
}