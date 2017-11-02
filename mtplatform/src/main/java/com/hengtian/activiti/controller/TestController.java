package com.hengtian.activiti.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.service.TMailLogService;
import com.hengtian.activiti.service.TUserTaskService;
import com.hengtian.application.service.TVacationService;
import com.hengtian.common.base.BaseController;
import com.hengtian.system.service.SysDepartmentService;
import com.hengtian.system.service.SysUserService;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

@Controller
@RequestMapping("/activiti/test")
public class TestController extends BaseController{
	Logger logger = Logger.getLogger(TestController.class);
	
	@Autowired
	private ActivitiService activitiService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired 
	private TVacationService tVacationService;
	@Autowired
    private SysUserService sysUserService;
	@Autowired
    private TUserTaskService tUserTaskService;
	@Autowired
	private TMailLogService tMailLogService;
	@Autowired
	private SysDepartmentService sysDepartmentService;
	
	@RequestMapping(value = "deploy/{modelId}")  
	public String deploy(@PathVariable("modelId") String modelId, RedirectAttributes redirectAttributes) {  
		JSONObject result = new JSONObject();
	    try {
	    	Model modelData = repositoryService.getModel(modelId);  
	        ObjectNode modelNode = (ObjectNode) new ObjectMapper()  
	                .readTree(repositoryService.getModelEditorSource(modelData.getId()));  
	        byte[] bpmnBytes = null;  
	  
	        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);  
	        bpmnBytes = new BpmnXMLConverter().convertToXML(model);  
	  
	        //String processName = modelData.getName() + ".bpmn20.xml";  
	     
	        Deployment deployment = repositoryService.createDeployment()  
	                .name(modelData.getName()).addString("哈哈哈", new String(bpmnBytes,"UTF-8"))  
	                .deploy();  
	        
	        /*Deployment deployment = repositoryService.createDeployment().name(modelData.getName())  
	                .addBpmnModel(modelData.getName(), bpmnModel).deploy();*/
	        result.put("msg", "部署成功");
	        result.put("type", "success");
	        
	        
	    } catch (Exception e) {
	        result.put("msg", "部署失败");
	        result.put("type", "error");
	        e.printStackTrace();
	    }
	    return result.toString();
	} 
	
	private XMLStreamReader convertJsonToXml(String str) {
		XMLStreamReader reader = null;
		 try {  
            XMLInputFactory factory = XMLInputFactory.newInstance();  
            InputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));   
            reader = factory.createXMLStreamReader(in);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return reader;
	}
}
