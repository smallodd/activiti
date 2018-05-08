<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>

<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
	<table data-options="region:'center'" id="taskCandidateRoleGrid" style="width:470px;height:360px;"></table>
</div>
<script>
    var taskDefKey = $("#taskKey").val();
    var taskType = $("#taskType"+taskDefKey).val();
    var assignType = $("#assignType"+taskDefKey).val();
    var singleSelect = true;
    if(taskType==="candidateUser" || taskType==="counterSign"){
        singleSelect = false;
    }
    var dataGrid;
    $(function() {
        dataGrid = $('#taskCandidateRoleGrid').datagrid({
            url : '${ctx}/emp/role/1',
            border:true,
            singleSelect : singleSelect,
            fitColumns : true,
            striped: true,
            idField : 'id',
            sortName : 'id',
            sortOrder : 'asc',
            columns : [ [ {
                width : '110px',
                title : '主键',
                field : 'id',
                sortable : true,
                hidden:true
            }, {
                width : '200px',
                title : '角色名称',
                field : 'roleName',
                sortable : true
            }, {
                width : '260px',
                title : '描述',
                field : 'description',
                sortable : true
            }] ],
            onSelect: function (rowIndex, rowData) {
                var jsonStr = $("#taskJsonSelect").val();
                if(jsonStr == ""){
                    jsonStr = $("#taskJson").val();
                }

                if(jsonStr===""){
                    var taskArray = [];
                    var jsonObj = {};

                    jsonObj.id = $("#taskId"+taskDefKey).val();
                    jsonObj.taskDefKey = taskDefKey;
                    jsonObj.taskType = taskType;
                    jsonObj.assignType = assignType;
                    jsonObj.name = rowData.roleName;
                    jsonObj.code = rowData.id;
                    taskArray.push(jsonObj);

                    var taskStr = JSON.stringify(taskArray);
                    $("#taskJsonSelect").val(taskStr);
                }else{
                    var taskArray = JSON.parse(jsonStr);
                    if(singleSelect){
                        for(var i=0;i<taskArray.length;i++) {
                            if (taskArray[i].taskDefKey == taskDefKey) {
                                var user = taskArray[i];
                                user.name = rowData.roleName;
                                user.code = rowData.id;
                                user.taskType = taskType;
                                user.assignType = assignType;

                                taskArray[i] = user;

                                var taskStr = JSON.stringify(taskArray);
                                $("#taskJsonSelect").val(taskStr);
                                break;
                            }
                        }
                    }else{
                        var b = false;
                        for(var i=0;i<taskArray.length;i++){
                            if(taskArray[i].taskDefKey == taskDefKey){
                                b = true;
                                var user = taskArray[i];
                                if(user.code){
                                    if($.inArray(rowData.id, user.code.split(",")) < 0){
                                        user.name = ((user.name=="")?"":(user.name + ",")) + rowData.roleName;
                                        user.code = (user.code==""?"":(user.code + ",")) + rowData.id;
                                        user.taskType = taskType;
                                        user.assignType = assignType;

                                        taskArray[i] = user;

                                        var taskStr = JSON.stringify(taskArray);
                                        $("#taskJsonSelect").val(taskStr);
                                        break;
                                    }
                                }else{
                                    user.name = ((user.name=="" || user.name == undefined)?"":(user.name + ",")) + rowData.roleName;
                                    user.code = ((user.code=="" || user.code == undefined)?"":(user.code + ",")) + rowData.id;
                                    user.taskType = taskType;
                                    user.assignType = assignType;
                                    taskArray[i] = user;

                                    var taskStr = JSON.stringify(taskArray);
                                    $("#taskJsonSelect").val(taskStr);
                                    break;
                                }
                            }
                        }
                        if(!b){
                            var jsonObj = {};

                            jsonObj.id = $("#taskId"+taskDefKey).val();
                            jsonObj.taskDefKey = taskDefKey;
                            jsonObj.taskType = taskType;
                            jsonObj.assignType = assignType;
                            jsonObj.name = rowData.roleName;
                            jsonObj.code = rowData.id;

                            taskArray.push(jsonObj);
                            var taskStr = JSON.stringify(taskArray);
                            $("#taskJsonSelect").val(taskStr);
                        }
                    }
                }
            },
            onUnselect: function (rowIndex, rowData) {
                var jsonStr = $("#taskJsonSelect").val();
                if(jsonStr == ""){
                    jsonStr = $("#taskJson").val();
                }
                var taskArray = JSON.parse(jsonStr);
                for(var i=0;i<taskArray.length;i++){
                    if(taskArray[i].taskDefKey == taskDefKey){
                        var user  = taskArray[i];
                        var name =  user.roleName;
                        var code = user.id;

                        var nameArray = name.split(",");
                        var valueArray = code.split(",");

                        nameArray.splice($.inArray(rowData.roleName, nameArray), 1)
                        valueArray.splice($.inArray(rowData.id, valueArray), 1)

                        user.name = nameArray.join(",");
                        user.code = valueArray.join(",");

                        taskArray[i] = user;
                        break;
                    }
                }
                var taskStr = JSON.stringify(taskArray);
                $("#taskJsonSelect").val(taskStr);
            },
            onLoadSuccess:function(data){
                var jsonStr = $("#taskJson").val();
                if(jsonStr === ''){
                    return;
                }
                var json = JSON.parse(jsonStr);

                var checkedUser;
                $.each(json,function(i,o){
                    if(o.taskDefKey == taskDefKey){
                        checkedUser = o;
                        return false;
                    }
                })
                if(checkedUser != undefined && checkedUser.id != undefined){
                    var checkedUserArray = checkedUser.id.split(",");
                    $.each(data.rows,function(i,obj){
                        if($.inArray(obj.id, checkedUserArray) >= 0){
                            $('#taskCandidateUserGrid').datagrid('selectRow',i);
                        }
                    })
                }
            },
            toolbar : '#tb_role'
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