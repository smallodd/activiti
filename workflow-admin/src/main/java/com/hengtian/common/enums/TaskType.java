package com.hengtian.common.enums;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum TaskType {
    //审核人
    ASSIGNEE("assignee","审批"),
    //会签
    COUNTERSIGN("counterSign","会签"),
    //候选人
    CANDIDATEUSER("candidateUser","候选");

    public String value;

    public String name;

    TaskType(String value,String name){
        this.value = value;
        this.name=name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getValue(String name) {
        for (TaskType c : TaskType.values()) {
            if (c.getName().equals(name) ) {
                return c.value;
            }
        }
        return null;
    }

    public static String getName(String value) {
        for (TaskType c : TaskType.values()) {
            if (c.getValue().equals(value) ) {
                return c.name;
            }
        }
        return null;
    }

    public static boolean checkExist(String value){
        for (TaskType c : TaskType.values()) {
            if (c.getValue().equals(value) ) {
                return true;
            }
        }
        return false;
    }

    public static List<TaskType> getTaskTypeList(){
        List<TaskType> taskTypes=new ArrayList<>();
        for (TaskType c : TaskType.values()) {
          taskTypes.add(c);
        }
        return taskTypes;
    }

    public static Map<String, String> getTaskTypeMap(){
        Map<String,String> map = Maps.newLinkedHashMap();
        for (TaskType c : TaskType.values()) {
            map.put(c.value,c.name);
        }
        return map;
    }
}
