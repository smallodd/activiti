<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>部门新增</title>
</head>
<body>
<div style="padding: 3px;">
    <form id="departmentAddForm" method="post">
        <table class="grid">
            <tr>
            	<td>部门名称</td>
                <td><input name="departmentName" type="text" class="easyui-textbox" style="width:240px;height:29px;" data-options="required:true" ></td>
            </tr>
            <tr>
                <td>排序</td>
                <td><input name="sequence" class="easyui-numberspinner" style="width:240px;height:29px;" required="required" data-options="min:0,max:1000,editable:false" value="0"></td>
            </tr>
            <tr>
            	<td>菜单图标</td>
                <td><input name="departmentIcon" class="easyui-textbox" style="width:240px;height:29px;" value="fi-folder"/></td>
            </tr>
            <tr>
                <td>上级部门</td>
                <td><select id="departmentAddPid" name="parentId" style="width: 200px; height: 29px;"></select>
                <a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#pid').combotree('clear');" >清空</a></td>
            </tr>
            <tr>
                <td>描述</td>
                <td><textarea name="description" style="width: 240px; height: 49px;"></textarea></td>
            </tr>
        </table>
    </form>
</div>
<script type="text/javascript">
    $(function() {
        $('#departmentAddPid').combotree({
            url : '${ctx}/sysDepartment/tree',
            parentField : 'parentId',
            lines : true,
            panelHeight : 'auto'
        });
        
        $('#departmentAddForm').form({
            url : '${ctx}/sysDepartment/add',
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