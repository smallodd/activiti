package com.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * webservice任务 客户端
 * @author liujunyang
 */
@Component("wsTaskListener")
public class WsTaskListener implements JavaDelegate,Serializable{
	private static final long serialVersionUID = 6106724547979111604L;

	private Expression namespace;//Webservice命名空间
	private Expression wsdl;//Webservice地址
	private Expression operation;//Webservice方法名
	private Expression msg;//Webservice方法参数名
	
	public Expression getNamespace() {
		return namespace;
	}
	public void setNamespace(Expression namespace) {
		this.namespace = namespace;
	}
	public Expression getWsdl() {
		return wsdl;
	}
	public void setWsdl(Expression wsdl) {
		this.wsdl = wsdl;
	}
	public Expression getOperation() {
		return operation;
	}
	public void setOperation(Expression operation) {
		this.operation = operation;
	}
	public Expression getMsg() {
		return msg;
	}
	public void setMsg(Expression msg) {
		this.msg = msg;
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try{  
	        RPCServiceClient client = new RPCServiceClient();  
	        Options options = client.getOptions();
	        EndpointReference end = new EndpointReference(wsdl.getValue(execution).toString());  
	        options.setTo(end);
	        Object[] obj = new Object[]{msg.getValue(execution).toString()};
	        Class<?>[] classes = new Class[] { String.class };
	        QName qname = new QName(namespace.getValue(execution).toString(), operation.getValue(execution).toString());  
	        String result = (String) client.invokeBlocking(qname, obj,classes)[0];  
	        System.out.println(result);  
	    }catch(AxisFault e){  
	        e.printStackTrace();  
	    }  
	}

}
