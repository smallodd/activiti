package com.hengtian.system.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.result.Tree;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.system.dao.SysRoleDao;
import com.hengtian.system.dao.SysRoleResourceDao;
import com.hengtian.system.dao.SysUserRoleDao;
import com.hengtian.system.model.SysRole;
import com.hengtian.system.model.SysRoleResource;
import com.hengtian.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author houjinrong@chtwm.com
 * @since 2017-08-09
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRole> implements SysRoleService {

	@Autowired
	private SysRoleDao sysRoleDao;
	@Autowired
	private SysRoleResourceDao sysRoleResourceDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	
	public List<SysRole> selectAll() {
        EntityWrapper<SysRole> wrapper = new EntityWrapper<SysRole>();
        return sysRoleDao.selectList(wrapper);
    }
	
	@Override
	public void selectDataGrid(PageInfo pageInfo) {
		Page<SysRole> page = new Page<SysRole>(pageInfo.getNowpage(), pageInfo.getSize());
        List<SysRole> list = sysRoleDao.selectRoleList(page, pageInfo.getSort(), pageInfo.getOrder());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
	}

	@Override
	public Object selectTree() {
		List<Tree> trees = new ArrayList<Tree>();
        List<SysRole> roles = this.selectAll();
        for (SysRole role : roles) {
            Tree tree = new Tree();
            tree.setId(role.getId());
            tree.setText(role.getRoleName());
            trees.add(tree);
        }
        return trees;
	}

	@Override
	public List<String> selectResourceIdListByRoleId(String id) {
		return sysRoleDao.selectResourceIdListByRoleId(id);
	}

	@Override
	public void updateRoleResource(String id, String resourceIds) {
		SysRoleResource roleResource = new SysRoleResource();
        roleResource.setRoleId(id);
        sysRoleResourceDao.delete(new EntityWrapper<SysRoleResource>(roleResource));
        
        String[] resourceIdArray = resourceIds.split(",");
        for (String resourceId : resourceIdArray) {
            roleResource = new SysRoleResource();
            roleResource.setRoleId(id);
            roleResource.setResourceId(resourceId);
            sysRoleResourceDao.insert(roleResource);
        }
	}

	@Override
	public Map<String, Set<String>> selectResourceMapByUserId(String userId) {
        Map<String, Set<String>> resourceMap = new HashMap<String, Set<String>>();
        List<String> roleIdList = sysUserRoleDao.selectRoleIdListByUserId(userId);
        Set<String> urlSet = new HashSet<String>();
        Set<String> roles = new HashSet<String>();
        for (String roleId : roleIdList) {
            List<Map<String, String>> resourceList = sysRoleDao.selectResourceListByRoleId(roleId);
            if (resourceList != null) {
                for (Map<String, String> map : resourceList) {
                	if(map!=null){
                		if (StringUtils.isNotBlank(map.get("url"))) {
                            urlSet.add(map.get("url"));
                        }
                	}
                }
            }
            SysRole role = sysRoleDao.selectById(roleId);
            if (role != null) {
                roles.add(role.getRoleName());
            }
        }
        resourceMap.put("urls", urlSet);
        resourceMap.put("roles", roles);
        return resourceMap;
    }
	
}
