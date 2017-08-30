package com.activiti.main;

import com.activiti.service.PublishProcessService;
import com.activiti.service.impl.PublishProcessServiceImp;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ActivityMain {
	private static Logger logger=Logger.getLogger(ActivityMain.class);
	public static void main(String[] args){
//		com.alibaba.dubbo.container.Main.main(args);
		//http://www.yiibai.com/javalang/runtime_addshutdownhook.html
		//java.lang.Runtime.addShutdownHook(Thread hook) 方法注册一个新的虚拟机关闭挂钩。 Java虚拟机的关机响应于两种类型的事件
		
//		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		 ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				 "classpath:spring/applicationContext.xml");
	        context.start();

	        PublishProcessService publishProcessService= (PublishProcessService) context.getBean("publishProcessServiceImp");
		logger.info("启动北京大区审批流程开始");
		publishProcessService.publish("beijingProcess.bpmn");
		logger.info("北京大区审批流程启动结束");
		logger.info("启动普通大区审批流程开始");
		publishProcessService.publish("common.bpmn");
		logger.info("普通大区审批流程启动结束");
		logger.info("总部大区审批流程开始");
		publishProcessService.publish("companyProcess.bpmn");
		logger.info("总部大区审批流程启动结束");
		System.out.print("-------------工作流dubbo服务启动成功---------------");
	        synchronized (ActivityMain.class) {
	            while (true) {
	                try {
						ActivityMain.class.wait();
	                } catch (Throwable e) {
	                }
	            }
	        }
	}
}
