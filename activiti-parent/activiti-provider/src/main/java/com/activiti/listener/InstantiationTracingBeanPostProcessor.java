package com.activiti.listener;

import com.activiti.main.ActivityMain;
import com.activiti.service.PublishProcessService;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by ma on 2017/8/31.
 */
public class InstantiationTracingBeanPostProcessor  implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger=Logger.getLogger(ActivityMain.class);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            PublishProcessService  publishProcessService= (PublishProcessService) contextRefreshedEvent.getApplicationContext().getBean("publishProcessServiceImp");
        /*    logger.info("启动北京大区审批流程开始");
            publishProcessService.publish("beijingProcess.bpmn");
            logger.info("北京大区审批流程启动结束");
            logger.info("启动普通大区审批流程开始");
            publishProcessService.publish("common.bpmn");
            logger.info("普通大区审批流程启动结束");
            logger.info("总部大区审批流程开始");
            publishProcessService.publish("companyProcess.bpmn");*/
        }
    }
}
