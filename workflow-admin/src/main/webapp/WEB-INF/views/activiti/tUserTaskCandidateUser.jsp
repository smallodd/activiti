<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
	<div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff;" id="tb1">
		<form id="candidateUserSearchForm">
			<table>
				<tr>
					<th>姓名/工号:</th>
					<td><input name="userName" placeholder="姓名/工号"/></td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="userSearchFun();">查询</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="userCleanFun();">清空</a>
					</td>
				</tr>
			</table>
		</form>
	</div>

	<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
		<table id="taskCandidateUserGrid" class="easyui-datagrid" data-options="fit:true"style="width:400px;"></table>
	</div>
	<script>
        var dataGrid;
        $(function() {
            dataGrid = $('#taskCandidateUserGrid').datagrid({
                url : '${ctx}/sysUser/selectDataGrid',
                pagePosition : 'bottom',
                border:true,
                pagination : true,
                singleSelect : false,
                fitColumns : true,
                idField : 'id',
                sortName : 'id',
                sortOrder : 'asc',
                columns : [ [ {
                    width : '60px',
                    title : '主键',
                    field : 'id',
                    hidden:true
                },{
                    width : '245px',
                    title : '所属部门',
                    field : 'departmentName',
                    sortable : true
                }, {
                    width : '110px',
                    title : '姓名',
                    field : 'userName',
                    sortable : true
                }, {
                    width : '110px',
                    title : '工号',
                    field : 'loginName',
                    sortable : true
                } ] ],
                onLoadSuccess:function(data){
                    var json= JSON.parse($("#taskJson").val());
                    console.info(json);
                    var taskKey = $("#taskKey").val();
                    $.each(json,function(i,o){
                        if(o.key == taskKey){
                            taskKey = o;
                            return false;
                        }
                    })
					var checkedUser = taskKey.value.split(",");
                    $.each(data.rows,function(i,obj){
                        if($.inArray(obj.id, checkedUser) >= 0){
                            $('#taskCandidateUserGrid').datagrid('selectRow',i);
						}
					})
                },
                toolbar : '#tb1'
            });
        });

		/**
		 * 清除
		 */
		function userCleanFun() {
			$('#candidateUserSearchForm input').val('');
			$("#taskCandidateUserGrid").datagrid('load', {});
		}

		/**
		 * 搜索
		 */
		function userSearchFun() {
			$("#taskCandidateUserGrid").datagrid('load', $.serializeObject($('#candidateUserSearchForm')));
		}
	</script>
</body>
</html>