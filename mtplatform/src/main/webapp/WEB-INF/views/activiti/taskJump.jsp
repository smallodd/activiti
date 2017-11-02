<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>任务跳转</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="overflow:auto;padding-top:30px;text-align:center">
    	 <select id="jumpTaskKey" class="easyui-combobox" data-options="width:240,height:29,panelHeight:'auto'">
    	 	<c:forEach items="${tasks}" var="task">
    	 		<option value="${task.taskDefKey}">${task.taskName}</option>
    	 	</c:forEach>
    	 </select>
    </div>
</div>
</body>
</html>