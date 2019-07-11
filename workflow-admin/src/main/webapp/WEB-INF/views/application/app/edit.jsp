<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>添加资源</title>
</head>
<body>
<div style="padding: 3px;">
    <form id="appEditForm" method="post">
        <table class="grid">
        	<tr>
        		<td>名称</td>
                <td>
                    <input name="id" type="hidden"  value="${app.id}" >
                    <input name="name" type="text" class="easyui-textbox" style="width:200;height:29" data-options="required:true" value="${app.name}">
                </td>
        	</tr>
            <tr>
                <td>KEY</td>
                <td><input name="key" type="text" class="easyui-textbox" style="width:200;height:29" data-options="required:true" value="${app.key}"></td>
            </tr>
            <tr>
                <td>备注</td>
                <td><textarea name="description" style="width:300px;height:100px;">${app.description}</textarea></td>
            </tr>
        </table>
    </form>
</div>
<script type="text/javascript">
    $(function() {
        $('#appEditForm').form({
            url : '${ctx}/app/edit',
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
                    parent.$.modalDialog.openner_datagrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
    });
</script>
</body>
</html>