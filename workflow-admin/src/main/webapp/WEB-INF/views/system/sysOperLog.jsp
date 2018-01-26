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
        <form id="sysOperLogSearchForm">
            <table>
                <tr>
                    <th>操作人:</th>
                    <td><input name="operUserName" placeholder="搜索条件"/></td>
                    <th>操作状态:</th>
                    <td>
	                    <select name="operStatus" class="easyui-combobox" data-options="width:140,height:20,editable:false,panelHeight:'auto'">
	                    	<option value=0>请选择</option>
	                    	<option value=1>成功</option>
	                    	<option value=2>失败</option>
	                    </select>
                    </td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="sysOperLogSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="sysOperLogCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div data-options="region:'center',border:false">
        <table id="sysOperLogDataGrid" data-options="fit:true,border:false" style="overflow:hidden;"></table>
    </div>
</div>
<script type="text/javascript">
    var sysOperLogDataGrid;
    $(function() {
        sysOperLogDataGrid = $('#sysOperLogDataGrid').datagrid({
        url : '${ctx}/sysOperLog/dataGrid',
        striped : true,
        rownumbers : true,
        pagination : true,
        singleSelect : true,
        fitColumns : true,
        sortName : 'operTime',
        sortOrder : 'desc',
        pageSize : 20,
        pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500,1000],
        columns : [[{
            width : '60',
            title : '操作人',
            field : 'operUserName'
        },{
            width : '80',
            title : 'IP地址',
            field : 'operClientIp'
        },{
            width : '140',
            title : '操作时间',
            field : 'operTime',
            sortable : true
        },{
            width : '80',
            title : '操作状态',
            field : 'operStatus',
            sortable : true,
            formatter : function(value, row, index) {
                switch (value) {
                case 1:
                    return '成功';
                case 2:
                    return '失败';
                }
            }
        }, {
            width : '120',
            title : '操作事件',
            field : 'operEvent'
        },{
            width : '220',
            title : '请求地址',
            field : 'requestUrl'
        },{
            width : '420',
            title : '请求方法',
            field : 'requestMethod'
        },{
            width : '500',
            title : '描述信息',
            field : 'logDescription'
        }] ],
        toolbar : '#sysOperLogToolbar'
    });
});


/**
 * 清除
 */
function sysOperLogCleanFun() {
    $('#sysOperLogSearchForm input').val('');
    sysOperLogDataGrid.datagrid('load', {});
}

/**
 * 搜索
 */
function sysOperLogSearchFun() {
     sysOperLogDataGrid.datagrid('load', $.serializeObject($('#sysOperLogSearchForm')));
}
</script>
</body>
</html>