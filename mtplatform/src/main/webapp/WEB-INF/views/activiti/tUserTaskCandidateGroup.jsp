<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>部门</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
    <table id="taskCandidateGroupGrid" class="easyui-datagrid" 
		    data-options="idField : 'id',treeField : 'departmentName',
            parentField : 'parentId',fit : true,border : false,
            fitColumns:true,singleSelect : false,
		    columns : [[{
                field : 'departmentCode',
                title : '部门编号',
                width : 100
            },{
                field : 'departmentName',
                title : '部门名称',
                width : 180
            }]],url:'${ctx}/sysDepartment/treeGrid'"></table>
            
</div>
</body>
</html>