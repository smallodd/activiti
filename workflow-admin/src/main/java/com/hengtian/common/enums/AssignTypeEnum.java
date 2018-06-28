package com.hengtian.common.enums;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 审批人类型
 * @author mayunliang@chtwm.com
 * date 2018/5/29 13:36
 */
public enum AssignTypeEnum {
    PERSON(3,"人员"),
    //DEPARTMENT(1,"部门"),
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

    AssignTypeEnum(Integer code, String name) {
        this.code=code;
        this.name=name;
    }

    public static Integer getCode(String name){
        for(AssignTypeEnum assignTypeEnum :values()){
            if(assignTypeEnum.getName().equals(name)){
                return  assignTypeEnum.code;
            }
        }
        return null;
    }

    public static String getName(int code){
        for(AssignTypeEnum assignTypeEnum :values()){
            if(assignTypeEnum.getCode().intValue()==code){
                return  assignTypeEnum.name;
            }
        }
        return null;
    }

    public static List<AssignTypeEnum> getList(){
        List<AssignTypeEnum> list=new ArrayList<>();
        for(AssignTypeEnum assignTypeEnum :values()){
              list.add(assignTypeEnum);
        }
        return list;
    }

    public static boolean checkExist(int code){
        for(AssignTypeEnum assignTypeEnum :values()){
            if(assignTypeEnum.getCode().intValue()==code){
                return  true;
            }
        }
        return false;
    }

    public static Map<Integer, String> getAssignpeMap(){
        Map<Integer,String> map = Maps.newLinkedHashMap();
        for (AssignTypeEnum c : AssignTypeEnum.values()) {
            map.put(c.code,c.name);
        }
        return map;
    }

    public static void main(String[] args) {
        String str = "4545_Y,535335_N";
        System.out.println(str.replaceAll("_Y", ""));
    }
}
