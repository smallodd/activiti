package com.hengtian.activiti.editor;

import org.activiti.engine.ActivitiException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController("myStencilsetRestResource")
public class StencilsetRestResource {
    @RequestMapping(value = {"/service/editor/stencilset"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public String getStencilset() {
        System.out.println("StencilsetRestResource.getStencilset-----------");
        InputStream stencilsetStream = getClass().getClassLoader().getResourceAsStream("stencilset.json");
        try {
            return IOUtils.toString(stencilsetStream, "utf-8");
        } catch (Exception e) {
            throw new ActivitiException("Error while loading stencil set", e);
        }
    }
}