<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>当前审批人</title>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
		<form id="processStartForm" method="post">
			<input type="hidden" id="jsonVariables" name="jsonVariables"/>
			<input type="hidden" id="processDefinitionId" name="processDefinitionId" value="${processDefinitionId}"/>
			<input type="hidden" id="processParam" name="processParam"/>
			<div class="var_" style="margin: 5px;display: none" id="varTemplate"><input style="height:28px;" placeholder="属性名称"/><span style="margin: 20px">:</span><input style="height:28px;" placeholder="属性值"/></div>
			<c:forEach var="expressionName" items="${expressionNameSet }">
				<div class="var_" style="margin: 5px;"><input style="height:28px" value="${expressionName}" readonly/><span style="margin: 20px">:</span><input style="height:28px" placeholder="属性值" required=""/></div>
			</c:forEach>
			<c:if test="${expressionNameSet == null || expressionNameSet.size() == 0}">
				<div class="var_" style="margin: 5px;"><input style="height:28px" placeholder="属性名称"/><span style="margin: 20px">:</span><input style="height:28px" placeholder="属性值"/></div>
			</c:if>
			<div style="margin: 5px;" id="addTemplate"><a href="javascript:addVariable();" class="easyui-linkbutton" iconCls="icon-add" style="width:94%;height:30px">添加属性</a></div>
		</form>
	</div>
	<script>
        $(function () {
            $('#processStartForm').form({
                url: '${ctx}/workflow/action/process/start',
                onSubmit: function () {
                    progressLoad();
                    var isValid = $(this).form('validate');
                    if (!isValid) {
                        progressClose();
                    }
                    setVariables();
                    return isValid;
				},
                success: function (result) {
                    result = $.parseJSON(result);
                    progressClose();
                    if (result.success) {
                        $.messager.alert('提示', result.msg, 'info');
                        parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                        parent.$.modalDialog.handler.dialog('close');
                    } else {
                        $.messager.alert('错误', result.msg, 'error');
                    }
                }
            });

        });

        /**
         * 添加属性列
         */
        function addVariable(){
            $("#addTemplate").before($("#varTemplate").clone().css("display","block"))
        }

        /**
         * 设置属性值
         */
        function setVariables(){
            progressClose();
            var vars = $(".var_");
            if(vars == undefined || vars == null){
                return;
            }
            var varObj = {};
            $.each(vars, function(i,var_){
                var vals = $(var_).find('input');
                var v0 = $(vals[0]).val();
                var v1 = $(vals[1]).val()
                if(v0 != '' && v1 != ''){
                    varObj[v0] = v1;
                }
            })
            $("#jsonVariables").val(JSON.stringify(varObj));
        }
	</script>
</body>
</html>