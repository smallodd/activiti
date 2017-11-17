<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
	<table id="taskAssigneeGrid" class="easyui-datagrid"
		   data-options="fit:true,border:false,pagination : true,
		    fitColumns:true,singleSelect : true,
		    columns : [[{width : '150',title : '所属部门',field : 'departmentName'},
            {width : '150', title : '姓名',field : 'userName',sortable : true},
             {width : '200',title : '工号',field : 'loginName',sortable : true}]],url:'${ctx}/sysUser/selectDataGrid'"></table>

</div>
</body>
</html>