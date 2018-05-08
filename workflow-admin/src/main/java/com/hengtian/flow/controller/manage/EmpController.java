package com.hengtian.flow.controller.manage;

import com.google.common.collect.Maps;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.rbac.entity.RbacRole;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import com.rbac.service.UserService;
import com.user.entity.org.Org;
import com.user.service.org.OrgService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 员工系统（通过dubbo服务从emp系统获取数据）
 *
 * @author houjinrong@chtwm.com
 * date 2018/5/7 14:07
 */
@Controller
@RequestMapping("/emp")
public class EmpController {

    Logger logger = Logger.getLogger(getClass());

    @Autowired
    private OrgService orgService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserService userService;

    /**
     * 获取用户列表-分页
     *
     * @param code 员工编号
     * @param name 用户名
     * @param page 起始页
     * @param rows 每页条目数
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:10
     */
    @RequestMapping("/user")
    @ResponseBody
    public Object queryUser(String code, String name, Integer page, Integer rows){
        code = StringUtils.isBlank(code)?null:code.trim();
        name = StringUtils.isBlank(name)?null:name.trim();
        com.github.pagehelper.PageInfo<RbacUser> userPageInfo = userService.getUserPageInfo(page, rows, code, null, name, null);
        PageInfo pageInfo = new PageInfo();
        if(userPageInfo != null){
            pageInfo.setTotal((int)userPageInfo.getTotal());
            pageInfo.setRows(userPageInfo.getList());
            pageInfo.setNowpage(page);
            pageInfo.setPagesize(rows);
            pageInfo.setSize(rows);
            pageInfo.setFrom(userPageInfo.getStartRow());
            Map<String,Object> condition = Maps.newHashMap();
            condition.put("code",code);
            condition.put("name",name);
            pageInfo.setCondition(condition);
        }

        return pageInfo;
    }

    /**
     * 获取部门列表-分页
     * @param code 部门编号
     * @param orgName 部门名称
     * @param page 起始页
     * @param rows 每页条目数
     * @return 分页数据
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:10
     */
    @RequestMapping("/department")
    @ResponseBody
    public Object queryDepartment(String code, String orgName, Integer page, Integer rows){
        com.github.pagehelper.PageInfo<Org> pageInfo = orgService.getOrgPageInfo(page, rows, code, orgName);
        return pageInfo;
    }

    /**
     * 获取角色列表
     *
     * @param system 系统编号
     * @return 分页数据
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:10
     */
    @RequestMapping("/role")
    @ResponseBody
    public Object queryRole(Integer system){
        List<RbacRole> allRoleBySystem = privilegeService.getAllRoleBySystem(system);
        return allRoleBySystem;
    }
}
