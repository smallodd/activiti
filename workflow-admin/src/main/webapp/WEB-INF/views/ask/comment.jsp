<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>意见征询</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" style="overflow:auto;padding-top:30px;text-align:center">
        <form id="taskJumpForm" method="post">
            <input type="hidden" name="currentTaskDefKey" id="currentTaskDefKey" value="${currentTaskDefKey}"/>
            <input type="hidden" name="processInstanceId" id="processInstanceId" value="${processInstanceId}"/>

            <div>
                <label>意见征询节点</label>
                <select name="targetTaskDefKey" id="targetTaskDefKey" class="easyui-combobox" data-options="width:350,height:29,panelHeight:'auto'">
                    <c:forEach items="${tasks}" var="task">
                        <option value="${task.taskDefinitionKey}">${task.taskName}</option>
                    </c:forEach>
                </select>

            </div>
            <div>
                <label>意见征询内容</label>
                <td><textarea style="margin-top: 20px;width: 350px; height: 200px;white-space: normal" placeholder="意见征询内容" name="commentResult"></textarea></td>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        $('#taskJumpForm').form({
            url: '${ctx}/rest/flow/operate/askTask',
            success: function (result) {
                result = $.parseJSON(result);
                progressClose();
                if (result.success) {
                    $.messager.alert('提示', result.msg, 'info');
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