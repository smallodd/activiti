package com.chtwm.workflow.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Date: 2018/6/29
 * Time: 17:27
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 */
@Slf4j
@Component
public class MyApplicationContextAware implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (MyApplicationContextAware.applicationContext == null) {
            MyApplicationContextAware.applicationContext = applicationContext;
        }
    }

    //获取applicationContext
    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}