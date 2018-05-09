<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
	<div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff;" id="tb_user">
		<form id="candidateUserSearchForm">
			<table>
				<tr>
					<th>工号</th>
					<td><input name="code" placeholder="工号" style="width: 120px;"/></td>
					<th>姓名:</th>
					<td><input name="name" placeholder="姓名" style="width: 120px;"/></td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="userSearchFun();">查询</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="userCleanFun();">清空</a>
					</td>
				</tr>
			</table>
		</form>
	</div>

	<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
		<table data-options="region:'center'" id="taskCandidateUserGrid" style="width:470px;height:360px;"></table>
	</div>
	<script>
        var taskDefKey = $("#taskKey").val();
        var taskType = $("#taskType"+taskDefKey).val();
        var singleSelect = true;
        if(taskType==="candidateUser" || taskType==="counterSign"){
            singleSelect = false;
		}
        var dataGrid;
        $(function() {
            dataGrid = $('#taskCandidateUserGrid').datagrid({
                url : '${ctx}/emp/user',
                pagePosition : 'bottom',
                border:true,
                pagination : true,
                singleSelect : singleSelect,
                fitColumns : true,
                striped: true,
                idField : 'code',
                sortName : 'code',
                sortOrder : 'asc',
                columns : [ [ {
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
                    var jsonStr = $("#taskJsonSelect").val();
                    if(jsonStr == ""){
                        jsonStr = $("#taskJson").val();
                    }

                    if(jsonStr===""){
                        var taskArray = [];
                        var jsonObj = {};

                        jsonObj.id = $("#taskId"+taskDefKey).val();
                        jsonObj.taskDefKey = taskDefKey;
                        jsonObj.name = rowData.name;
                        jsonObj.code = rowData.code;
                        taskArray.push(jsonObj);

                        var taskStr = JSON.stringify(taskArray);
                        $("#taskJsonSelect").val(taskStr);
					}else{
                        var taskArray = JSON.parse(jsonStr);
                        if(singleSelect){
                            for(var i=0;i<taskArray.length;i++) {
                                if (taskArray[i].taskDefKey == taskDefKey) {
                                    var user = taskArray[i];
                                    user.name = rowData.name;
                                    user.code = rowData.code;

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
                                        if($.inArray(rowData.code, user.code.split(",")) < 0){
                                            user.name = ((user.name=="")?"":(user.name + ",")) + rowData.name;
                                            user.code = (user.code==""?"":(user.code + ",")) + rowData.code;

                                            var taskStr = JSON.stringify(taskArray);
                                            $("#taskJsonSelect").val(taskStr);
                                            break;
                                        }
                                    }else{
                                        user.name = ((user.name=="" || user.name == undefined)?"":(user.name + ",")) + rowData.name;
                                        user.code = ((user.code=="" || user.code == undefined)?"":(user.code + ",")) + rowData.code;

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
                                jsonObj.name = rowData.name;
                                jsonObj.code = rowData.code;

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
                            var name =  user.name;
                            var code = user.code;

                            var nameArray = name.split(",");
                            var valueArray = code.split(",");

                            nameArray.splice($.inArray(rowData.name, nameArray), 1)
                            valueArray.splice($.inArray(rowData.code, valueArray), 1)

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
					if(checkedUser != undefined && checkedUser.code != undefined){
                        var checkedUserArray = checkedUser.code.split(",");
                        $.each(data.rows,function(i,obj){
                            if($.inArray(obj.code, checkedUserArray) >= 0){
                                $('#taskCandidateUserGrid').datagrid('selectRow',i);
                            }
                        })
					}
                },
                toolbar : '#tb_user'
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