<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>调整申请</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" >
    <div data-options="region:'center',border:false" style="overflow: hidden;padding: 3px;" >
        <form id="complateTaskForm" method="post">
            <table class="grid">
            	<tr>
            		<td>请假类型</td>
            		<td>
            			<input name="id" type="hidden" value="${vacation.id}"/>
            			<input name="taskId" type="hidden"  value="${task.id}">
	            		<select id="vacationTypeEditId" name="vacationType" class="easyui-combobox" data-options="width:240,height:29,editable:false,panelHeight:'auto'">
	                            <option value=1>事假</option>
	                            <option value=2>病假</option>
	                    </select>
                    </td>
            	</tr>
                <tr>
                    <td>开始日期</td>
                    <td><input name="beginDate" class="easyui-datetimebox" placeholder="点击选择时间" style="width: 240px; height: 29px;" value="${vacation.beginDate}"/></td>
                </tr>
                <tr>
                    <td>结束日期</td>
                    <td><input name="endDate" class="easyui-datetimebox" placeholder="点击选择时间" style="width: 240px; height: 29px;" value="${vacation.endDate}"/></td>
                </tr>
                <tr>
                    <td>请假天数</td>
                    <td><input name="workDays" class="easyui-numberspinner" style="width: 240px; height: 29px;" required="required" data-options="min:0,max:1000,editable:true" value="${vacation.workDays}"></td>
                </tr>
                <tr>
                	<td>请假原因</td>
                	<td colspan="3"><textarea name="vacationReason" style="width: 240px; height: 49px;">${vacation.vacationReason}</textarea></td>
                </tr>
                <tr>
                 	<td>是否继续申请</td>
                 	<td>
                 		<input type="radio" name="commentResult" style="cursor:pointer;" value="continue" checked="checked">重新申请</input>
                 		<input type="radio" name="commentResult" style="cursor:pointer;" value="stop" >结束流程</input>
                 	</td>
                 </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#complateTaskForm').form({
            url : '${ctx}/tVacation/modifyTask',
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
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
        $("#vacationTypeEditId").val('${vacation.vacationType}');
    });
</script>
</body>
</html>