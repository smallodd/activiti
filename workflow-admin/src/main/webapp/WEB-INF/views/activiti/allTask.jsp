<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>列表</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff">
        <form id="taskSearchForm">
            <table>
                <tr>
                    <th>名称:</th>
                    <td><input name="name" placeholder="搜索条件"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="taskSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="taskCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
     </div>
 
    <div data-options="region:'center',border:false">
        <table id="taskDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="taskToolbar" style="display: none;">
    <%-- <shiro:hasPermission name="/task/add">
        <a onclick="taskAddFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-page-add'">添加</a>
    </shiro:hasPermission> --%>
</div>
<div id="delegateTaskDialog"></div>
<div id="transferTaskDialog"></div>
<div id="jumpTaskDialog"></div>
<div id="showTaskWindow"></div>
<script type="text/javascript">
    var taskDataGrid;
    $(function() {
        taskDataGrid = $('#taskDataGrid').datagrid({
        url : '${ctx}/activiti/allTaskDataGrid',
        striped : true,
        rownumbers : true,
        pagination : true,
        singleSelect : true,
        fitColumns : true,
        idField : 'id',
        sortName : 'id',
        sortOrder : 'asc',
        pageSize : 20,
        pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500],
        columns : [ [ {
            width : '60',
            title : '主键',
            field : 'id',
            hidden:true
        }, {
            width : '100',
            title : '状态',
            field : 'taskState',
            sortable : true,
            formatter : function(value, row, index) {
                switch (value) {
                case '1':
                    return '待签收';
                case '2':
                    return '待受理';
                }
            }
        }, {
            width : '100',
            title : '申请人',
            field : 'processOwner'
        },{
            width : '450',
            title : '标题',
            field : 'businessName'
        },{
            width : '140',
            title : '当前任务节点名称',
            field : 'taskName'
        },{
            width : '100',
            title : '当前审批人',
            field : 'taskAssign'
        }, {
            width : '140',
            title : '当前任务创建时间',
            field : 'taskCreateTime',
            sortable : true
        }, {
            field : 'action',
            title : '操作',
            width : 350,
            formatter : function(value, row, index) {
                  var str = '';

                	<shiro:hasPermission name="/activiti/claimTask">
                    	str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-claimTask" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="claimTaskFun(\'{0}\');" >签收</a>', row.id);
                	</shiro:hasPermission>

                	<shiro:hasPermission name="/activiti/complateTask">
                		str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-complateTask" data-options="plain:true,iconCls:\'fi-monitor icon-purple\'" onclick="complateTaskFun(\'{0}\');" >办理</a>', row.id);
            		</shiro:hasPermission>
            		<shiro:hasPermission name="/activiti/delegateTask">
                    	str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                    	str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-delegateTask" data-options="plain:true,iconCls:\'fi-torsos-male-female icon-green\'" onclick="delegateTaskFun(\'{0}\');" >委派</a>', row.id);
                	</shiro:hasPermission>
                	<shiro:hasPermission name="/activiti/transferTask">
                		str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                		str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-transferTask" data-options="plain:true,iconCls:\'fi-rewind-ten icon-red\'" onclick="transferTaskFun(\'{0}\');" >转办</a>', row.id);
            		</shiro:hasPermission>
            		<shiro:hasPermission name="/activiti/jumpTask">
            			str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
            			str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-jumpTask" data-options="plain:true,iconCls:\'fi-share icon-yellow\'" onclick="jumpTaskFun(\'{0}\');" >跳转</a>', row.id);
        			</shiro:hasPermission>
                    str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                    str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-taskProgress" data-options="plain:true,iconCls:\'fi-arrow-right icon-grey\'" onclick="showTaskFun(\'{0}\');" >进度</a>', row.processInstanceId);
                return str;
            }
        } ] ],
        onLoadSuccess:function(data){
            $('.task-easyui-linkbutton-claimTask').linkbutton({text:'签收'});
            $('.task-easyui-linkbutton-complateTask').linkbutton({text:'办理'});
            $('.task-easyui-linkbutton-delegateTask').linkbutton({text:'委派'});
            $('.task-easyui-linkbutton-transferTask').linkbutton({text:'转办'});
            $('.task-easyui-linkbutton-jumpTask').linkbutton({text:'跳转'});
            $('.task-easyui-linkbutton-taskProgress').linkbutton({text:'进度'});
        },
        toolbar : '#taskToolbar'
    });
});

    
/**
 * 办理任务
 */
 function complateTaskFun(id){
	if (id == undefined) {
        var rows = taskDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
    	taskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
	parent.$.modalDialog({
        title : '办理',
        width : 400,
        height : 450,
        href : '${ctx}/activiti/complateTaskPage?id='+id,
        buttons : [ {
            text : '确定',
            handler : function() {
                parent.$.modalDialog.openner_dataGrid = taskDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#complateTaskForm');
                f.submit();
            }
        } ]
    });
}


/**
 * 签收任务
 */
 function claimTaskFun(id) {
     if (id == undefined) {//点击右键菜单才会触发这个
         var rows = taskDataGrid.datagrid('getSelections');
         id = rows[0].id;
     } else {//点击操作里面的删除图标会触发这个
         taskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
     }
     parent.$.messager.confirm('询问', '您是否签收当前任务？', function(b) {
         if (b) {
             progressLoad();
             $.post('${ctx}/activiti/claimTask', {
                 id : id
             }, function(result) {
                 if (result.success) {
                     parent.$.messager.alert('提示', result.msg, 'info');
                     taskDataGrid.datagrid('reload');
                 }
                 progressClose();
             }, 'JSON');
         }
     });
}

/**
 * 委派任务(同代办)
 */
 function delegateTaskFun(id){
    parent.$.modalDialog({
	        title : '选择受理人',
	        width : 500,
	        height : 450,
            modal : true,
	        href :  '${ctx}/activiti/taskDelegate',
	        buttons : [ {
	            text : '确定',
	            handler : function() {
	            	var rows = $("#delegateTaskGrid").datagrid("getSelections");
	            	$.post("${ctx}/activiti/delegateTask", { "taskId": id, "userId": rows[0].id },function (result) {
	            		console.log(result);
	                    if (result.success) {
	                    	$.messager.alert('提示', result.msg,'info');
	                    	$('#taskDataGrid').datagrid('reload');
	                        $("#delegateTaskDialog").dialog('close');
	                    } else {
	                        $.messager.alert('错误', result.msg, 'error');
	                    }
	                })
	            }
	        }]
	    });
 }

 /**
  * 转办任务
  */
  function transferTaskFun(id){
     parent.$.modalDialog({
 	        title : '选择受理人',
 	        width : 500,
 	        height : 450,
            modal : true,
 	        href :  '${ctx}/activiti/taskDelegate',
 	        buttons : [ {
 	            text : '确定',
 	            handler : function() {
 	            	var rows = $("#delegateTaskGrid").datagrid("getSelections");
 	            	$.post("${ctx}/activiti/transferTask", { "taskId": id, "userId": rows[0].id },function (result) {
 	                    if (result.success) {
 	                    	$.messager.alert('提示', result.msg,'info');
 	                    	$('#taskDataGrid').datagrid('reload');
 	                        $("#transferTaskDialog").dialog('close');
 	                    } else {
 	                        $.messager.alert('错误', result.msg, 'error');
 	                    }
 	                })
 	            }
 	        }]
 	    });
  }
 
/**
 * 跳转任务
 */
 function jumpTaskFun(id){
	 $("#jumpTaskDialog").dialog({
	        title : '选择任务节点',
	        width : 300,
	        height : 200,
            modal : true,
	        href :  '${ctx}/activiti/taskJump?id='+id,
	        buttons : [ {
	            text : '确定',
	            handler : function() {
	            	var taskDefinitionKey = $("#jumpTaskKey").val();
	            	$.post("${ctx}/activiti/jumpTask", { "taskId": id, "taskDefinitionKey": taskDefinitionKey },function (result) {
	                    if (result.success) {
	                    	$.messager.alert('提示', result.msg,'info');
	                    	$('#taskDataGrid').datagrid('reload');
	                        $("#jumpTaskDialog").dialog('close');
	                    } else {
	                        $.messager.alert('错误', result.msg, 'error');
	                    }
	                })
	            }
	        }]
	    });
 }

/**
 * 查看任务进度
 */
function showTaskFun(processInstanceId) {
    var contentStr= $.formatString('<img src="${ctx}/activiti/showTask/{0}"></img>',processInstanceId);
    $("#showTaskWindow").window({
        title : '任务进度',
        width : 900,
        height : 500,
        content:contentStr,
        modal : true
    });
}

/**
 * 清除
 */
function taskCleanFun() {
    $('#taskSearchForm input').val('');
    taskDataGrid.datagrid('load', {});
}
/**
 * 搜索
 */
function taskSearchFun() {
     taskDataGrid.datagrid('load', $.serializeObject($('#taskSearchForm')));
}
</script>
</body>
</html>