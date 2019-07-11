<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>当前审批人</title>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
		<table data-options="region:'center'" id="taskAssignee" style="width:210px;height:395px;"></table>
	</div>
	<script>
        var dataGrid;
        $(function() {
            var taskId = "${taskId}";
            dataGrid = $('#taskAssignee').datagrid({
                url : '${ctx}/workflow/data/task/assignee/'+taskId,
                border:true,
                singleSelect : false,
                fitColumns : true,
                striped: true,
                idField : 'userCode',
                sortOrder : 'asc',
                columns : [[{
                    width : '100px',
                    title : '工号',
                    field : 'userCode'
                }, {
                    width : '100px',
                    title : '姓名',
                    field : 'userName'
                }]]
            });
        });
	</script>
</body>
</html>