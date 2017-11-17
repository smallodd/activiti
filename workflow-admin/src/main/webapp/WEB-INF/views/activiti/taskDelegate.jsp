<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
<div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff;" id="tb">
	<form id="userSearchForm">
		<table>
			<tr>
				<th>姓名/工号:</th>
				<td><input class="easyui-searchbox" name="userName" placeholder="姓名/工号"/></td>
				<td>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="userSearchFun();">查询</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="userCleanFun();">清空</a>
				</td>
			</tr>
		</table>
	</form>
</div>
<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
    <table id="delegateTaskGrid" class="easyui-datagrid" 
		    data-options="fit:true,border:false,pagination : true,
		    fitColumns:true,singleSelect : true,
		    columns : [[{width : '150',title : '所属部门',field : 'departmentName'},
            {width : '150', title : '姓名',field : 'userName',sortable : true},
            {width : '200',title : '工号',field : 'loginName',sortable : true}]],url:'${ctx}/sysUser/selectDataGrid',toolbar:'#tb'"></table>
            
</div>

<form id="taskDelegateForm" method="post">
	<input type="hidden" name="taskId" id="taskId"/>
	<input type="hidden" name="userId" id="userId"/>
</form>

<form id="taskTransferForm" method="post">
	<input type="hidden" name="taskId" id="taskId_"/>
	<input type="hidden" name="userId" id="userId_"/>
</form>

<script>
    /**
     * 清除
     */
    function userCleanFun() {
        $('#userSearchForm input').val('');
        $("#delegateTaskGrid").datagrid('load', {});
    }

    /**
     * 搜索
     */
    function userSearchFun() {
        $("#delegateTaskGrid").datagrid('load', $.serializeObject($('#userSearchForm')));
    }

    /**
	 * 委派
     */
    $(function() {
        $('#taskDelegateForm').form({
            url : '${ctx}/activiti/delegateTask',
            onSubmit : function() {
                progressLoad();
                var rows = $("#delegateTaskGrid").datagrid("getSelections");
                $("#userId").val(rows[0].id);
                return true;
            },

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

    /**
     * 转办
     */
    $(function() {
        $('#taskTransferForm').form({
            url : '${ctx}/activiti/transferTask',
            onSubmit : function() {
                progressLoad();
                var rows = $("#delegateTaskGrid").datagrid("getSelections");
                $("#userId_").val(rows[0].id);
                return true;
            },

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