package com.hengtian.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hengtian.system.model.SysResource;
import com.hengtian.system.model.SysRole;


/**
 * <p>
  * 角色表 Mapper 接口
 * </p>
 * @author junyang.liu
 */
public interface SysRoleDao extends BaseMapper<SysRole> {

    List<String> selectResourceIdListByRoleId(@Param("id") String id);

    List<SysResource> selectResourceListByRoleIdList(@Param("list") List<String> list);

    List<Map<String, String>> selectResourceListByRoleId(@Param("id") String id);

    List<SysRole> selectRoleList(Pagination page, @Param("sort") String sort, @Param("order") String order);



}