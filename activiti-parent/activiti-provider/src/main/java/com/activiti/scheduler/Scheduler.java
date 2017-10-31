package com.activiti.scheduler;

import com.activiti.dateSource.SqlService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

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

    public void execute(){

            List<Map<String, Object>> list= sqlService.execQuery("select * from emp",null);
            if(list!=null&&list.size()>0){
               logger.info("没有数据");
            }else{
                System.out.print("没有数据");
            }


    }


}
