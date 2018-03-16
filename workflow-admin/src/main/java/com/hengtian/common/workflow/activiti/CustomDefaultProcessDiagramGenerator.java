package com.hengtian.common.workflow.activiti;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.image.ProcessDiagramGenerator;

import java.io.InputStream;
import java.util.List;

public interface CustomDefaultProcessDiagramGenerator extends ProcessDiagramGenerator {

    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows,
                                       String activityFontName, String labelFontName, ClassLoader customClassLoader, double scaleFactor, String currentActivityId);
}
