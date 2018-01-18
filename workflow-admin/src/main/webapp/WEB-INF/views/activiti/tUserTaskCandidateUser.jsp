<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
	<div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff;" id="tb1">
		<form id="candidateUserSearchForm">
			<table>
				<tr>
					<th>姓名/工号:</th>
					<td><input name="userName" placeholder="姓名/工号"/></td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="userSearchFun();">查询</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="userCleanFun();">清空</a>
					</td>
				</tr>
			</table>
		</form>
	</div>

	<div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
		<table id="taskCandidateUserGrid" class="easyui-datagrid" data-options="fit:true"style="width:400px;"></table>
	</div>
	<script>
        var dataGrid;
        $(function() {
            dataGrid = $('#taskCandidateUserGrid').datagrid({
                url : '${ctx}/sysUser/selectDataGrid',
                pagePosition : 'bottom',
                border:true,
                pagination : true,
                singleSelect : false,
                fitColumns : true,
                idField : 'id',
                sortName : 'id',
                sortOrder : 'asc',
                columns : [ [ {
                    width : '60px',
                    title : '主键',
                    field : 'id',
                    hidden:true
                },{
                    width : '245px',
                    title : '所属部门',
                    field : 'departmentName',
                    sortable : true
                }, {
                    width : '110px',
                    title : '姓名',
                    field : 'userName',
                    sortable : true
                }, {
                    width : '110px',
                    title : '工号',
                    field : 'loginName',
                    sortable : true
                } ] ],
                onSelect: function (rowIndex, rowData) {
                    var jsonStr = $("#taskJson").val();console.info(jsonStr);
                    var taskKey = $("#taskKey").val();

                    if(jsonStr===""){
                        var jsonArray = [];
                        var jsonObj = {};

                        jsonObj.id = $("#taskId"+taskKey).val();
                        jsonObj.key = taskKey;
                        jsonObj.type = $("#"+taskKey).val();
                        jsonObj.name = rowData.userName;
                        jsonObj.value = rowData.id;

                        jsonArray.push(jsonObj);
                        var taskStr = JSON.stringify(jsonArray);
                        $("#taskJson").val(taskStr);
					}else{
                        var taskArray = JSON.parse(jsonStr);
                        for(var i=0;i<taskArray.length;i++){
                            if(taskArray[i].key == taskKey){
                                var user = taskArray[i];
                                if($.inArray(rowData.id, user.value.split(",")) < 0){
                                    user.name = (user.name==""?"":(user.name + ",")) + rowData.userName;
                                    user.value = (user.value==""?"":(user.value + ",")) + rowData.id;

                                    taskArray[i] = user;

                                    var taskStr = JSON.stringify(taskArray);
                                    $("#taskJson").val(taskStr);
                                    break;
								}
                            }
                        }
					}
                },
                onUnselect: function (rowIndex, rowData) {
                    var jsonStr = $("#taskJson").val();
                    var taskKey = $("#taskKey").val();
                    var taskArray = JSON.parse(jsonStr);
                    for(var i=0;i<taskArray.length;i++){
                        if(taskArray[i].key == taskKey){
                            var user  = taskArray[i];
                            var name =  user.name;
                            var value = user.value;

                            var nameArray = name.split(",");
                            var valueArray = value.split(",");

                            nameArray.splice($.inArray(rowData.userName, nameArray), 1)
                            valueArray.splice($.inArray(rowData.id, valueArray), 1)

                            user.name = nameArray.join(",");
                            user.value = valueArray.join(",");

                            taskArray[i] = user;
                            break;
                        }
                    }
                    var taskStr = JSON.stringify(taskArray);
                    $("#taskJson").val(taskStr);
                },
                onLoadSuccess:function(data){
                    var jsonStr = $("#taskJson").val();
                    if(jsonStr === ''){
                        return;
					}
                    var json = JSON.parse(jsonStr);
                    var taskKey = $("#taskKey").val();

                    $.each(json,function(i,o){
                        if(o.key == taskKey){
                            taskKey = o;
                            return false;
                        }
                    })
					var checkedUser = taskKey.value.split(",");
                    $.each(data.rows,function(i,obj){
                        if($.inArray(obj.id, checkedUser) >= 0){
                            $('#taskCandidateUserGrid').datagrid('selectRow',i);
						}
					})
                },
                toolbar : '#tb1'
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