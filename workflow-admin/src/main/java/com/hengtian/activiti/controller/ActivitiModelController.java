package com.hengtian.activiti.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hengtian.activiti.model.TUserTask;
import com.hengtian.activiti.service.ActivitiModelService;
import com.hengtian.activiti.service.TUserTaskService;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.Tree;
import com.hengtian.common.utils.FileUtil;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import net.sf.json.JSONObject;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/activiti/model")
public class ActivitiModelController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String model_version = "1.0";

    private final String default_model_name = "未命名模型";

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    StrongUuidGenerator uuidGenerator;

    @Autowired
    TUserTaskService tUserTaskService;

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
    public PageInfo dataGrid(Integer page, Integer rows, String sort, String order,String name) {
        name = StringUtils.isBlank(name)?null:name.trim();
        PageInfo pageInfo = new PageInfo(page, rows);
        pageInfo.setOrder(order);
        pageInfo.setSort(sort);
        activitiModelService.selectActivitiModelDataGrid(pageInfo,name);
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
     * 复制流程页面
     */
    @GetMapping("/copyPage/{modelId}")
    public String copyPage(@PathVariable String modelId, org.springframework.ui.Model model) {
        model.addAttribute("id",modelId);
        return "activiti/model/copy";
    }  /**
     * 重置key页面
     */
    @GetMapping("/resetKey/{modelId}")
    public String resetKey(@PathVariable String modelId, org.springframework.ui.Model model) {
        model.addAttribute("id",modelId);
        return "activiti/model/resetKey";
    }
    /**
     * 创建模型
     */
    @SysLog(value="创建模型")
    @PostMapping("/create")
    @ResponseBody
    public Object create(String name,String key,String description,HttpServletRequest request, HttpServletResponse response) {
        Result result = new Result();
        try {
            if(StringUtils.isNotBlank(key)){
                Model model = repositoryService.createModelQuery().modelKey(key).singleResult();
                if(model != null){
                    String msg = "创建模型时KEY重复";
                    logger.info(msg);
                    result.setSuccess(false);
                    result.setMsg(msg);
                    return result;
                }
            }else{

                key = RandomStringUtils.randomAlphabetic(3)+new Date().getTime()+ RandomStringUtils.randomNumeric(5);
            }
            name = StringUtils.isBlank(name)?default_model_name:name.trim();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, model_version);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(name);
            modelData.setKey(key);

            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            //response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());

            logger.info("创建模型成功");
            result.setObj(modelData.getId());
            result.setSuccess(true);
            result.setMsg("创建模型成功");
        } catch (Exception e) {
            String msg = "创建模型失败";
            logger.error(msg,e);
            result.setSuccess(false);
            result.setMsg(msg);
        }

        return com.alibaba.fastjson.JSONObject.toJSONString(result);
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
     * 复制流程
     */
    @SysLog(value="复制流程")
    @ResponseBody
    @RequestMapping(value = "/copy")
    public Object copy( String id,String name,String key,HttpServletRequest request) {

        JSONObject result = new JSONObject();
        if(StringUtils.isNotBlank(key)){
            Model model = repositoryService.createModelQuery().modelKey(key).singleResult();
            if(model != null){
                String msg = "复制模型时KEY重复";
                logger.info(msg);
                return renderError(msg);
            }
        }else{
            key = uuidGenerator.getNextId();
        }
        try {

            //获取原模型数据
            Model modelData = repositoryService.getModel(id);
            //获取原节点数据
            ObjectNode modelNode = (ObjectNode) new ObjectMapper()
                    .readTree(repositoryService.getModelEditorSource(modelData.getId()));
            ObjectNode properties = (ObjectNode) modelNode.path("properties");
            properties.put("process_id",key);
            modelNode.set("properties",properties);

            String metaInfo=modelData.getMetaInfo();
            JSONObject jsonObject=JSONObject.fromObject(metaInfo);
            Model model=repositoryService.newModel();
            jsonObject.put("name",name);
            jsonObject.put("description",jsonObject.get("description"));
            model.setMetaInfo(jsonObject.toString());
            model.setKey(key);
            model.setName(name);
            repositoryService.saveModel(model);


            repositoryService.addModelEditorSource(model.getId(), modelNode.toString().getBytes("utf-8"));
            repositoryService.addModelEditorSourceExtra(model.getId(),repositoryService.getModelEditorSourceExtra(modelData.getId()));
            String contextPath = request.getSession().getServletContext().getRealPath("image");
            File srcFile=new File(contextPath+File.separator+modelData.getId()+".png");
            File destFile=new File(contextPath+File.separator+model.getId()+".png");
            FileUtil.copyFile(srcFile,destFile);

            logger.info("复制成功");
            return renderSuccess("复制成功！");
        } catch (Exception e) {
            logger.error("复制失败",e);
            return renderError("复制失败！");
        }
    } /**
     * 重置key
     */
    @SysLog(value="重置key")
    @ResponseBody
    @RequestMapping(value = "/resetKey")
    public Object resetKey( String id,String key) {

        JSONObject result = new JSONObject();
        if(StringUtils.isNotBlank(key)){
            Model model = repositoryService.createModelQuery().modelKey(key).singleResult();
            if(model != null){
                String msg = "重置key时失败，key已存在";
                logger.info(msg);
                return renderError(msg);
            }
        }else{
            key = uuidGenerator.getNextId();
        }
        try {

            //获取原模型数据
            Model modelData = repositoryService.getModel(id);
            //获取原节点数据
            ObjectNode modelNode = (ObjectNode) new ObjectMapper()
                    .readTree(repositoryService.getModelEditorSource(modelData.getId()));
            ObjectNode properties = (ObjectNode) modelNode.path("properties");
            properties.put("process_id",key);
            modelNode.set("properties",properties);

            modelData.setKey(key);

            repositoryService.saveModel(modelData);

            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));


            logger.info("重置key成功");
            return renderSuccess("重置key成功！");
        } catch (Exception e) {
            logger.error("重置key失败",e);
            return renderError("重置key失败！");
        }
    }

    /**
     * 根据模型部署流程
     */
    @SysLog(value="根据模型部署流程")
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
            Collection<FlowElement> flowElements=model.getMainProcess().getFlowElements();

            boolean startEvent=false;
            boolean endEvent=false;
            for(FlowElement flowElement:flowElements){
                if(startEvent&&endEvent){
                    break;
                }
                if(flowElement instanceof StartEvent){
                    startEvent=true;
                }
                if(flowElement instanceof EndEvent){
                    endEvent=true;
                }

            }
            if(!startEvent||!endEvent){
                 return renderError("开始节点和结束节点必须同时拥有才能部署！");
            }
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);

            String processName = modelData.getName() + ".bpmn20.xml";

            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName()).addString(processName, new String(bpmnBytes,"UTF-8"))
                    .deploy();
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);
	        /*Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
	                .addBpmnModel(modelData.getName(), bpmnModel).deploy();*/

            logger.info("部署成功");
            return renderSuccess("流程部署成功！");
        } catch (Exception e) {
            logger.error("部署失败",e);
            return renderError("流程部署失败！");
        }
    }

    /**
     * 查看流程图
     */
    @GetMapping("/image/{modelId}")
    public void modelImage(@PathVariable String modelId,HttpServletResponse response,HttpServletRequest request){
        try {
            String contextPath = request.getSession().getServletContext().getRealPath("image");
            FileInputStream fileInputStream=new FileInputStream(contextPath+File.separator+modelId+".png");


                byte[] b = new byte[1024];
                int len = -1;
                while ((len = fileInputStream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
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

    /**
     * 查询所有的模型tree
     */
    @RequestMapping("/allTrees")
    @ResponseBody
    public Object allTree() {
        List<Tree> trees = new ArrayList<Tree>();
        List<Model> list = repositoryService.createModelQuery().deployed().list();
        if(CollectionUtils.isNotEmpty(list)){
            for(Model model : list){
                Tree tree = new Tree();
                tree.setId(model.getKey());
                tree.setPid("0");
                tree.setText(model.getName());
                tree.setIconCls("fi-folder");
                tree.setAttributes(null);
                trees.add(tree);
            }
        }

        return trees;
    }
    @ResponseBody
    @RequestMapping(value = "/deleteModel")
    public Object deleteModel(String id){
        Model model=repositoryService.getModel(id);
        if(StringUtils.isNotBlank(model.getDeploymentId())){
            EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
            ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
            wrapper.where("proc_def_key={0}",processDefinition.getKey());
            tUserTaskService.delete(wrapper);
            repositoryService.deleteDeployment(model.getDeploymentId(),true);

        }
        repositoryService.deleteModel(id);

        return renderSuccess("删除成功！");
    }
}
