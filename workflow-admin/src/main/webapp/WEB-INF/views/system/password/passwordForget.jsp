

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>忘记密码</title>
    <style>
        img.double-border {
            border: 3px solid #ddd;
            padding: 3px;
            background: #fff;
        }
    </style>
</head>
<body style="text-align:center">
<div style="margin:20px 0;"></div>
<div id="validateMail">
<div class="easyui-panel" title="忘记密码" style="width:400px;padding:30px 60px;">
    <form id="updatePasswordForm" method="post">
        <div style="margin-bottom:12px">
            <input type="text" id="loginName" name="loginName" class="easyui-textbox" data-options="required:true,missingMessage:'登录名称不能为空',iconCls:'icon-man',iconWidth:38" placeholder="请输入登录名称/工号" style="width:100%;height:40px;">
        </div>
        <div style="margin-bottom:12px;">
            <img id="captcha" class="double-border" src="/resource/images/default_code.png" onclick="createCaptcha()" style="vertical-align: middle;height:30px;"/>
            <input type="text" name="code" class="easyui-textbox" data-options="required:true,missingMessage:'验证码不能为空'" placeholder="请输入验证码" style="width:180px;height:40px;float:left;vertical-align: middle;">
        </div>
        <div>
            <a href="javascript:formSubmit();" class="easyui-linkbutton" style="width:100%;height:32px">发送</a>
        </div>
    </form>
    <span style="width:100%;height:32px;margin-bottom:8px;color:steelblue;font-size:5px;">提示：请填写正确的信息，点击邮箱中地址修改</span>
</div>
</div>

<div id="validateSuccess" style="display:none">
<div class="easyui-panel" title="提示" style="width:400px;padding:30px 60px;">
    <a href="#" class="easyui-linkbutton" style="width:260px;height:60px">
        您的密码修改请求已发送到您邮箱，请进入邮箱修改,如无其它需求请关闭该页面.
    </a>
</div>
</div>
<script type="text/javascript">
    function formSubmit() {
        $('#updatePasswordForm').submit();
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

    $(function() {
        window.onload = fixTextPlaceholder();

        $('#updatePasswordForm').form({
            url : '${ctx}/sysUser/password/mailValidate',
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
                    $("#validateMail").remove();
                    $("#validateSuccess").show();
                    parent.$.messager.alert('提示',result.msg,'info');
                }else{
                    parent.$.messager.alert('提示',result.msg,'error');
                }
            }
        });
    });

    function createCaptcha(){
        if($("#loginName").val() != ""){
            $("#captcha").attr("src",this.src='/createCaptcha?loginName='+$('#loginName').val()+'&ran='+Math.random());
        }else{
            parent.$.messager.alert('提示',"请填写登录名称/工号",'info');
        }
    }
</script>
</body>
</html>