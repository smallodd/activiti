package com.hengtian.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hengtian.system.model.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
  * 用户角色关联表 Mapper 接口
 * </p>
 * @author junyang.liu
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

	List<SysUserRole> selectByUserId(@Param("userId") String userId);

    List<String> selectRoleIdListByUserId(@Param("userId") String userId);
}