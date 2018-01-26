<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
<meta http-equiv="Pragma" content="no-cache"> 
<meta http-equiv="Cache-Control" content="no-cache"> 
<meta http-equiv="Expires" content="0">
<meta name="renderer" content="webkit">
<title>登录页</title>
<link rel="stylesheet" type="text/css" href="/resource/system/login.css"/>
<script type="text/javascript" src="/resource/jeasyui/jquery.min.js"></script>
<script type="text/javascript" src="/resource/jeasyui/jquery.easyui.min.js"></script>
</head>
<body>
<div class="login" onkeydown="keylogin()">
    <div class="message">恒天财富-工作流管理平台</div>
    <div id="darkbannerwrap"></div>
	<form name="loginform" id="loginform" method="post">
		<input name="loginName" placeholder="请输入用户名" required type="text">
		<hr class="hr15">
		<input name="loginPwd" placeholder="请输入密码" required type="password">
		<hr class="hr15">
		<input name="btn" value="登录" style="width:100%;" type="button" onclick="login()">
		<hr class="hr20">
		<div style="margin-top:18px;text-align: center"><span id="msg" style="color:red;"></span></div>
	</form>
	<a style="margin-left:280px;" href="/sysUser/passwordForget">忘记密码</a>
</div>
<script type="text/javascript">
	//点击登录
	function login(){
		$("#loginform").form("submit",{
			url:"${pageContext.request.contextPath}/login",
			success:function(data){
				result=eval("("+data+")");
				if(result.success){
				    sessionStorage.removeItem("isLogin");
					window.location.href="${pageContext.request.contextPath}/";
				}else{
					$("#msg").text(result.msg);
				}
			}
		});
	}
	//回车登录
	function keylogin(){
		if (event.keyCode == 13){
		    event.returnValue=false;
			event.cancel = true;
			loginform.btn.click();
		}
	}
</script>
</body>
</html>