package com.activiti.service;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

/**
 * 用于对工作流用户角色的操作
 * Created by ma on 2017/7/19.
 */
public interface UserRoleService {

    String findUser(String id, String groupId);

    void insertMembership(User user, Group group);

    void deleteUser(String id, String groupId);

}
