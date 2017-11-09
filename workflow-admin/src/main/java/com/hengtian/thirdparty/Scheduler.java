package com.hengtian.thirdparty;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hengtian.common.utils.ConfigUtil;
import com.hengtian.common.utils.DigestUtils;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.system.model.SysDepartment;
import com.hengtian.system.model.SysRole;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysDepartmentService;
import com.hengtian.system.service.SysRoleService;
import com.hengtian.system.service.SysUserService;
import com.hengtian.system.vo.SysUserVo;
import com.hengtian.thirdparty.service.SqlService;
import org.activiti.engine.IdentityService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/10/31.
 */
public class Scheduler {
    private final Logger logger=Logger.getLogger(Scheduler.class);

    SqlService sqlService=new SqlService();

    @Autowired
    SysUserService userService;
    @Autowired
    SysRoleService roleService;
    @Autowired
    SysDepartmentService sysDepartmentService;

    public void executeUser(){
            if(ConfigUtil.getValue("syn").equals("false")){
                logger.info("不执行数据同步");
                return;
            }

            List<Map<String, Object>> list = sqlService.execQuery("select * from emp", null);

            if(list==null||list.size()==0){
               logger.info("没有数据");
            }else{
                List<SysUserVo> userVoList =new ArrayList<SysUserVo>();
                for(Map<String,Object> map:list) {

                    SysUserVo userVo=new SysUserVo();
                    String code = (String) map.get("code");
                    String password = (String) map.get("password");
                    String name = (String) map.get("name");
                    String email = (String) map.get("email");
                    if(StringUtils.isBlank(password)){
                        password= DigestUtils.md5Hex("123456").toUpperCase();
                    }
                    userVo.setUserEmail(email);
                    userVo.setId(code);
                    userVo.setLoginName(code);
                    userVo.setLoginPwd(password);
                    userVo.setUserName(name);
                    userVo.setDepartmentId(map.get("dept_code").toString());
                    EntityWrapper<SysRole> wrapper=new EntityWrapper<>();
                    wrapper.where("role_code='006'");
                    SysRole sysRole=roleService.selectOne(wrapper);
                    userVo.setRoleIds(sysRole.getId());
                    EntityWrapper<SysUser> userWrapper=new EntityWrapper<>();
                    userWrapper.where("id='"+code+"'");
                    SysUser sysUser=userService.selectOne(userWrapper);
                    if(sysUser!=null){
                        logger.info("更新"+code+"---"+password+"---"+name+"----"+email);
                        userService.updateByVo(userVo);
                    }else{
                        logger.info("插入"+code+"---"+password+"---"+name+"----"+email);
                        userService.insertByVo(userVo);
                    }
                }
            }
    }

    public void executeDepartment(){
        if(ConfigUtil.getValue("syn").equals("false")){
            logger.info("不执行数据同步");
            return;
        }
        List<Map<String, Object>> list=sqlService.execQuery("select *  from org" ,null);

        for(Map<String,Object> map:list){
            String code= (String) map.get("code");
            String fatherCode= (String) map.get("father_code");
            String name= (String) map.get("name");
            SysDepartment sysDepartment=new SysDepartment();
            sysDepartment.setDepartmentCode(code);
            sysDepartment.setParentId(fatherCode);
            sysDepartment.setId(code);
            sysDepartment.setDepartmentName(name);
            sysDepartment.setDepartmentIcon("fi-folder");
            EntityWrapper<SysDepartment> entityWrapper=new EntityWrapper<>();
            entityWrapper.where("id='"+code+"'");
            SysDepartment sysDepartment1=sysDepartmentService.selectOne(entityWrapper);
            if(sysDepartment1==null) {
                sysDepartmentService.insert(sysDepartment);
            }else{
                sysDepartmentService.updateById(sysDepartment);
            }
        }
    }
    public void executeRole(){

        List<Map<String, Object>> list=sqlService.execQuery("select *  from rbac_role" ,null);

        for (Map<String,Object> map:list){

        }
    }

}
