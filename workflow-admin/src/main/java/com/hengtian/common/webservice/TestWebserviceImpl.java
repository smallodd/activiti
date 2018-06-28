package com.hengtian.common.webservice;

import javax.jws.WebService;

@Deprecated
@WebService(endpointInterface="com.hengtian.common.webservice.TestWebservice",serviceName="TestWebservice")
public class TestWebserviceImpl implements TestWebservice {

	@Override
	public String testService(String msg) {
		String result="您发送的消息是: "+msg;
		return result;
	}

}
