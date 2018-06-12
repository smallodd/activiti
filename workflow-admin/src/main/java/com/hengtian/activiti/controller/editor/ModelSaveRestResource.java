package com.hengtian.activiti.controller.editor;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 保存模型
 *
 * @author houjinrong@chtwm.com
 * date 2018/6/12 9:45
 */
@RestController
public class ModelSaveRestResource implements ModelDataJsonConstants {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = {"/service/model/{modelId}/save"})
    @ResponseStatus(HttpStatus.OK)
    public void saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values, HttpServletRequest request) {
        try {
            Model model = this.repositoryService.getModel(modelId);
            System.out.println("ModelSaveRestResource.saveModel----------");
            ObjectNode modelJson = (ObjectNode) this.objectMapper.readTree(model.getMetaInfo());

            modelJson.put("name", values.getFirst("name"));
            modelJson.put("description", values.getFirst("description"));
            model.setMetaInfo(modelJson.toString());
            model.setName(values.getFirst("name"));
            String str = (values.getFirst("json_xml"));
            JSONObject jsonObject = JSONObject.parseObject(str);
            jsonObject.getJSONObject("properties").put("process_id", model.getKey());
            //每次修改模型，版本升级
            model.setVersion(model.getVersion() + 1);
            this.repositoryService.saveModel(model);

            this.repositoryService.addModelEditorSource(model.getId(), (jsonObject.toJSONString()).getBytes("utf-8"));

            InputStream svgStream = new ByteArrayInputStream((values.getFirst("svg_xml")).getBytes("utf-8"));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            transcoder.transcode(input, output);
            byte[] result = outStream.toByteArray();

            this.repositoryService.addModelEditorSourceExtra(model.getId(), result);
        } catch (Exception e) {
            LOGGER.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
    }
}