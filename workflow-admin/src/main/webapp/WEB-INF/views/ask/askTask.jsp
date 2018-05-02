<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>问询列表</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff">
        <form id="taskSearchForm">
            <table>
                <tr>
                    <th>被问询人:</th>
                    <td><input name="askUserId" placeholder="被问询人"/></td>
                    <th>问询任务节点key:</th>
                    <td><input name="currentTaskKey" placeholder="问询任务节点key"/></td>
                    <th>被问询任务节点key:</th>
                    <td><input name="askTaskKey" placeholder="被问询任务节点key"/></td>
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
</div>
<div id="showTaskWindow"></div>
<script type="text/javascript">
    var taskDataGrid;
    $(function() {
        taskDataGrid = $('#taskDataGrid').datagrid({
            url : '${ctx}/ask/askTaskDataGrid',
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
                width : '80',
                title : '问询是否结束',
                field : 'isAskEnd',
                formatter : function(value, row, index) {
                    switch (value) {
                    case 0:
                        return '否';
                    case 1:
                        return '是';
                    }
                }
            }, {
                width : '200',
                title : '流程实例名称',
                field : 'procInstName'
            },{
                width : '200',
                title : '问询任务节点名称',
                field : 'currentTaskName'
            },{
                width : 200,
                title : '问询人',
                field : 'createId'
            }, {
                width : 200,
                title : '被问询人',
                field : 'askUserId'
            }, {
                width : 200,
                title : '被问询任务节点名称',
                field : 'askTaskName'
            } ] ],
            onLoadSuccess:function(data){
            },
            toolbar : '#taskToolbar'
        });
    });

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