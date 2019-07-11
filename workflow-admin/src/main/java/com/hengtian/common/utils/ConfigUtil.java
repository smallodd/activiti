package com.hengtian.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by ma on 2017/11/3.
 */

@Slf4j
public class ConfigUtil {
    /* 日志对象 */
    /* 防盗链网址1 */
    private static final String WEB_URL_1 = "web.url1";
    /* 防盗链网址2 */
    private static final String WEB_URL_2 = "web.url2";
    /* 是否初始化： true：初始化过 */
    private static boolean isInit = false;

    /* 属性集合 */
    private static Map<String, String> map = new HashMap<String, String>();
    /** 防盗链url集合 */
    private static Map<String, String> webUrlMap = new HashMap<String, String>();

    static {
        initData();
    }

    /**
     *
     * 初始化数据
     *
     */
    private static void initData() {
        if (isInit) {
            return;
        }
        isInit = true;

        if (log.isDebugEnabled()) {
            log.debug("初始化config.properties开始");
        }

        // 获取文件路径
        String path = getClassPath() + "application.properties";
        // String
        // path="D:/workspace-gxdp/gxdp-parent/gxdp-web/src/main/resources/config.properties";

        // 读文件
        Properties properties = readProperties(path);
        if (properties == null) {
            if (log.isDebugEnabled()) {
                log.debug("获取文件信息失败");
            }
            return;
        }

        // 获取所有值
        getValues(properties);

        if (log.isDebugEnabled()) {
            log.debug("初始化config.properties结束");
        }

    }

    private static void getValues(Properties prop) {
        // 获取Properties key集合
        Set keyValue = prop.keySet();

        for (Iterator it = keyValue.iterator(); it.hasNext();) {
            Object obj=it.next();
            if(obj==null){
                continue;
            }
            // 键
            String key = String.valueOf(obj);
            // 值
            String value = null;
            if (StringUtils.isNotBlank(key)) {
                // 值
                value = prop.getProperty(key);
                if (StringUtils.isNotBlank(value)) {
                    map.put(key, value);

                    if (WEB_URL_1.equals(key) || WEB_URL_2.equals(key)) {
                        webUrlMap.put(key, value);
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("key={};value={}", key, value);
            }
        }
    }

    /**
     *
     * 获取给定key对应的value
     *
     * @param key
     *            String
     * @return String key对应的value 或null
     */
    public static String getValue(String key) {
        if (StringUtils.isNotBlank(key)) {
            return map.get(key);
        }
        return null;
    }
    public static String getClassPath() {
        return ConfigUtil.class.getResource("/").getPath();
    }

    /**
     *
     * 获取给定的property文件内容
     *
     * 返回为null时读取文件失败
     *
     * @param path
     *            String 文件路径
     * @return Properties Properties对象或NULL
     */
    public static Properties readProperties(String path) {
        if (StringUtils.isEmpty(path)) {
            log.error("文件路径为空");
            return null;
        }

        // 属性对象
        Properties properties = new Properties();
        FileInputStream inputStream = null;

        try {
            // 读取文件
            inputStream = new FileInputStream(path);
            properties.load(inputStream);
        } catch (Exception e) {
            log.error("文件路径{}", path, e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("文件路径{}", path, e);
                    return null;
                }
            }
        }
        return properties;
    }


}
