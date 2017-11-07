<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>资源管理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false"  style="overflow: hidden;">
        <table id="appListGrid"></table>
    </div>
</div>
<div id="roleToolbar" style="display: none;">
    <shiro:hasPermission name="/app/add">
        <a onclick="addAppFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加</a>
    </shiro:hasPermission>
</div>
<script type="text/javascript">
    var appListGrid;
    $(function() {
        appListGrid = $('#appListGrid').datagrid({
            url : '${ctx}/app/dataGrid',
            idField : 'id',
            fit : true,
            rownumbers : true,
            fitColumns : true,
            border : false,
            singleSelect : true,
            frozenColumns : [ [ {
                title : '应用ID',
                field : 'id',
                hidden:true,
                width : 50
            } ] ],
            columns : [ [ {
                field : 'name',
                title : '应用名称',
                width : 60
            }, {
                field : 'key',
                title : '应用KEY',
                width : 80
            },{
                field : 'creator',
                title : '应用创建人',
                width : 60
            },{
                field : 'updater',
                title : '应用更新人',
                width : 60
            },{
                field : 'description',
                title : '应用描述',
                width : 60
            },{
                field : 'createTime',
                title : '应用创建时间',
                width : 60
            },{
                field : 'updateTime',
                title : '应用更新时间',
                width : 60
            },{
                field : 'action',
                title : '操作',
                width : 130,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/app/edit">
                            str += $.formatString('<a href="javascript:void(0)" class="app-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editAppFun(\'{0}\');" >编辑</a>', row.id);
                        </shiro:hasPermission>
                        <shiro:hasPermission name="/app/delete">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="app-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteAppFun(\'{0}\');" >删除</a>', row.id);
                        </shiro:hasPermission>
                        <shiro:hasPermission name="/app/modelManage">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="app-easyui-linkbutton-model-edit" data-options="plain:true,iconCls:\'fi-widget icon-blue\'" onclick="grantModelFun(\'{0}\');" >模型管理</a>', row.id);
                        </shiro:hasPermission>
                        //<shiro:hasPermission name="/app/delete">
                            //str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            //str += $.formatString('<a href="javascript:void(0)" class="app-easyui-linkbutton-model-list" data-options="plain:true,iconCls:\'fi-magnifying-glass icon-green\'" onclick="deleteAppFun(\'{0}\');" >查看模型</a>', row.id);
                        //</shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.app-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.app-easyui-linkbutton-del').linkbutton({text:'删除'});
                $('.app-easyui-linkbutton-model-edit').linkbutton({text:'模型管理'});
                //$('.app-easyui-linkbutton-model-list').linkbutton({text:'查看模型'});
            },
            toolbar : '#roleToolbar'
        });
    });

    function editAppFun(id) {
        if (id == undefined) {
            var rows = appListGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            appListGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '编辑',
            width : 500,
            height : 350,
            href : '${ctx}/app/editPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_datagrid = appListGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#appEditForm');
                    f.submit();
                }
            } ]
        });
    }

    function deleteAppFun(id) {
        if (id == undefined) {
            var rows = appListGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            appListGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要删除当前应用？', function(b) {
            if (b) {
                progressLoad();
                $.post('${ctx}/app/delete', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        appListGrid.datagrid('reload');
                        parent.layout_west_tree.tree('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

    function addAppFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 400,
            height : 300,
            href : '${ctx}/app/addPage',
            buttons : [ {
                text : '添加',
                handler : function() {
                    parent.$.modalDialog.openner_datagrid = appListGrid;
                    var f = parent.$.modalDialog.handler.find('#appAddForm');
                    f.submit();
                }
            } ]
        });
    }

    function grantModelFun(id) {
        if (id == undefined) {
            var rows = appListGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            appListGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }

        parent.$.modalDialog({
            title : '授权',
            width : 500,
            height : 500,
            href : '${ctx}/app/grantPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = appListGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#modelGrantForm');
                    f.submit();
                }
            } ]
        });
    }
</script>
</body>
</html>