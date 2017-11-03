<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>主页</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${ctx}/resource/sysjs/index.js"></script>
</head>
<body>
    <div id="loading" style="position: fixed;top: -50%;left: -50%;width: 200%;height: 200%;background: #fff;z-index: 100;overflow: hidden;">
        <img src="${ctx}/resource/static/style/images/ajax-loader.gif" style="position: absolute;top: 0;left: 0;right: 0;bottom: 0;margin: auto;"/>
    </div>
    <div id="index_layout">
        <div data-options="region:'north',border:false" style="overflow: hidden;height:50px">
            <div>
                <span style="float: right; padding-right: 20px; margin-top: 15px; color: #333">
                    <i class="fi-torso"></i>
                    <b><shiro:principal></shiro:principal></b>&nbsp;&nbsp; 
                    <shiro:hasPermission name="/sysUser/editPwdPage">
                        <a href="javascript:void(0)" onclick="editUserPwd()" class="easyui-linkbutton" plain="true" icon="fi-unlock" >修改密码</a>
                    </shiro:hasPermission>&nbsp;&nbsp;
                    <a href="javascript:void(0)" onclick="logout()" class="easyui-linkbutton" plain="true" icon="fi-x">安全退出</a>
                </span>
                <span class="header"></span>
            </div>
        </div>
        <div data-options="region:'west',split:true" title="菜单" style="width: 160px; overflow: hidden;overflow-y:auto; padding:0px">
            <div class="well well-small" style="padding: 5px 5px 5px 5px;">
                <ul id="layout_west_tree"></ul>
            </div>
        </div>
        <div data-options="region:'center'" style="overflow: hidden;">
            <div id="index_tabs" style="overflow: hidden;">
                <div title="首页" data-options="iconCls:'fi-home',border:false" style="overflow: hidden;">
                	<div id="main" style="width:100%;height:100%;"></div>
                </div>
            </div>
        </div>
        <div data-options="region:'south',border:false" style="height: 30px;line-height:30px; overflow: hidden;text-align: center;background-color: #eee" ></div>
    </div>
    <div id="tabsMenu">
        <div data-options="iconCls:'fi-loop'" type="refresh" style="font-size: 12px;">刷新</div>
        <div class="menu-sep"></div>
        <div data-options="iconCls:'fi-x'" type="close" style="font-size: 12px;">关闭</div>
        <div data-options="iconCls:''" type="closeOther">关闭其他</div>
        <div data-options="iconCls:''" type="closeAll">关闭所有</div>
    </div>
</body>
</html>