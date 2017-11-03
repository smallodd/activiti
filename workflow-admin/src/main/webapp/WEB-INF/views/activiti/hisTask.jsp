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
        <table id="hisTaskDataGrid" data-options="fit:true,border:false"></table>
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
<script type="text/javascript">
    var hisTaskDataGrid;
    $(function() {
        hisTaskDataGrid = $('#hisTaskDataGrid').datagrid({
        url : '${ctx}/activiti/hisTaskDataGrid',
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
        },{
            width : '100',
            title : '申请人',
            field : 'processOwner'
        },{
            width : '450',
            title : '标题',
            field : 'businessName'
        },{
            width : '140',
            title : '任务节点名称',
            field : 'taskName'
        }, {
            width : '140',
            title : '办理时间',
            field : 'taskCreateTime',
            sortable : true
        }]],
        toolbar : '#taskToolbar'
    });
});

/**
 * 清除
 */
function taskCleanFun() {
    $('#taskSearchForm input').val('');
    hisTaskDataGrid.datagrid('load', {});
}
/**
 * 搜索
 */
function taskSearchFun() {
     hisTaskDataGrid.datagrid('load', $.serializeObject($('#taskSearchForm')));
}
</script>
</body>
</html>