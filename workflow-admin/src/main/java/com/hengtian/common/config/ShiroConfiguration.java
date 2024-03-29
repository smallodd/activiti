package com.hengtian.common.config;

import com.hengtian.common.shiro.UserRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;

/**
 * 功能描述:shiro配置
 * @Author: hour
 * @Date: 2019/6/12 14:43
 */
@Slf4j
@Configuration
public class ShiroConfiguration {

    @Bean(name="authRealm")
    public UserRealm userRealm(){
        return new UserRealm();
    }

    @Bean(name="securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("authRealm") UserRealm authRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(authRealm);
        return securityManager;
    }

    @Bean(name="shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //默认的登陆访问url
        shiroFilterFactoryBean.setLoginUrl("/login");
        //登陆成功后跳转的url
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //没有权限跳转的url
        shiroFilterFactoryBean.setUnauthorizedUrl("/login");
        /**
         * anon:不需要认证
         * authc:需要认证
         * user:验证通过或RememberMe登录的都可以
         */
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/resource/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/swagger-ui.*", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/configuration/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/swagger-resources", "anon");
        filterChainDefinitionMap.put("/editor-app", "anon");
        filterChainDefinitionMap.put("/diagram-viewer", "anon");
        filterChainDefinitionMap.put("/sysUser/passwordForget", "anon");
        filterChainDefinitionMap.put("/sysUser/passwordReset", "anon");
        filterChainDefinitionMap.put("/sysUser/resetPassword", "anon");
        filterChainDefinitionMap.put("/createCaptcha", "anon");
        filterChainDefinitionMap.put("/sysUser/password/mailValidate", "anon");
        filterChainDefinitionMap.put("/rest/**", "anon");
        filterChainDefinitionMap.put("/diagram-viewer/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean(name="lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
