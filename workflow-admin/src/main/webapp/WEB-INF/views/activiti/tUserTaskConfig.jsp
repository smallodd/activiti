<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>设定人员</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: auto;padding: 8px;">
        <form id="tUserTaskConfigForm" method="post">
        	<input id="taskJson" name="taskJson" type="hidden" value="${taskJson}"/>
            <input id="taskJsonSelect" type="hidden" value="${taskJson}"/>
            <table class="grid">
            <tr>
                <td><strong>任务名称</strong></td>
                <td><strong>配置选项类型</strong></td>
                <td><strong>人员或部门选项</strong></td>
                <td><strong>通过人数</strong></td>
            </tr>
            <c:forEach var="task" items="${tasks}">
				<tr>
					<td>[${task.taskName}]</td>
					<td>
						<input id="taskId${task.taskDefKey}" type="hidden" value="${task.id}"/>
						<select id="${task.taskDefKey}" class="easyui-combobox selectAssessmentType" data-options="width:100,height:29,panelHeight:'auto'">
                            <option value="assignee" selected="selected">受理人</option>
                            <option value="candidateUser">候选人</option>
                            <%--<option value="candidateGroup">候选组</option>--%>
                            <option value="counterSign">会签人</option>
                        </select>
                    </td>
                    <td><input id="taskUser${task.taskDefKey}" placeholder="点击选择" data-options="required:true" style="width:170;height:29" onclick="configUser('${task.taskDefKey}')"></input></td>
				    <td>
                        <c:choose>
                            <c:when test="${task.taskType == 'counterSign'}"><c:set var="index" value="${fn:length(task.candidateIds.split(','))}"></c:set></c:when>
                            <c:otherwise><c:set var="index" value="1"></c:set></c:otherwise>
                        </c:choose>

                        <select class="easyui-combobox" data-options="width:60,height:29,panelHeight:'auto'" id="userCount${task.taskDefKey}">
                            <c:forEach var="i" begin="1" end="${index}">
                                <c:choose>
                                    <c:when test="${(index+1-i)==task.userCountNeed}"><option value="${index+1-i}" selected="selected">${index+1-i}</option></c:when>
                                    <c:otherwise><option value="${index+1-i}">${index+1-i}</option></c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
			</c:forEach>
            </table>
        </form>
    </div>
</div>
<div id="taskAssigneeDialog"></div>
<div id="taskCandidateUserDialog"></div>
<div id="taskCandidateGroupDialog"></div>
<input type="hidden" id="taskKey"/>
<script type="text/javascript">
    $(function() {
    	//给各个任务的人员名称和ID赋值
    	var taskJson= $("#taskJson").val();
    	if(taskJson!==""){
    		var taskJsonArray= JSON.parse(taskJson);
        	for(var i=0;i<taskJsonArray.length;i++){
        		var key = taskJsonArray[i].key;
        		$("#taskUser"+key).val(taskJsonArray[i].name);
        		$("#"+key).val(taskJsonArray[i].type);
        	}
    	}
    	
    	//Form表单提交
        $('#tUserTaskConfigForm').form({
            url : '${ctx}/tUserTask/configUser',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }

            	var taskJsonVal = JSON.parse($("#taskJson").val());
            	for(var i=0;i<taskJsonVal.length;i++){
                    taskJsonVal[i].type=$("#"+taskJsonVal[i].key).val();

                    taskJsonVal[i].userCountNeed = $("#userCount"+taskJsonVal[i].key).combobox("getValue");
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

        $(".selectAssessmentType").combobox({
            onChange: function (n,o) {
                $("#taskJsonSelect").val("");

                var taskKey = $(this).attr("id");
                $("#taskUser"+taskKey).val("");
                $("#userCount"+taskKey).combobox("clear");
                $("#userCount"+taskKey).combobox("loadData",[]);
                clearUser(taskKey);
            }
        });
    });

    function clearUser(taskKey){
        var taskJsonStr = $("#taskJson").val();
        if(taskJsonStr != ""){
            var taskJsonVal = JSON.parse(taskJsonStr);
            $.each(taskJsonVal,function(i,o){
                if(taskKey == o.key){
                    o.value = "";
                    o.name = "";

                    return false;
                }
            })

            $("#taskJson").val(JSON.stringify(taskJsonVal));
        }
    }
    
    //配置人员
    function configUser(datas){
        $("#taskKey").val(datas);

    	var taskType = $(document.getElementById(datas)).val();

    	if(taskType==="assignee"){
    	    $("#taskAssigneeDialog").dialog({
    	        title : '选择受理人',
    	        width : 500,
    	        height : 450,
    	        href :  '${ctx}/tUserTask/taskAssignee',
    	        buttons : [ {
    	            text : '确定',
    	            handler : function() {
    	            	var rows = $("#taskAssigneeGrid").datagrid("getSelections");
    	            	var jsonStr= $("#taskJson").val();
    	            	if(jsonStr===""){
    	            		var jsonArray = [];
        	            	var jsonObj = {};
        	            	jsonObj.id=$("#taskId"+datas).val();
        	            	jsonObj.key=datas;
        	            	jsonObj.type=$("#"+datas).val();
        	            	jsonObj.name=rows[0].userName;
        	            	jsonObj.value=rows[0].id;
        	            	jsonArray.push(jsonObj);
        	            	var taskStr = JSON.stringify(jsonArray);
        	            	$("#taskJson").val(taskStr);
    	            	}else{
    	            		var jsonObj = {};
    	            		jsonObj.id=$("#taskId"+datas).val();
        	            	jsonObj.key=datas;
        	            	jsonObj.type=$("#"+datas).val();
    	            		jsonObj.name=rows[0].userName;
    	            		jsonObj.value=rows[0].id;
        	            	var taskArray = JSON.parse(jsonStr);
        	            	taskArray.push(jsonObj);
        	            	var taskStr = JSON.stringify(taskArray);
        	            	$("#taskJson").val(taskStr);
    	            	}
    	            	//给输入框赋人员名称的值
    	            	var taskJsonVal = JSON.parse($("#taskJson").val());
    	            	for(var i=0;i<taskJsonVal.length;i++){
    	            		if(taskJsonVal[i].key===datas){
    	            			$("#taskUser"+datas).val(taskJsonVal[i].name);
    	            		}
    	            	}

                        var dataList = [];
                        dataList.push({"value": 1,"text":1});

                        if(dataList.length > 0){
                            $("#userCount"+datas).combobox("clear");
                            $("#userCount"+datas).combobox("loadData",dataList);
                            $("#userCount"+datas).combobox("select",dataList.length);
                        }
    	            	$("#taskAssigneeDialog").dialog('close');
    	            }
    	        } ]
    	    });
    	}else if(taskType==="candidateUser" || taskType==="counterSign"){
    		$("#taskCandidateUserDialog").dialog({
    	        title : '选择人员',
    	        width : 500,
    	        height : 450,
    	        href :  '${ctx}/tUserTask/taskCandidateUser',
    	        buttons : [ {
    	            text : '确定',
    	            handler : function() {
    	                var userCount = 0;
    	            	//给输入框赋人员名称的值
                        var taskJsonStr = $("#taskJsonSelect").val();
                        if(taskJsonStr == undefined || taskJsonStr == ""){
                            $("#taskCandidateUserDialog").dialog('close');
                            return;
                        }
    	            	var taskJsonVal = JSON.parse(taskJsonStr);
    	            	for(var i=0;i<taskJsonVal.length;i++){
    	            		if(taskJsonVal[i].key===datas){
    	            		    if(taskJsonVal[i].value != undefined){
                                    userCount = taskJsonVal[i].value.split(",").length;
                                    $("#taskUser"+datas).val(taskJsonVal[i].name);
                                }
    	            		}
    	            	}

                        var dataList = [];
                        if(taskType==="counterSign"){
                            for(var i=1;i<userCount+1;i++){
                                dataList.push({"value": i,"text":i});
                            }
                        }else{
                            dataList.push({"value": 1,"text":1});
                        }

                        if(dataList.length > 0){
                            $("#userCount"+datas).combobox("loadData",dataList);
                            $("#userCount"+datas).combobox("select",dataList.length);
                        }
                        $("#taskJson").val($("#taskJsonSelect").val());
                        $("#taskJsonSelect").val("");
    	            	$("#taskCandidateUserDialog").dialog('close');
    	            }
    	        } ]
    	    });
    	}else if(taskType==="candidateGroup"){
    		$("#taskCandidateGroupDialog").dialog({
    	        title : '选择候选组',
    	        width : 500,
    	        height : 450,
    	        href :  '${ctx}/tUserTask/taskCandidateGroup',
    	        buttons : [ {
    	            text : '确定',
    	            handler : function() {
    	            	var rows = $("#taskCandidateGroupGrid").datagrid("getSelections");
    	            	var jsonStr= $("#taskJson").val();
    	            	if(jsonStr===""){
    	            		var jsonArray = [];
    	            		var jsonObj = {};
    	            		var names = "";
    	            		var ids = "";
    	            		jsonObj.id=$("#taskId"+datas).val();
        	            	jsonObj.key=datas;
        	            	jsonObj.type=$("#"+datas).val();
    	            		for(var i=0;i<rows.length;i++){
    	            			if(i===(rows.length-1)){
    	            				names+=rows[i].departmentName;
                	            	ids+=rows[i].id;
    	            			}else{
    	            				names+=rows[i].departmentName+",";
                	            	ids+=rows[i].id+",";
    	            			}
    	            		}
    	            		jsonObj.name=names;
        	            	jsonObj.value=ids;
    	            		jsonArray.push(jsonObj);
        	            	var taskStr = JSON.stringify(jsonArray);
        	            	$("#taskJson").val(taskStr);
    	            	}else{
    	            		var jsonObj = {};
    	            		var names = "";
    	            		var ids = "";
    	            		jsonObj.id=$("#taskId"+datas).val();
        	            	jsonObj.key=datas;
        	            	jsonObj.type=$("#"+datas).val();
    	            		for(var i=0;i<rows.length;i++){
    	            			if(i===(rows.length-1)){
    	            				names+=rows[i].departmentName;
                	            	ids+=rows[i].id;
    	            			}else{
    	            				names+=rows[i].departmentName+",";
                	            	ids+=rows[i].id+",";
    	            			}
    	            		}
    	            		jsonObj.name=names;
        	            	jsonObj.value=ids;
        	            	var taskArray = JSON.parse(jsonStr);
        	            	taskArray.push(jsonObj);
        	            	var taskStr = JSON.stringify(taskArray);
        	            	$("#taskJson").val(taskStr);
    	            	}
    	            	//给输入框赋人员名称的值
    	            	var taskJsonVal = JSON.parse($("#taskJson").val());
    	            	for(var i=0;i<taskJsonVal.length;i++){
    	            		if(taskJsonVal[i].key===datas){
    	            			$("#taskUser"+datas).val(taskJsonVal[i].name);
    	            		}
    	            	}
    	            	$("#taskCandidateGroupDialog").dialog('close');
    	            }
    	        } ]
    	    });
    	}
    }
</script>
</body>
</html>