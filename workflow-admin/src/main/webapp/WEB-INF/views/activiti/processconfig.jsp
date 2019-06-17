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
        <form id="processdefSearchForm">
            <table>
                <tr>
                    <th>名称:</th>
                    <td><input name="name" placeholder="搜索条件"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="processdefSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="processdefCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
     </div>
 
    <div data-options="region:'center',border:false">
        <table id="processdefDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="processdefToolbar" style="display: none;">
    <%-- <shiro:hasPermission name="/activiti/deploy">
        <a onclick="processdefDeployFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-upload icon-green'">流程部署</a>
    </shiro:hasPermission> --%>
</div>
<script type="text/javascript">
    var processdefDataGrid;
    $(function() {
        processdefDataGrid = $('#processdefDataGrid').datagrid({
        url : '${ctx}/activiti/processconfigDataGrid',
        striped : true,
        rownumbers : true,
        pagination : true,
        singleSelect : true,
        idField : 'id',
        sortName : 'id',
        sortOrder : 'asc',
        pageSize : 20,
        pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500],
        columns : [ [ {
            width : '140',
            title : '流程定义ID',
            field : 'id',
            sortable : true
        }, {
            width : '140',
            title : '流程定义名称',
            field : 'name'
        }, {
            width : '140',
            title : '流程定义KEY',
            field : 'key'
        }, {
            width : '140',
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
        }/*,  {
            field : 'action',
            title : '操作',
            width : 200,
            formatter : function(value, row, index) {
                var str = '';
                <shiro:hasPermission name="/processdef/edit">
                    str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="processdefEditFun(\'{0}\');" >编辑</a>', row.id);
                </shiro:hasPermission>
                <shiro:hasPermission name="/processdef/delete">
                    str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                    str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="processdefDeleteFun(\'{0}\');" >删除</a>', row.id);
                </shiro:hasPermission>
                return str;
            }
        } */ ] ],
        onLoadSuccess:function(data){
            /* $('.processdef-easyui-linkbutton-edit').linkbutton({text:'编辑'});
            $('.processdef-easyui-linkbutton-del').linkbutton({text:'删除'}); */
        },
        toolbar : '#processdefToolbar'
    });
});

/**
 * 编辑
 */
/* function processdefEditFun(id) {
    if (id == undefined) {
        var rows = processdefDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
        processdefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    parent.$.modalDialog({
        title : '编辑',
        width : 700,
        height : 600,
        href :  '${ctx}/processdef/editPage?id=' + id,
        buttons : [ {
            text : '确定',
            handler : function() {
                parent.$.modalDialog.openner_dataGrid = processdefDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#processdefEditForm');
                f.submit();
            }
        } ]
    });
} */

/**
 * 删除
 */
/*  function processdefDeleteFun(id) {
     if (id == undefined) {//点击右键菜单才会触发这个
         var rows = processdefDataGrid.datagrid('getSelections');
         id = rows[0].id;
     } else {//点击操作里面的删除图标会触发这个
         processdefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
     }
     parent.$.messager.confirm('询问', '您是否要删除当前角色？', function(b) {
         if (b) {
             progressLoad();
             $.post('${ctx}/processdef/delete', {
                 id : id
             }, function(result) {
                 if (result.success) {
                     parent.$.messager.alert('提示', result.msg, 'info');
                     processdefDataGrid.datagrid('reload');
                 }
                 progressClose();
             }, 'JSON');
         }
     });
} */

/**
 * 清除
 */
function processdefCleanFun() {
    $('#processdefSearchForm input').val('');
    processdefDataGrid.datagrid('load', {});
}

/**
 * 搜索
 */
function processdefSearchFun() {
     processdefDataGrid.datagrid('load', $.serializeObject($('#processdefSearchForm')));
}
</script>
</body>
</html>