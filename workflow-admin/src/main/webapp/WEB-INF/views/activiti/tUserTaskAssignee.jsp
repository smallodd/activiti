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
				<th>名称:</th>
				<td><input class="easyui-searchbox" name="userName" placeholder="搜索条件"/></td>
				<td>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="userSearchFun();">查询</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="userCleanFun();">清空</a>
				</td>
			</tr>
		</table>
	</form>
</div>
<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
    <table id="userGrid" class="easyui-datagrid"
		    data-options="fit:true,border:true,pagination : true,
		    fitColumns:true,singleSelect : true,
		    columns : [[{width : '150',title : '所属部门',field : 'departmentName'},
            {width : '150', title : '姓名',field : 'userName',sortable : true},
            {width : '200',title : '工号',field : 'loginName',sortable : true}]],url:'${ctx}/sysUser/selectDataGrid',toolbar:'#tb'"></table>
            
</div>
<script>
    /**
     * 清除
     */
    function userCleanFun() {
        $('#userSearchForm input').val('');
        $("#userGrid").datagrid('load', {});
    }

    /**
     * 搜索
     */
    function userSearchFun() {
        $("#userGrid").datagrid('load', $.serializeObject($('#userSearchForm')));
    }
</script>
</body>
</html>