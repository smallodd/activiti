package com.hengtian.activiti.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hengtian.activiti.service.ActivitiModelService;
import com.hengtian.application.model.TVacation;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.utils.PageInfo;
import net.sf.json.JSONObject;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/activiti/model")
public class ActivitiModelController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    RepositoryService repositoryService;

    /**
     * 流程模型管理页
     * @author houjinrong
     * @return
     */
    @RequestMapping("/modelManager")
    public String modelManager(){
        return "activiti/model/index";
    }

    /**
     * 查询流程定义
     * @author houjinrong
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog(value="查询流程模型")
    @PostMapping("/modelDataGrid")
    @ResponseBody
    public PageInfo dataGrid(Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows);
        activitiModelService.selectActivitiModelDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 创建流程模型页面
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "activiti/model/add";
    }

    /**
     * 创建模型
     */
    @RequestMapping("/create")
    public void create(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, "lutiannan");
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            String description = "lutiannan---";
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName("lutiannan");
            modelData.setKey("12313123");

            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
        } catch (Exception e) {
            System.out.println("创建模型失败：");
        }
    }

    /**
     * 修改模型
     */
    @RequestMapping("/update/{modelId}")
    public void update(@PathVariable String modelId, HttpServletRequest request, HttpServletResponse response) {
        try {
            Model model = repositoryService.getModel(modelId);
            if(model != null){
                response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelId);
            }else{
                response.sendRedirect(request.getContextPath() + "/editor/create");
            }
        } catch (Exception e) {
            System.out.println("编辑模型失败：");
        }
    }

    /**
     * 根据模型部署流程
     */
    @SysLog(value="激活流程")
    @ResponseBody
    @RequestMapping(value = "/deploy/{modelId}")
    public Object deploy(@PathVariable("modelId") String modelId) {
        JSONObject result = new JSONObject();
        try {
            Model modelData = repositoryService.getModel(modelId);
            ObjectNode modelNode = (ObjectNode) new ObjectMapper()
                    .readTree(repositoryService.getModelEditorSource(modelData.getId()));
            byte[] bpmnBytes = null;

            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);

            String processName = modelData.getName() + ".bpmn20.xml";

            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName()).addString(processName, new String(bpmnBytes,"UTF-8"))
                    .deploy();

	        /*Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
	                .addBpmnModel(modelData.getName(), bpmnModel).deploy();*/

            logger.info("部署成功");
            return renderSuccess("流程部署成功！");
        } catch (Exception e) {
            logger.error("部署失败",e);
            e.printStackTrace();
            return renderError("流程部署失败！");
        }
    }

    /**
     * 查看流程图
     */
    @GetMapping("/image/{modelId}")
    public void modelImage(@PathVariable String modelId,HttpServletResponse response){
        try {
            byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(modelId);
            if(modelEditorSourceExtra != null && modelEditorSourceExtra.length > 0){
                InputStream in = new ByteArrayInputStream(modelEditorSourceExtra);
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = in.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出model的xml文件
     */
    @RequestMapping(value = "/export/{modelId}")
    public void export(@PathVariable("modelId") String modelId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            Model modelData = repositoryService.getModel(modelId);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            //获取节点信息
            byte[] arg0 = repositoryService.getModelEditorSource(modelData.getId());
            JsonNode editorNode = new ObjectMapper().readTree(arg0);
            //将节点信息转换为xml
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
//                String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            String filename = modelData.getName() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            response.flushBuffer();
        } catch (Exception e){
            PrintWriter out = null;
            try {
                out = response.getWriter();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            out.write("未找到对应数据");
            e.printStackTrace();
        }
    }
}
