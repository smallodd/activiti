package com.activiti.main;

import com.activiti.service.PublishProcessService;
import com.activiti.service.impl.PublishProcessServiceImp;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.applet.Main;

public class ActivityMain {
	private static Logger logger=Logger.getLogger(ActivityMain.class);
	public static void main(String[] args){
//		com.alibaba.dubbo.container.Main.main(args);
		//http://www.yiibai.com/javalang/runtime_addshutdownhook.html
		//java.lang.Runtime.addShutdownHook(Thread hook) 方法注册一个新的虚拟机关闭挂钩。 Java虚拟机的关机响应于两种类型的事件
		
//		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		 ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				 "classpath:spring/applicationContext.xml");
		 logger.info("开始============================================");
	        context.start();


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
