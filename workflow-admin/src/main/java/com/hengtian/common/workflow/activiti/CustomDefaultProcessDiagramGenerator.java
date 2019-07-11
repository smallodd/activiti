package com.hengtian.common.workflow.activiti;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
@Component
public interface CustomDefaultProcessDiagramGenerator extends ProcessDiagramGenerator {

    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows,
                                       String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor, List<String> taskDefKeyList);
}
