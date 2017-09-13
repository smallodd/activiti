package com.activiti.service.impl;

import com.activiti.service.UserRoleService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ma on 2017/7/19.
 */
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
    IdentityService identityService;
    @Override
    public String findUser(String id,String groupId) {

        return identityService.getUserInfo(id,groupId);
    }

    @Override
    public void insertMembership(User user,Group group) {
        List<Group> groups=identityService.createGroupQuery().groupId(group.getId()).list();
        List<User> users=identityService.createUserQuery().userId(user.getId()).list();
        if(groups==null||groups.size()==0)
        identityService.saveGroup(group);
        if(users==null||users.size()==0)
        identityService.saveUser(user);
        identityService.createMembership(user.getId(),group.getId());

    }

    @Override
    public void deleteUser(String id,String groupId) {
        identityService.deleteMembership(id,groupId);
    }
}
