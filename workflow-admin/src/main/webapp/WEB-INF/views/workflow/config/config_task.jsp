<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>设定人员</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: auto;padding: 8px;">
        <form id="configAssigneeForm" method="post">
        	<input id="taskJson" name="taskJson" type="hidden" value="${taskJson}"/>
            <input id="taskJsonSelect" type="hidden" value="${taskJson}"/>
            <table class="grid">
            <tr>
                <td><strong>任务名称</strong></td>
                <td><strong>配置选项类型</strong></td>
                <td><strong>人员 | 部门 | 角色 | 表达式选项</strong></td>
                <td><strong>操作权限</strong></td>
                <td><strong>下一节点</strong></td>
                <td><strong>通过条件</strong></td>
                <td><strong>条件参数</strong></td>
            </tr>
            <c:forEach var="ut" items="${uTasks}">
				<tr>
					<td style="width: 100px;">[${ut.taskName}]</td>
					<td style="width: 150px;" data-key="${ut.taskDefKey}">
						<input id="taskId${ut.taskDefKey}" type="hidden" value="${ut.id}"/>
						<select id="taskType${ut.taskDefKey}" class="easyui-combobox selectConfigType" data-options="width:66,height:29,panelHeight:'auto'">
                            <c:forEach var="taskType" items="${taskType}">
                                <option value="${taskType.key}" <c:if test="${taskType.key == ut.taskType}">selected="selected"</c:if>>${taskType.value}</option>
                            </c:forEach>
                        </select>
                        <select id="assignType${ut.taskDefKey}" class="easyui-combobox selectConfigType" data-options="width:77,height:29,panelHeight:'auto'">
                            <c:forEach var="assignType" items="${assignType}">
                                <option value="${assignType.key}" <c:if test="${assignType.key == ut.assignType}">selected="selected"</c:if>>${assignType.value}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td><input id="taskUser${ut.taskDefKey}" placeholder="点击选择" data-options="required:true" style="width:200px;height:29px" onclick="configAssignee('${ut.taskDefKey}','${ut.procDefKey}')"/></td>
				    <td><input id="taskButton${ut.taskDefKey}" placeholder="点击选择" data-options="required:true" style="width:200px;height:29px" onclick="configButton('${ut.taskDefKey}')"/></td>
                    <td>
                        <select id="needSetNext${ut.taskDefKey}" class="easyui-combobox needSetNext" data-options="width:60,height:29,panelHeight:'auto'">
                            <option value="0" <c:if test="${ut.needSetNext == 0}">selected="selected"</c:if>>默认</option>
                            <option value="1" <c:if test="${ut.needSetNext == 1}">selected="selected"</c:if>>指定</option>
                        </select>
                    </td>
                    <td>
                        <select id="passType${ut.taskDefKey}" class="easyui-combobox passType" data-options="width:60,height:29,panelHeight:'auto'">
                            <option value="1" <c:if test="${ut.percentage != 0}">selected="selected"</c:if>>比例</option>
                            <option value="2" <c:if test="${ut.percentage == 0}">selected="selected"</c:if>>人数</option>
                        </select>
                    </td>
                    <td>
                        <div id="percentageDiv${ut.taskDefKey}"  <c:if test="${ut.percentage == 0}">style="display: none"</c:if>>
                            <input class="easyui-numberbox" id="percentage${ut.taskDefKey}" value="${ut.percentage}" placeholder="范围0-1" data-options="required:true,min:0.01,max:1,precision:2" style="width:66px;height:29px">
                        </div>
                        <div id="userCountNeedDiv${ut.taskDefKey}" <c:if test="${ut.percentage != 0}">style="display: none"</c:if>>
                            <input class="easyui-numberbox" id="userCountNeed${ut.taskDefKey}" value="${ut.userCountNeed}" placeholder="整数" data-options="required:true,precision:0" style="width:66px;height:29px;display:none">
                        </div>
                    </td>
                </tr>
			</c:forEach>
            </table>
        </form>
    </div>
</div>
<div id="configDepartmentDialog"></div>
<div id="configRoleDialog"></div>
<div id="configUserDialog"></div>
<div id="configExprDialog"></div>
<div id="configButtonDialog"></div>
<input type="hidden" id="taskKey"/>
<script type="text/javascript">
    $(function() {
    	//给各个任务的人员名称和ID赋值
    	var taskJson= $("#taskJson").val();
    	if(taskJson!==""){
    		var taskJsonArray= JSON.parse(taskJson);
        	for(var i=0;i<taskJsonArray.length;i++){
        		var taskDefKey = taskJsonArray[i].taskDefKey;
        		$("#taskUser"+taskDefKey).val(taskJsonArray[i].name);
        		if(taskJsonArray[i].name){
                    $("#taskUser"+taskDefKey).attr("title",taskJsonArray[i].name.replace(/,/g,"\n"));
                }

                $("#taskButton"+taskDefKey).val(taskJsonArray[i].buttonName);
                if(taskJsonArray[i].buttonKey){
                    $("#taskButton"+taskDefKey).attr("title",taskJsonArray[i].buttonName.replace(/,/g,"\n"));
                }
        	}
    	}

    	//Form表单提交
        $('#configAssigneeForm').form({
            url : '${ctx}/assignee/config',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }

            	var taskJsonVal = JSON.parse($("#taskJson").val());
            	for(var i=0;i<taskJsonVal.length;i++){
                    taskJsonVal[i].taskType = $("#taskType"+taskJsonVal[i].taskDefKey).val();
                    taskJsonVal[i].assignType = $("#assignType"+taskJsonVal[i].taskDefKey).val();

                    taskJsonVal[i].needSetNext = $("#needSetNext"+taskJsonVal[i].taskDefKey).val();

                    var passType = $("#passType"+taskJsonVal[i].taskDefKey).val();
                    if(passType == 1){
                        taskJsonVal[i].percentage = $("#percentage"+taskJsonVal[i].taskDefKey).val();
                    }else if(passType == 2){
                        taskJsonVal[i].percentage = 0;
                        taskJsonVal[i].userCountNeed = $("#userCountNeed"+taskJsonVal[i].taskDefKey).val();
                    }
            	}
            	var taskStr = JSON.stringify(taskJsonVal);
            	$("#taskJson").val(taskStr);
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

        $(".selectConfigType").combobox({
            onChange: function (n,o) {
                var taskDefKey = $(this).parent().attr("data-key");

                var taskType = $("#taskType"+taskDefKey).val();
                var assignType = $("#assignType"+taskDefKey).val();
                if(assignType == 4 && taskType != "assignee"){
                    $(this).combobox("select",o);
                    $.messager.alert('提示', "只有审批支持表达式",'info');
                    return;
                }

                $("#taskJsonSelect").val("");
                $("#taskUser"+taskDefKey).val("");
                $("#percentage"+taskDefKey).val("");
                clearUser(taskDefKey);
            }
        });

        $(".passType").combobox({
            onChange: function (n,o) {
                var taskDefKey = $(this).attr("id").replace("passType", "");
                if(n == 1){
                    $("#percentageDiv"+taskDefKey).show();
                    $("#userCountNeedDiv"+taskDefKey).hide();
                }else if(n == 2){
                    $("#percentageDiv"+taskDefKey).hide();
                    $("#userCountNeedDiv"+taskDefKey).show();
                }
            }
        });
    });

    function clearUser(taskDefKey){
        var taskJsonStr = $("#taskJson").val();
        if(taskJsonStr != ""){
            var taskJsonVal = JSON.parse(taskJsonStr);
            $.each(taskJsonVal,function(i,o){
                if(taskDefKey == o.taskDefKey){
                    o.code = "";
                    o.name = "";

                    return false;
                }
            })

            $("#taskJson").val(JSON.stringify(taskJsonVal));
        }
    }

    //配置人员
    function configAssignee(taskDefKey,procDefKey){
        $("#taskKey").val(taskDefKey);

    	var taskType = $(document.getElementById("taskType"+taskDefKey)).val();
        var assignType = $(document.getElementById("assignType"+taskDefKey)).val();

        if(assignType === "1"){
            //部门设置
            $("#configDepartmentDialog").dialog({
                title : '选择部门',
                width : 500,
                height : 450,
                href :  '${ctx}/assignee/select/department',
                buttons : [ {
                    text : '确定',
                    handler : function() {
                        //给输入框赋人员名称的值
                        var taskJsonStr = $("#taskJsonSelect").val();
                        if(taskJsonStr == undefined || taskJsonStr == ""){
                            $("#configDepartmentDialog").dialog('close');
                            return;
                        }
                        var taskJsonVal = JSON.parse(taskJsonStr);
                        for(var i=0;i<taskJsonVal.length;i++){
                            if(taskJsonVal[i].taskDefKey===taskDefKey){
                                if(taskJsonVal[i].code != undefined){
                                    $("#taskUser"+taskDefKey).val(taskJsonVal[i].name);
                                    if(taskJsonVal[i].name){
                                        $("#taskUser"+taskDefKey).attr("title",taskJsonVal[i].name.replace(/,/g,"\n"));
                                    }
                                }
                            }
                        }

                        $("#taskJson").val($("#taskJsonSelect").val());
                        $("#taskJsonSelect").val("");
                        $("#configDepartmentDialog").dialog('close');
                    }
                }]
            });
        }else if(assignType === "2"){
            //角色设置
            $("#configRoleDialog").dialog({
                title : '选择角色',
                width : 500,
                height : 450,
                href :  '${ctx}/assignee/select/role?procDefKey='+procDefKey,
                buttons : [ {
                    text : '确定',
                    handler : function() {
                        //给输入框赋人员名称的值
                        var taskJsonStr = $("#taskJsonSelect").val();
                        if(taskJsonStr == undefined || taskJsonStr == ""){
                            $("#configRoleDialog").dialog('close');
                            return;
                        }
                        var taskJsonVal = JSON.parse(taskJsonStr);
                        for(var i=0;i<taskJsonVal.length;i++){
                            if(taskJsonVal[i].taskDefKey===taskDefKey){
                                if(taskJsonVal[i].code != undefined){
                                    $("#taskUser"+taskDefKey).val(taskJsonVal[i].name);
                                    if(taskJsonVal[i].name){
                                        $("#taskUser"+taskDefKey).attr("title",taskJsonVal[i].name.replace(/,/g,"\n"));
                                    }
                                }
                            }
                        }

                        $("#taskJson").val($("#taskJsonSelect").val());
                        $("#taskJsonSelect").val("");
                        $("#configRoleDialog").dialog('close');
                    }
                }]
            });
        }else if(assignType === "3"){
            //人员设置
            $("#configUserDialog").dialog({
                title : '选择人员',
                width : 500,
                height : 450,
                href :  '${ctx}/assignee/select/user',
                buttons : [ {
                    text : '确定',
                    handler : function() {
                        //给输入框赋人员名称的值
                        var taskJsonStr = $("#taskJsonSelect").val();
                        if(taskJsonStr == undefined || taskJsonStr == ""){
                            $("#configUserDialog").dialog('close');
                            return;
                        }
                        var taskJsonVal = JSON.parse(taskJsonStr);
                        for(var i=0;i<taskJsonVal.length;i++){
                            if(taskJsonVal[i].taskDefKey===taskDefKey){
                                if(taskJsonVal[i].code != undefined){
                                    $("#taskUser"+taskDefKey).val(taskJsonVal[i].name);
                                    if(taskJsonVal[i].name){
                                        $("#taskUser"+taskDefKey).attr("title",taskJsonVal[i].name.replace(/,/g,"\n"));
                                    }
                                }
                            }
                        }

                        $("#taskJson").val($("#taskJsonSelect").val());
                        $("#taskJsonSelect").val("");
                        $("#configUserDialog").dialog('close');
                    }
                }]
            });
        }else if(assignType === "4"){
            //表达式设置
            $("#configExprDialog").dialog({
                title : '选择表达式',
                width : 500,
                height : 450,
                href :  '${ctx}/expr/select',
                buttons : [ {
                    text : '确定',
                    handler : function() {
                        //给输入框赋人员名称的值
                        var taskJsonStr = $("#taskJsonSelect").val();
                        if(taskJsonStr == undefined || taskJsonStr == ""){
                            $("#configExprDialog").dialog('close');
                            return;
                        }
                        var taskJsonVal = JSON.parse(taskJsonStr);
                        for(var i=0;i<taskJsonVal.length;i++){
                            if(taskJsonVal[i].taskDefKey===taskDefKey){
                                if(taskJsonVal[i].code != undefined){
                                    $("#taskUser"+taskDefKey).val(taskJsonVal[i].name);
                                    if(taskJsonVal[i].name){
                                        $("#taskUser"+taskDefKey).attr("title",taskJsonVal[i].name.replace(/,/g,"\n"));
                                    }
                                }
                            }
                        }

                        $("#taskJson").val($("#taskJsonSelect").val());
                        $("#taskJsonSelect").val("");
                        $("#configExprDialog").dialog('close');
                    }
                }]
            });
        }
    }

    /**
     * 权限按钮设置
     * @param taskDefKey 任务定义key
     */
    function configButton(taskDefKey){
        $("#taskKey").val(taskDefKey);
        $("#configButtonDialog").dialog({
            title : '选择按钮',
            width : 500,
            height : 450,
            href :  '${ctx}/button/select',
            buttons : [ {
                text : '确定',
                handler : function() {
                    //给输入框赋人员名称的值
                    var taskJsonStr = $("#taskJsonSelect").val();
                    if(taskJsonStr == undefined || taskJsonStr == ""){
                        $("#configButtonDialog").dialog('close');
                        return;
                    }
                    var taskJsonVal = JSON.parse(taskJsonStr);
                    for(var i=0;i<taskJsonVal.length;i++){
                        if(taskJsonVal[i].taskDefKey===taskDefKey){
                            if(taskJsonVal[i].buttonName != undefined){
                                $("#taskButton"+taskDefKey).val(taskJsonVal[i].buttonName);
                                if(taskJsonVal[i].buttonName){
                                    $("#taskButton"+taskDefKey).attr("title",taskJsonVal[i].buttonName.replace(/,/g,"\n"));
                                }
                            }
                        }
                    }

                    $("#taskJson").val($("#taskJsonSelect").val());
                    $("#taskJsonSelect").val("");
                    $("#configButtonDialog").dialog('close');
                }
            }]
        });
    }
</script>
</body>
</html>