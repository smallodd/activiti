package com.hengtian.common.shiro;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

/**
 * @description：自定义Authentication对象，
 * 使得Subject除了携带用户的登录名外还可以携带更多信息
 */
@Component
public class ShiroUser implements Serializable {
    private static final long serialVersionUID = -1373760761780840081L;
    
    private String id;
    private String loginName;
    private String name;
    private Set<String> urlSet;
    private Set<String> roles;

    public ShiroUser(){}

    public ShiroUser(String loginName) {
        this.loginName = loginName;
    }

    public ShiroUser(String id, String loginName, String name, Set<String> urlSet) {
        this.id = id;
        this.loginName = loginName;
        this.name = name;
        this.urlSet = urlSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getUrlSet() {
        return urlSet;
    }

    public void setUrlSet(Set<String> urlSet) {
        this.urlSet = urlSet;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getLoginName() {
        return loginName;
    }

    /**
     * 本函数输出将作为默认的<shiro:principal/>输出.
     */
    @Override
    public String toString() {
        return loginName;
    }
}