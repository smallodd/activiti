<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
	<div data-options="region:'north',border:false" style="height: 0px; overflow: hidden;background-color: #fff;" id="tb_user">
		<form id="taskClaimForm" method="post">
			<input type="hidden" name="taskId" id="taskId" value="${taskId}"/>
			<input type="hidden" name="claimType" id="claimType" value="${claimType}"/>
			<input type="hidden" name="system" id="system" value="${system}"/>
			<input type="text" name="claimUser" id="claimUser"/>
		</form>
	</div>

	<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
		<table data-options="region:'center'" id="taskCandidateUserClaimGrid" style="width:470px;height:360px;"></table>
	</div>
	<script>
        var taskId = $("#taskId").val();
        var claimType = $("#claimType").val();
        var system = $("#system").val();
        var dataGrid;
        $(function() {
            dataGrid = $('#taskCandidateUserClaimGrid').datagrid({
                url : '${ctx}/workflow/data/user/claim?taskId='+taskId+"&claimType="+claimType+"&system="+system,
                pagePosition : 'bottom',
                border:true,
                pagination : true,
                singleSelect : false,
                fitColumns : true,
                striped: true,
                idField : 'code',
                sortName : 'code',
                sortOrder : 'asc',
                columns : [ [ {
                    field : 'jobCode',
                    hidden : true
                }, {
                    width : '245px',
                    title : '所属部门',
                    field : 'deptName',
                    sortable : true,
                    formatter : function(value){
                        if(value){
                            var array = value.split("\\");
							return array[array.length-1];
						}
                        return "";
					}
                }, {
                    width : '110px',
                    title : '工号',
                    field : 'code',
                    sortable : true
                }, {
                    width : '110px',
                    title : '姓名',
                    field : 'name',
                    sortable : true
                }] ],
                onSelect: function (rowIndex, rowData) {
                    var claimUser = $("#claimUser").val();
                    if(claimUser != null && claimUser != ""){
                        $("#claimUser").val(claimUser+","+rowData.jobCode);
					}else{
                        $("#claimUser").val(rowData.jobCode);
					}
                },
                onUnselect: function (rowIndex, rowData) {
                    var claimUser = $("#claimUser").val();
                    if(claimUser != null && claimUser != ""){
                        var array = claimUser.split(",");
                        array.splice($.inArray(rowData.jobCode, array), 1)
                        $("#claimUser").val(array.join(","));
                    }
                },
                onLoadSuccess:function(data){

                }
            });
        });

        var formAction = "${ctx}/workflow/action/task/claim";
        var claimType = $("#claimType").val();
        if(claimType == 2){
            formAction = "${ctx}/workflow/action/task/unclaim";
		}

        $('#taskClaimForm').form({
            url: formAction,
            onSubmit: function () {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success: function (result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    //之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
	</script>
</body>
</html>