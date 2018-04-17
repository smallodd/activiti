package com.hengtian.common.webservice;

import javax.xml.ws.Endpoint;

@Deprecated
public class TestMain {
	public static void main(String[] args) {
		//发布Webservice服务
		Endpoint.publish("http://localhost:8080/testws", new TestWebserviceImpl());
		System.out.println("服务运行中...");
		try {
			Thread.sleep(1000*60*3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("服务已关闭...");  
		System.exit(0);
	}
}
