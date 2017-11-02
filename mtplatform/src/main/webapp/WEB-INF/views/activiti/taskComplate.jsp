<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>办理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" >
    <div data-options="region:'center',border:false" style="overflow: auto;padding: 3px;" >
        <form id="complateTaskForm" method="post">
            <table class="grid">
                <tr>
            		<td>请假类型</td>
            		<td>
            			<input name="taskId" type="hidden"  value="${task.id}">
            			<input name="vacationId" type="hidden"  value="${vacation.id}">
	            		<select id="tVacationEditVacationType" class="easyui-combobox" data-options="width:240,height:29,editable:false,panelHeight:'auto'" readonly="readonly">
	                            <option value=1>事假</option>
	                            <option value=2>病假</option>
	                    </select>
                    </td>
            	</tr>
                <tr>
                    <td>开始日期</td>
                    <td><input class="easyui-datetimebox" placeholder="点击选择时间" style="width: 240px; height: 29px;" value="${vacation.beginDate}" readonly="readonly"/></td>
                </tr>
                <tr>
                    <td>结束日期</td>
                    <td><input class="easyui-datetimebox" placeholder="点击选择时间" style="width: 240px; height: 29px;" value="${vacation.endDate}" readonly="readonly"/></td>
                </tr>
                <tr>
                    <td>请假天数</td>
                    <td><input class="easyui-numberspinner" style="width: 240px; height: 29px;" required="required" data-options="min:0,max:1000,editable:false" value="${vacation.workDays}" readonly="readonly"></td>
                </tr>
                <tr>
                	<td>请假原因</td>
                	<td><textarea style="width: 240px; height: 49px;" readonly="readonly">${vacation.vacationReason}</textarea></td>
                </tr>
                <tr><td>意见列表:</td><td><c:if test="${empty comments}">暂无意见 ！</c:if></td></tr>
                <c:forEach var="comment" items="${comments}">
					<tr>
						<td>${comment.commentUser}</td><td>[ ${comment.commentTime} ]  ${comment.commentContent}</td>
					</tr>
				</c:forEach>
				<tr>
                	<td>我的意见</td>
                	<td colspan="3"><textarea name="commentContent" style="width: 240px; height: 49px;" required="required"></textarea></td>
                </tr>
                <!-- <tr><td>是否同意</td>
                    <td><select id="commentResult" name="commentResult" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                            <option value="1">同意</option>
                            <option value="2">不同意</option>
                    </select></td>
                 </tr> -->
                 <tr>
                 	<td>是否同意</td>
                 	<td>
                 		<input type="radio" name="commentResult" style="cursor:pointer;" value=2 checked="checked">同意</input>
                 		<input type="radio" name="commentResult" style="cursor:pointer;" value=3 >不同意</input>
                 	</td>
                 </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#complateTaskForm').form({
            url : '${ctx}/activiti/complateTask',
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
                    //之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
        $("#tVacationEditVacationType").val('${vacation.vacationType}'); 
    });
</script>
</body>
</html>