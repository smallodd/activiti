<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>转办-选择受理人</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="overflow: auto;padding: 3px;">
		<div class="easyui-layout" style="width:730px;height:400px;">
			<div data-options="region:'east',split:true,collapsible:false" title="办理人列表" style="width:200px;">
				<ul id="assigneeTree" style="margin-top: 10px"></ul>
			</div>
			<div data-options="region:'center'" title="选择操作">
				<div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff;" id="tb">
					<form id="userSearchForm">
						<table>
							<tr>
								<th>姓名/工号:</th>
								<td><input name="userName" placeholder="姓名/工号" style="width:120px;"/></td>
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
						   data-options="fit:false,border:true,pagination : true,
							fitColumns:true,singleSelect : true,
							columns : [[{width : '250',title : '所属部门',field : 'deptName'},
							{width : '150', title : '姓名',field : 'code',sortable : true},
							{width : '150',title : '工号',field : 'name',sortable : true}]],url:'${ctx}/emp/user',toolbar:'#tb'"></table>

				</div>
			</div>
		</div>
	</div>
</div>

<form id="taskDelegateForm" method="post">
	<input type="hidden" name="taskId" id="taskId"/>
	<input type="hidden" name="userId" id="userId"/>
</form>

<form id="taskTransferForm" method="post">
	<input type="hidden" name="taskId" id="taskId_" value="${taskId}"/>
	<input type="hidden" name="userId" id="userId_"/>
	<input type="hidden" name="transferUserId" id="transferUserId"/>
</form>

<script>
	var taskId = parent.$("#taskId_").val();
	if(taskId != undefined && taskId != ""){
        $('#assigneeTree').tree({
            url: '${ctx}/workflow/data/task/transfer/tree/'+taskId,
			singleSelect: true
        });
    }

    /**
	 * 委派
     */
    $(function() {
        $('#taskDelegateForm').form({
            url : '${ctx}/emp/user',
            onSubmit : function() {
                progressLoad();
                var rows = $("#delegateTaskGrid").datagrid("getSelections");
                if(rows == ""){
                    progressClose();
                    return false;
				}
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
            url : '${ctx}/workflow/action/task/transfer',
            onSubmit : function() {
                progressLoad();
                var rows = $("#delegateTaskGrid").datagrid("getSelections");
                if(rows == ""){
                    progressClose();
                    $.messager.alert('提示', "请先选择转办人",'info');
                    return false;
                }

                $("#transferUserId").val(rows[0].code);
                var assignee = $('#assigneeTree').tree('getSelected');
                if(!assignee){
                    progressClose();
                    $.messager.alert('提示', "请先选择任务办理人",'info');
                    return false;
				}else{
                    var children = $('#assigneeTree').tree("getChildren",assignee.target);
                    if(children != null && children.length > 0){
                        progressClose();
                        $.messager.alert('提示', "请先选择任务办理人",'info');
                        return false;
					}
				}

                $("#userId_").val(assignee.id);
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
     * 搜索
     */
    function userSearchFun() {
        $("#delegateTaskGrid").datagrid('load', $.serializeObject($('#userSearchForm')));
    }

    /**
     * 清除
     */
    function userCleanFun() {
        $('#userSearchForm input').val('');
        $("#delegateTaskGrid").datagrid('load', {});
    }
</script>
</body>
</html>