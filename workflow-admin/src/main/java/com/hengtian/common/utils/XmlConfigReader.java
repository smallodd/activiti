package com.hengtian.common.utils;

import net.sf.json.JSONArray;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ma on 2017/11/23.
 */
public class XmlConfigReader {
    private static final Logger log = LoggerFactory.getLogger(XmlConfigReader.class);
    private static XmlConfigReader instance = null;

    private XmlConfigReader() {
    }

    public static synchronized XmlConfigReader getInstance() {
        if(instance == null) {
            instance = new XmlConfigReader();
        }

        return instance;
    }

    public static synchronized List<RedisConfigBean> read() {
        List<RedisConfigBean> redisList = new ArrayList();
        SAXReader reader = new SAXReader();
        InputStream input = null;
        String path =FileUtil.getClassPathName("redisClusterConfig.xml");
        log.info("加载配置文件路径path={}", path);

        try {
            input = new FileInputStream(new File(path));
            log.info("集群配置文件地址：" + path);
            Document doc = reader.read(input);
            Element root = doc.getRootElement();
            if(root == null) {
                log.error("获取根节点为空，xml路径={}", path);
                return redisList;
            }

            List clusterGroupList = root.elements();
            if(clusterGroupList == null) {
                log.error("获取clusterGroup节点为空，xml路径={}", path);
                return redisList;
            }

            for(int i = 0; i < clusterGroupList.size(); ++i) {
                Object obj = clusterGroupList.get(i);
                if(obj != null && obj instanceof Element) {
                    Element clusterGroupEle = (Element)obj;
                    Attribute attrProPrefix = clusterGroupEle.attribute("proPrefix");
                    if(attrProPrefix == null) {
                        log.error("获取server节点的proPrefix属性为空，xml路径={}", path);
                    } else {
                        String proPrefix = attrProPrefix.getStringValue();
                        if(org.apache.commons.lang3.StringUtils.isBlank(proPrefix)) {
                            log.error("获取server节点的proPrefix属性为空，xml路径={}", path);
                        } else {
                            List serverList = clusterGroupEle.elements("server");
                            if(serverList == null) {
                                log.error("获取server节点为空，xml路径={}", path);
                            } else {
                                for(int j = 0; j < serverList.size(); ++j) {
                                    Object serverObj = serverList.get(j);
                                    if(serverObj != null && serverObj instanceof Element) {
                                        Element serverEle = (Element)serverObj;
                                        Attribute attrHost = serverEle.attribute("host");
                                        Attribute attrPort = serverEle.attribute("port");
                                        String host = null;
                                        String port = null;
                                        if(attrHost != null) {
                                            host = attrHost.getStringValue();
                                        }

                                        if(attrPort != null) {
                                            port = attrPort.getStringValue();
                                        }

                                        if(!org.apache.commons.lang3.StringUtils.isBlank(host) && !org.apache.commons.lang3.StringUtils.isBlank(port)) {
                                            RedisConfigBean bean = new RedisConfigBean();
                                            bean.setProPrefix(proPrefix);
                                            bean.setHost(host);
                                            bean.setPort(Integer.valueOf(Integer.valueOf(port).intValue()));
                                            log.info("配置文件解析节点：" + bean.toString());
                                            redisList.add(bean);
                                        } else {
                                            log.error("获取节点属性有为空情况，IP={},Port={}", host, port);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception var21) {
            log.error("读取xml异常,xml路径={}", path, var21);
        }

        log.info("配置文件解析：" + JSONArray.fromObject(redisList));
        return redisList;
    }
    public static void  main(String[] arg){
        String path =FileUtil.getClassPathName("redisClusterConfig.xml");
        System.out.println(111+path);
    }
}
