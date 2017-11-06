<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>新增</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" >
    <div data-options="region:'center',border:false" style="overflow: hidden;padding: 3px;" >
        <form id="tVacationAddForm" method="post">
            <table class="grid">
            	<tr>
            		<td>请假类型</td>
            		<td>
	            		<select name="vacationType" class="easyui-combobox" data-options="width:240,height:29,editable:false,panelHeight:'auto'">
	                            <option value=1>事假</option>
	                            <option value=2>病假</option>
	                    </select>
                    </td>
            	</tr>
                <tr>
                    <td>开始日期</td>
                    <td><input name="beginDate" class="easyui-datetimebox" placeholder="点击选择时间" style="width: 240px; height: 29px;"/></td>
                </tr>
                <tr>
                    <td>结束日期</td>
                    <td><input name="endDate" class="easyui-datetimebox" placeholder="点击选择时间" style="width: 240px; height: 29px;"/></td>
                </tr>
                <tr>
                    <td>请假天数</td>
                    <td><input name="workDays" class="easyui-numberspinner" style="width: 240px; height: 29px;" required="required" data-options="min:0,max:1000,editable:true" value="0"></td>
                </tr>
                <tr>
                	<td>请假原因</td>
                	<td colspan="3"><textarea name="vacationReason" style="width: 240px; height: 49px;"></textarea></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#tVacationAddForm').form({
            url : '${ctx}/tVacation/add',
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
    });
</script>
</body>
</html>