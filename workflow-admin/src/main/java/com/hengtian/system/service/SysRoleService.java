package com.hengtian.system.service;


import java.util.List;
import java.util.Map;
import java.util.Set;
import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.system.model.SysRole;

/**
 * 角色表 服务类
 *
 * @author houjinrong@chtwm.com
 * @since 2017-08-09
 */
public interface SysRoleService extends IService<SysRole> {
	
	void selectDataGrid(PageInfo pageInfo);

    Object selectTree();

    List<String> selectResourceIdListByRoleId(String id);

    void updateRoleResource(String id, String resourceIds);

    Map<String, Set<String>> selectResourceMapByUserId(String userId);
	
}
