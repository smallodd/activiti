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
			<input type="hidden" name="taskId" id="taskId" value="${task.id}">
			<input type="hidden" name="userId" id="userId">
            <table class="grid">
                <%--<tr>--%>
                	<%--<td>任务说明</td>--%>
                	<%--<td><textarea style="width: 240px; height: 49px;" readonly="readonly">${task.description}</textarea></td>--%>
                <%--</tr>--%>
				<tr>
					<td>审批人</td>
					<td><select id="taskUser" style="width:100px;"></select></td>
				</tr>
                <tr><td>意见列表</td><td><c:if test="${empty comments}">暂无意见 ！</c:if></td></tr>
                <c:forEach var="comment" items="${comments}">
					<tr>
						<td>${comment.commentUser}</td><td>[ ${comment.commentTime} ]  ${comment.commentContent}</td>
					</tr>
				</c:forEach>
				<tr>
                	<td>我的意见</td>
                	<td colspan="3"><textarea name="commentContent" style="width: 240px; height: 49px;" required="required"></textarea></td>
                </tr>
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
    var taskId = $("#taskId").val();
    if(taskId != undefined && taskId != ""){
        $.ajax({
            type: 'POST',
            dataType : 'json',
            url: '${ctx}/activiti/getTaskUserWithEnd',
            data: {"taskId":taskId},
            success: function(json){
                if(json.success == false){
                    return;
				}
                var option = "";
                $.each(json,function(i,obj){
                    option = option + "<option value='"+obj.id+"'>"+obj.userName+"</option>";
                })
                $("#taskUser").html(option);
                $("#taskUser").combobox({});
            }
        });
    }

    $(function() {
        $('#complateTaskForm').form({
            url : '${ctx}/activiti/completeTask',
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