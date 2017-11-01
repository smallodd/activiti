package com.activiti.scheduler;

import com.activiti.dateSource.SqlService;
import com.activiti.entity.UserVo;
import com.activiti.service.UserRoleService;
import com.common.util.ConfigUtil;
import org.activiti.engine.IdentityService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/10/31.
 */
public class Scheduler {
    private final Logger logger=Logger.getLogger(Scheduler.class);
    @Autowired
    SqlService sqlService;
    @Autowired
    IdentityService identityService;
    @Autowired
    UserRoleService userRoleService;
    public void execute(){
            if(ConfigUtil.getValue("syn").equals("false")){
                logger.info("不执行数据同步");
                return;
            }

            //同步用户信息
            long count=identityService.createUserQuery().count();
            List<Map<String, Object>> countList=sqlService.execQuery("select count(*) count from emp" ,null);
            Long empCount=Long.valueOf(countList.get(0).get("count").toString());
            List<Map<String, Object>> list=new ArrayList<>();
            if(empCount-count>15000) {
                list = sqlService.execQuery("select * from emp", null);
            }else{
                list= sqlService.execQuery("select * from emp where create_time >DATE_SUB(NOW(),INTERVAL '2 0:0:0' DAY) ORDER BY crean_time DESC", null);
            }
            if(list==null||list.size()==0){
               logger.info("没有数据");
            }else{
                List<UserVo> userVoList =new ArrayList<UserVo>();
                for(Map<String,Object> map:list) {

                    UserVo userVo=new UserVo();
                    String code = (String) map.get("code");
                    String password = (String) map.get("password");
                    String name = (String) map.get("name");
                    String email = (String) map.get("email");
                    System.out.print(code+"---"+password+"---"+name+"----"+email);
                    userVo.setEmail(email);
                    userVo.setId(code);
                    userVo.setPassword(password);
                    userVo.setFirstName(name);
                    userVoList.add(userVo);
                }
                userRoleService.addUser(userVoList);
            }


    }


}
