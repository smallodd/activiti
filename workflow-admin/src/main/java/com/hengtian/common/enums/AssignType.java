package com.hengtian.common.enums;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2018/4/18.
 */
public enum AssignType {
    PERSON(3,"人员"),
    DEPARTMENT(1,"部门"),
    ROLE(2,"角色"),
    EXPR(4,"表达式");
    public Integer code;

    public String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    AssignType(Integer code, String name) {
        this.code=code;
        this.name=name;
    }

    public static Integer getCode(String name){
        for(AssignType assignType:values()){
            if(assignType.getName().equals(name)){
                return  assignType.code;
            }
        }
        return null;
    }

    public static String getName(int code){
        for(AssignType assignType:values()){
            if(assignType.getCode().intValue()==code){
                return  assignType.name;
            }
        }
        return null;
    }

    public static List<AssignType> getList(){
        List<AssignType> list=new ArrayList<>();
        for(AssignType assignType:values()){
              list.add(assignType);
        }
        return list;
    }

    public static boolean checkExist(int code){
        for(AssignType assignType:values()){
            if(assignType.getCode().intValue()==code){
                return  true;
            }
        }
        return false;
    }

    public static Map<Integer, String> getAssignpeMap(){
        Map<Integer,String> map = Maps.newLinkedHashMap();
        for (AssignType c : AssignType.values()) {
            map.put(c.code,c.name);
        }
        return map;
    }
}
