<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>角色编辑</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="roleEditForm" method="post">
            <table class="grid">
                <tr>
                    <td>角色名称</td>
                    <td><input name="id" type="hidden"  value="${role.id}">
                    <input name="roleName" type="text" class="easyui-textbox" style="width: 240px; height: 29px;" class="easyui-textbox" data-options="required:true" value="${role.roleName}"></td>
                </tr>
                <tr>
                    <td>角色编码</td>
                    <td><input name="roleCode" class="easyui-textbox" style="width: 240px; height: 29px;" value="${role.roleCode}" data-options="editable:false"/></td>
                </tr>
                <!-- <tr>
                    <td>状态</td>
                    <td >
                        <select id="roleEditStatus" name="status" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                            <option value="0">正常</option>
                            <option value="1">停用</option>
                        </select>
                    </td>
                </tr> -->
                <tr>
                    <td>备注</td>
                    <td><textarea name="description" style="width: 240px; height: 49px;">${role.description}</textarea></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#roleEditForm').form({
            url : '${ctx}/sysRole/edit',
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
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
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