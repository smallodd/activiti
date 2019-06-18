<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>任务跳转</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="overflow:auto;padding-top:30px;text-align:center">
		<form id="taskJumpForm" method="post">
			<input type="hidden" name="taskId" id="taskId" value="${taskId}"/>
			 <select name="taskDefinitionKey" id="taskDefinitionKey" class="easyui-combobox" data-options="width:240,height:29,panelHeight:'auto'">
				 <c:forEach items="${tasks}" var="task">
					<option value="${task.taskDefinitionKey}">${task.taskName}</option>
				 </c:forEach>
			 </select>
		</form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#taskJumpForm').form({
            url : '${ctx}/activiti/jumpTask',
            success : function(result) {
                result = $.parseJSON(result);
                progressClose();
                if (result.success) {
                    $.messager.alert('提示', result.msg,'info');
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    $.messager.alert('错误', result.msg, 'error');
                }
            }
        });

    });
</script>
</body>
</html>