

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>重置密码</title>
</head>
<body>
<div style="margin:20px 0;"></div>
<div id="resetPass">
<div class="easyui-panel" title="重置密码" style="width:400px;padding:30px 60px">
    <c:if test="${flag==-1}">请求不合法</c:if>
    <c:if test="${flag==0}">链接已过期，请重试</c:if>
    <c:if test="${flag==1}">
    <form id="updatePasswordForm" method="get">
        <input type="hidden" name="loginName" value="${loginName}"/>
        <a href="javascript:void(0)" class="easyui-linkbutton" style="width:100%;height:32px;margin-bottom:8px;color:steelblue;">提示：密码只能为字母数字下划线，长度为6-20位</a>
        <div style="margin-bottom:20px">
            <div>新密码:</div>
            <input type="password" name="password" id="newPassword" class="easyui-textbox" data-options="required:true,validType:['password'],iconCls:'icon-lock',iconWidth:38" placeholder="请输入新密码" style="width:100%;height:40px">
        </div>
        <div style="margin-bottom:20px">
            <div>确认密码:</div>
            <input type="password" name="confirmPassword" class="easyui-textbox" data-options="required:true,validType:['equalTo[\'#newPassword\']','password'],iconCls:'icon-lock',iconWidth:38" placeholder="再次输入新密码" style="width:100%;height:40px">
            <!--
            <input type="password" name="confirmPassword" class="easyui-textbox" data-options="required:true,validType:['equalTo[\'#newPassword\']','password'],prompt:'再次输入新密码',iconCls:'icon-lock',iconWidth:38" placeholder="再次输入新密码" style="width:100%;height:40px">
            -->
        </div>

        <div>
            <a href="javascript:resetSubmit()" class="easyui-linkbutton" iconCls="icon-ok" style="width:100%;height:32px">确认修改</a>
        </div>
    </form>
    </c:if>
</div>
</div>

<div id="resetSuccess" style="display:none;width:400px;">
<div class="easyui-panel" title="重置密码" style="width:400px;padding:30px 60px;">
    <a href="/login" class="easyui-linkbutton" iconCls="icon-ok" style="width:260px;height:32px">密码已修改成功，点击登录</a>
</div>
</div>
<script type="text/javascript">
    function resetSubmit(){
        $("#updatePasswordForm").submit();
    }
    function fixTextPlaceholder() {
        $(".easyui-textbox").each(function (i) {
            var span = $(this).siblings("span")[0];
            var targetInput = $(span).find("input:first");
            if (targetInput) {
                $(targetInput).attr("placeholder", $(this).attr("placeholder"));
            }
        });
    }

    $.extend($.fn.validatebox.defaults.rules, {
        /*必须和某个字段相等*/
        equalTo: {
            validator: function (value, param) {
                return $(param[0]).val() == value;
            }, message: '两次密码输入不一致'
        },
        password: {
            validator: function (value, param) {
                var reg = /^\w{6,20}$/;
                return reg.test(value);
            }, message: '密码格式不正确'
        }
    });
    $(function() {
        window.onload = fixTextPlaceholder();

        $('#updatePasswordForm').form({
            url : '${ctx}/sysUser/resetPassword',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success : function(result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    $("#resetPass").remove();
                    $("#resetSuccess").css("display","block");
                    parent.$.messager.alert('提示',result.msg,'info');
                }else{
                    parent.$.messager.alert('提示',result.msg,'error');
                }
            }
        });
    });
</script>
</body>
</html>