package com.hengtian.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.result.Tree;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.system.dao.SysResourceDao;
import com.hengtian.system.dao.SysRoleDao;
import com.hengtian.system.dao.SysUserRoleDao;
import com.hengtian.system.model.SysResource;
import com.hengtian.system.service.SysResourceService;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
@Service
public class SysResourceServiceImpl extends ServiceImpl<SysResourceDao, SysResource> implements SysResourceService {

	@Autowired
	private SysRoleDao sysRoleDao;
	@Autowired
	private SysResourceDao sysResourceDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	
	@Override
	public List<SysResource> selectAll() {
		EntityWrapper<SysResource> wrapper = new EntityWrapper<SysResource>();
		wrapper.orderBy("sequence");
        return sysResourceDao.selectList(wrapper);
	}

	public List<SysResource> selectByType(Integer type) {
		EntityWrapper<SysResource> wrapper = new EntityWrapper<SysResource>();
	 	SysResource resource = new SysResource();
        wrapper.setEntity(resource);
        wrapper.addFilter("resource_type = {0}", type);
        return sysResourceDao.selectList(wrapper);
    }
	
	@Override
	public List<Tree> selectAllMenu() {
        List<Tree> trees = new ArrayList<Tree>();
        // 查询所有菜单
        List<SysResource> resources = this.selectByType(ConstantUtils.RESOURCE_MENU);
        if (resources == null) {
            return trees;
        }
        for (SysResource resource : resources) {
            Tree tree = new Tree();
            tree.setId(resource.getId());
            tree.setPid(resource.getParentId());
            tree.setText(resource.getResourceName());
            tree.setIconCls(resource.getResourceIcon());
            tree.setAttributes(resource.getResourceUrl());
            trees.add(tree);
        }
        return trees;
	}

	@Override
	public List<Tree> selectAllTree() {
        // 获取所有的资源 tree形式，展示
        List<Tree> trees = new ArrayList<Tree>();
        List<SysResource> resources = this.selectAll();
        if (resources == null) {
            return trees;
        }
        for (SysResource resource : resources) {
            Tree tree = new Tree();
            tree.setId(resource.getId());
            tree.setPid(resource.getParentId());
            tree.setText(resource.getResourceName());
            tree.setIconCls(resource.getResourceIcon());
            tree.setAttributes(resource.getResourceUrl());
            trees.add(tree);
        }
        return trees;
    }

	@Override
	public List<Tree> selectTree(ShiroUser shiroUser) {
        List<Tree> trees = new ArrayList<Tree>();
        Set<String> roles = shiroUser.getRoles();
        if (roles == null) {
            return trees;
        }
        // 如果有超级管理员权限
        if (roles.contains("admin")) {
            List<SysResource> resourceList = this.selectByType(ConstantUtils.RESOURCE_MENU);
            if (resourceList == null) {
                return trees;
            }
            for (SysResource resource : resourceList) {
                Tree tree = new Tree();
                tree.setId(resource.getId());
                tree.setPid(resource.getParentId());
                tree.setText(resource.getResourceName());
                tree.setIconCls(resource.getResourceIcon());
                tree.setAttributes(resource.getResourceUrl());
                trees.add(tree);
            }
            return trees;
        }
        // 普通用户
        List<String> roleIdList = sysUserRoleDao.selectRoleIdListByUserId(shiroUser.getId());
        if (roleIdList == null) {
            return trees;
        }
        List<SysResource> resourceLists = sysRoleDao.selectResourceListByRoleIdList(roleIdList);
        if (resourceLists == null) {
            return trees;
        }
        for (SysResource resource : resourceLists) {
            Tree tree = new Tree();
            tree.setId(resource.getId());
            tree.setPid(resource.getParentId());
            tree.setText(resource.getResourceName());
            tree.setIconCls(resource.getResourceIcon());
            tree.setAttributes(resource.getResourceUrl());
            trees.add(tree);
        }
        return trees;
    }
	
}
