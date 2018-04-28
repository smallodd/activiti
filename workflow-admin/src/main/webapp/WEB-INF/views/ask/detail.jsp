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
            <input type="hidden" name="currentTaskDefKey" id="currentTaskDefKey" value="${askComment.currentTaskDefKey}"/>
            <input type="hidden" name="processInstanceId" id="processInstanceId" value="${askComment.processInstanceId}"/>
            <textarea  style="margin-top: 20px;width: 325px; height: 200px;" placeholder="问询内容" >${askComment.askComment}</textarea>
            <textarea  style="margin-top: 20px;width: 325px; height: 200px;" placeholder="回复内容" name="answerComment">${askComment.answerComment}</textarea>
		</form>
    </div>
</div>

<script type="text/javascript">
    $(function() {
        $('#taskJumpForm').form({
            url : '${ctx}/ask/askComment',
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