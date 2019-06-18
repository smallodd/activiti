<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>列表</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff">
        <form id="tUserTaskSearchForm">
            <table>
                <tr>
                    <th>流程定义KEY:</th>
                    <td><input name="key" placeholder="搜索条件"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="tUserTaskSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="tUserTaskCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
     </div>
 
    <div data-options="region:'center',border:true,title:''">
        <table id="tUserTaskDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="tUserTaskToolbar" style="display: none;">
    <%-- <shiro:hasPermission name="/activiti/deploy">
        <a onclick="tUserTaskDeployFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-upload icon-green'">流程部署</a>
    </shiro:hasPermission> --%>
</div>
<script type="text/javascript">
    var tUserTaskDataGrid;
    $(function() {
    	tUserTaskDataGrid = $('#tUserTaskDataGrid').datagrid({
        url : '${ctx}/tUserTask/dataGrid',
        striped : true,
        rownumbers : true,
        pagination : true,
        singleSelect : true,
        idField : 'id',
        sortName : 'id',
        sortOrder : 'asc',
        pageSize : 20,
        pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500,1000],
        columns : [[{
            width : '200',
            title : '流程定义ID',
            field : 'id',
            sortable : true
        }, {
            width : '200',
            title : '流程定义名称',
            field : 'name'
        }, {
            width : '200',
            title : '流程定义KEY',
            field : 'key'
        }, {
            width : '90',
            title : '流程定义版本',
            field : 'version'
        }, {
            width : '140',
            title : '部署时间',
            field : 'deployTime'
        }, {
            width : '140',
            title : '资源名称',
            field : 'resourceName'
        }, {
            width : '140',
            title : '流程图片名称',
            field : 'imageName'
        }, {
            width : '140',
            title : '挂起状态',
            field : 'suspended',
            sortable : true,
            formatter : function(value, row, index) {
                switch (value) {
                case '1':
                    return '未挂起';
                case '2':
                    return '已挂起';
                }
            }
        }, {
            field : 'action',
            title : '操作',
            width : 200,
            formatter : function(value, row, index) {
                var str = '';
                <shiro:hasPermission name="/tUserTask/configUser">
                    str += $.formatString('<a href="javascript:void(0)" class="tUserTask-easyui-linkbutton-configUser" data-options="plain:true,iconCls:\'fi-torsos-male-female icon-green\'" onclick="configUserFun(\'{0}\');" >设定人员</a>', row.id);
                </shiro:hasPermission>
                <shiro:hasPermission name="/task/start">
                str += $.formatString('<a href="javascript:void(0)" class="tUserTask-easyui-linkbutton-start" data-options="plain:true,iconCls:\'fi-torsos-male-female icon-green\'" onclick="startProcessInstance(\'{0}\');" >模拟流程开启</a>', row.key);
                </shiro:hasPermission>
                return str;
            }
        }]],
        onLoadSuccess:function(data){
            $('.tUserTask-easyui-linkbutton-configUser').linkbutton({text:'设定人员'});
            $('.tUserTask-easyui-linkbutton-start').linkbutton({text:'模拟开启'});
        },
        toolbar : '#tUserTaskToolbar'
    });
});
    function startProcessInstance(processKey){
        $.ajax({
            type: 'POST',
            dataType : 'json',
            url: '${ctx}/activiti/startTask',
            data: {"processKey":processKey},
            success: function(json){
                alert(json.msg);
            }
        });
    }
/**
 * 设定人员
 */
function configUserFun(id) {
    if (id == undefined) {
        var rows = tUserTaskDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
        tUserTaskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    parent.$.modalDialog({
        title : '设定人员',
        width : 500,
        height : 450,
        href :  '${ctx}/tUserTask/configUserPage?id=' + id,
        buttons : [ {
            text : '确定',
            handler : function() {
                parent.$.modalDialog.openner_dataGrid = tUserTaskDataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#tUserTaskConfigForm');
                f.submit();
            }
        } ]
    });
}
    
/**
 * 清除
 */
function tUserTaskCleanFun() {
    $('#tUserTaskSearchForm input').val('');
    tUserTaskDataGrid.datagrid('load', {});
}

/**
 * 搜索
 */
function tUserTaskSearchFun() {
    tUserTaskDataGrid.datagrid('load', $.serializeObject($('#tUserTaskSearchForm')));
}
</script>
</body>
</html>