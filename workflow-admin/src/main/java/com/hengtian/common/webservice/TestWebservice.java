package com.hengtian.common.webservice;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
@Deprecated
public interface TestWebservice {
	
	@WebMethod
	@WebResult(name="result")
	String testService(String msg);

}
