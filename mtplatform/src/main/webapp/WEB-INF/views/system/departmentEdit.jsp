<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>部门管理</title>
</head>
<body>
<div style="padding: 3px;">
    <form id="departmentEditForm" method="post">
        <table class="grid">
        	<tr>
            	<td>资源名称</td>
                <td>
                	<input name="id" type="hidden"  value="${department.id}">
                	<input name="departmentName" class="easyui-textbox" style="width: 240px; height: 29px;" type="text" value="${department.departmentName}" placeholder="请输入部门名称" class="easyui-validatebox" data-options="required:true" >
                </td>
            </tr>
            <tr>
                <td>编号</td>
                <td>
                	<input name="departmentCode" style="width: 240px; height: 29px;" type="text" class="easyui-textbox" data-options="editable:false" value="${department.departmentCode}" />
                </td>
            </tr>
            <tr>
                <td>排序</td>
                <td><input name="sequence" style="width:240px;height:29px;"  class="easyui-numberspinner" value="${department.sequence}" style="widtd: 140px; height: 29px;" required="required" data-options="editable:false"></td>
            </tr>
            <tr>
            	<td>菜单图标</td>
                <td ><input name="departmentIcon" class="easyui-textbox" style="width: 240px; height: 29px;" value="${department.departmentIcon}"/></td>
            </tr>
            <tr>
                <td>上级资源</td>
                <td>
                	<select id="departmentEditPid" name="parentId" style="width: 200px; height: 29px;"></select>
                	<a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#departmentEditPid').combotree('clear');" >清空</a>
                </td>
            </tr>
            <tr>
                <td>描述</td>
            	<td><textarea name="description" style="width: 240px; height: 49px;">${department.description}</textarea></td>
            </tr>
        </table>
    </form>
</div>
<script type="text/javascript">
    $(function() {
        $('#departmentEditPid').combotree({
            url : '${ctx}/sysDepartment/tree?flag=false',
            parentField : 'parentId',
            lines : true,
            panelHeight : 'auto',
            value :'${department.parentId}'
        });
        
        $('#departmentEditForm').form({
            url : '${ctx}/sysDepartment/edit',
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
                    parent.$.modalDialog.openner_treeGrid.treegrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_treeGrid这个对象，是因为department.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');
                }
            }
        });
    });
</script>
</body>
</html>