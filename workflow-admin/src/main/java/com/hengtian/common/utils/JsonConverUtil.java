package com.hengtian.common.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by ma on 2017/11/23.
 */
public class JsonConverUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonConverUtil.class);

    public JsonConverUtil() {
    }

    public static String getStrFromObject(Object object) {
        String formatString = "yyyy-MM-dd HH:mm:ss";
        return getStrFromObject(object, formatString);
    }

    public static String getStrFromObject(Object object, String formatString) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(formatString));
        String jsonString = null;
        if(null != object) {
            if(!(object instanceof Collection) && !(object instanceof Object[])) {
                jsonString = JSONObject.fromObject(object, jsonConfig).toString();
            } else {
                jsonString = JSONArray.fromObject(object, jsonConfig).toString();
            }
        }

        return jsonString == null?"{}":jsonString;
    }

    private static void setDataFormat2JAVA() {
        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpherEx(new String[]{"yyyy-MM-dd HH:mm:ss"}, (Date)null));
    }

    public static Object getObjectFromJsonString(String jsonString, Class<?> clazz) {
        JSONObject jsonObject = null;

        try {
            setDataFormat2JAVA();
            jsonObject = JSONObject.fromObject(jsonString);
        } catch (Exception var4) {
            log.error("", var4);
        }

        return JSONObject.toBean(jsonObject, clazz);
    }

    public static Object getObjectFromJsonString(String jsonString, Class<?> clazz, Map<String, Object> map) {
        JSONObject jsonObject = null;

        try {
            setDataFormat2JAVA();
            jsonObject = JSONObject.fromObject(jsonString);
        } catch (Exception var5) {
            log.error("", var5);
        }

        return JSONObject.toBean(jsonObject, clazz, map);
    }

    public static Object[] getObjectArray(String jsonString, Class<?> clazz) {
        setDataFormat2JAVA();
        JSONArray array = JSONArray.fromObject(jsonString);
        Object[] obj = new Object[array.size()];

        for(int i = 0; i < array.size(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            obj[i] = JSONObject.toBean(jsonObject, clazz);
        }

        return obj;
    }

    public static Object[] getObjectArray(String jsonString, Class<?> clazz, Map<String, Object> map) {
        setDataFormat2JAVA();
        JSONArray array = JSONArray.fromObject(jsonString);
        Object[] obj = new Object[array.size()];

        for(int i = 0; i < array.size(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            obj[i] = JSONObject.toBean(jsonObject, clazz, map);
        }

        return obj;
    }

    public static List<?> getObjectList(String jsonString, Class<?> clazz) {
        setDataFormat2JAVA();
        JSONArray array = JSONArray.fromObject(jsonString);
        List list = new ArrayList();
        Iterator iterator = array.iterator();

        while(iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject)iterator.next();
            list.add(JSONObject.toBean(jsonObject, clazz));
        }

        return list;
    }

    public static List<?> getObjectList(String jsonString, Class<?> clazz, Map<String, Object> map) {
        setDataFormat2JAVA();
        JSONArray array = JSONArray.fromObject(jsonString);
        List list = new ArrayList();
        Iterator iterator = array.iterator();

        while(iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject)iterator.next();
            list.add(JSONObject.toBean(jsonObject, clazz, map));
        }

        return list;
    }

    public static Map getMapFromJson(String jsonString) {
        setDataFormat2JAVA();
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Map map = new HashMap();
        Iterator iterator = jsonObject.keys();

        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            map.put(key, jsonObject.get(key));
        }

        return map;
    }

    public static Object[] getObjectArrayFromJson(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return jsonArray.toArray();
    }
}
