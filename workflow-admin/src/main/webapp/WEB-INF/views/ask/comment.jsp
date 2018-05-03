<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>问询</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="overflow:auto;padding-top:30px;text-align:center">
        <form id="taskJumpForm" method="post">
            <span style="padding-right: 30px">问询节点</span>
            <input type="hidden" name="askId" id="askId" value="${askId}"/>
			 <select name="targetTaskDefKey" id="targetTaskDefKey" class="easyui-combobox" data-options="width:240,height:29,panelHeight:'auto'">
				 <c:forEach items="${tasks}" var="task">
					<option value="${task.taskDefinitionKey}">${task.name}</option>
				 </c:forEach>
			 </select>
            <textarea  style="margin-top: 20px;width: 325px; height: 200px;white-space: normal" placeholder="问询内容" name="commentResult"></textarea>
		</form>
    </div>
</div>

<script type="text/javascript">
    $(function() {
        $('#taskJumpForm').form({
            url : '${ctx}/ask/askTask',
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