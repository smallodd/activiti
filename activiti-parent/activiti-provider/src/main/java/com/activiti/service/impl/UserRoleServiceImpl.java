package com.activiti.service.impl;


import com.activiti.service.UserRoleService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Resource;
import javax.management.relation.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/7/19.
 */
public class UserRoleServiceImpl implements UserRoleService {
    private static Logger logger= LoggerFactory.getLogger("UserRoleServiceImpl");
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


    @Override
    public void addGroup(Group group) {
        if(group.getId()==null){
            throw  new RuntimeException("分组id不能为空");
        }
        if(StringUtils.isBlank(group.getName())){
            throw  new RuntimeException("分组名称不能为空");
        }
        identityService.saveGroup(group);
    }
}
