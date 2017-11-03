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
 
    <div data-options="region:'center',border:false" id="dd">
        <table id="processdefDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="processdefToolbar" style="display: none;">
    <shiro:hasPermission name="/activiti/deploy">
        <a onclick="modelCreate();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">新建模型</a>
    </shiro:hasPermission>
</div>
<div id="tVacationGetProcessImage"></div>
<script type="text/javascript">
    var processdefDataGrid;
    $(function() {
        processdefDataGrid = $('#processdefDataGrid').datagrid({
        url : '${ctx}/activiti/model/modelDataGrid',
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
            width : '200',
            title : '流程模型ID',
            field : 'id',
            sortable : true
        }, {
            width : '200',
            title : '流程模型名称',
            field : 'name'
        }, {
            width : '140',
            title : '流程模型KEY',
            field : 'key'
        }, {
            width : '140',
            title : '流程模型版本',
            field : 'version'
        }, {
            field : 'action',
            title : '操作',
            width : 260,
            formatter : function(value, row, index) {
                var str = '';
                if(row.suspended==='1'){
                	<shiro:hasPermission name="/activiti/sleep">
                    	str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-sleep" data-options="plain:true,iconCls:\'fi-stop icon-blue\'" onclick="processdefSleepFun(\'{0}\');" >挂起</a>', row.id);
                	</shiro:hasPermission>
                }
                if(row.suspended==='2'){
                	<shiro:hasPermission name="/activiti/active">
                    	str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-active" data-options="plain:true,iconCls:\'fi-play-circle icon-green\'" onclick="processdefActiveFun(\'{0}\');" >激活</a>', row.id);
                	</shiro:hasPermission>
                }
                str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="modelEdit(\'{0}\');" >编辑模型</a>', row.id);
                str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-sleep" data-options="plain:true,iconCls:\'fi-upload icon-blue\'" onclick="processDeploy(\'{0}\');" >部署流程</a>', row.id);
                str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-active" data-options="plain:true,iconCls:\'fi-magnifying-glass icon-blue\'" onclick="modelDetail(\'{0}\');" >查看详情</a>', row.id);
                return str;
            }
        } ] ],
        onLoadSuccess:function(data){
            $('.model-easyui-linkbutton-edit').linkbutton({text:'编辑模型'});
            $('.model-easyui-linkbutton-sleep').linkbutton({text:'部署流程'});
            $('.model-easyui-linkbutton-active').linkbutton({text:'查看详情'});
        },
        toolbar : '#processdefToolbar'
    });
});

/**
 * 创建流程模型
 */
function modelCreate() {
    parent.$.modalDialog({
        title : '创建流程模型',
        width : 500,
        height : 300,
        href : '${ctx}/activiti/model/addPage',
        buttons : [ {
            text : '确定',
            handler : function() {
                parent.$.modalDialog.openner_dataGrid = processdefDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#processdefDeployForm');
                f.submit();
            }
        } ]
    });
}

/**
 * 编辑模型
 * @param url
 */
function modelEdit(modelId) {
    window.open("/activiti/model/update/"+modelId);
    /*parent.$.modalDialog({
        title : '编辑模型',
        width : 300,
        height : 200,
        href : '${ctx}/activiti/model/update/'+modelId,
        buttons : [ {
            text : '确定',
            handler : function() {
                parent.$.modalDialog.openner_dataGrid = processdefDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#processdefDeployForm');
                f.submit();
            }
        } ]
    });*/
    /*var newTab = $('#index_tabs', parent.document);
    newTab = $('#processdefToolbar');
    var title = "11";
    var url = "/activiti/model/update/"+modelId;
    if (newTab.tabs('exists', title)){
        newTab.tabs('select', title);
    } else {
        var content = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
        newTab.tabs('add',{
            title:title,
            content:content,
            border : false,
            closable : true,
            fit : true,
            iconCls : node.iconCls
        });
    }*/
}

/**
 * 流程部署
 * @param url
 */
function processDeploy(id) {
    if (id == undefined) {//点击右键菜单才会触发这个
        var rows = processdefDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {//点击操作里面的删除图标会触发这个
        processdefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    parent.$.messager.confirm('询问', '您是否要使用该模型部署流程？', function(b) {
        if (b) {
            progressLoad();
            $.post('${ctx}/activiti/model/deploy/'+id, {
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
}

/**
 * 查看详情
 */
function modelDetail(id) {
    if (id == undefined) {
        var rows = processdefDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
        processdefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    var contentStr= $.formatString('<img src="${ctx}/activiti/model/image/{0}"></img>',id);
    $("#tVacationGetProcessImage").window({
        title : '查看详情',
        width : 900,
        height : 500,
        content:contentStr,
        buttons : [ {
            text : '关闭',
            handler : function() {
                $("#tVacationGetProcessImage").dialog("close");
            }
        } ]
    });
}

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