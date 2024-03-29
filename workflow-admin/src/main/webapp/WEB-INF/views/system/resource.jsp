<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>资源管理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false"  style="overflow: hidden;">
        <table id="resourceTreeGrid"></table>
    </div>
</div>
<div id="resourceToolbar" style="display: none;">
    <shiro:hasPermission name="/sysResource/add">
        <a onclick="addResourceFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加</a>
    </shiro:hasPermission>
</div>
<script type="text/javascript">
    var resourceTreeGrid;
    $(function() {
        resourceTreeGrid = $('#resourceTreeGrid').treegrid({
            url : '${ctx}/sysResource/treeGrid',
            idField : 'id',
            treeField : 'resourceName',
            parentField : 'parentId',
            fit : true,
            rownumbers : true,
            fitColumns : true,
            border : false,
            frozenColumns : [ [ {
                title : '资源ID',
                field : 'id',
                hidden:true,
                width : 50
            } ] ],
            columns : [ [ {
                field : 'resourceCode',
                title : '资源编码',
                width : 60
            },  {
                field : 'resourceName',
                title : '资源名称',
                width : 150
            }, {
                field : 'resourceUrl',
                title : '资源路径',
                width : 200
            }, /* {
                field : 'openMode',
                title : '打开方式',
                width : 60
            }, */ {
                field : 'sequence',
                title : '排序',
                width : 40
            }, {
                field : 'resourceIcon',
                title : '图标',
                width : 120
            }, {
                field : 'resourceType',
                title : '资源类型',
                width : 80,
                formatter : function(value, row, index) {
                    switch (value) {
                    case '0':
                        return '菜单';
                    case '1':
                        return '按钮';
                    }
                }
            }, {
                field : 'parentId',
                title : '上级资源ID',
                width : 150,
                hidden : true
            }/* , {
                field : 'status',
                title : '状态',
                width : 40,
                formatter : function(value, row, index) {
                    switch (value) {
                    case 0:
                        return '正常';
                    case 1:
                        return '停用';
                    }
                }
            } */, {
                field : 'action',
                title : '操作',
                width : 130,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/sysRsource/edit">
                            str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editResourceFun(\'{0}\');" >编辑</a>', row.id);
                        </shiro:hasPermission>
                        <shiro:hasPermission name="/sysRsource/delete">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteResourceFun(\'{0}\');" >删除</a>', row.id);
                        </shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.resource-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.resource-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#resourceToolbar'
        });
    });

    function editResourceFun(id) {
        if (id != undefined) {
            resourceTreeGrid.treegrid('select', id);
        }
        var node = resourceTreeGrid.treegrid('getSelected');
        if (node) {
            parent.$.modalDialog({
                title : '编辑',
                width : 500,
                height : 350,
                href : '${ctx}/sysResource/editPage?id=' + node.id,
                buttons : [ {
                    text : '确定',
                    handler : function() {
                        parent.$.modalDialog.openner_treeGrid = resourceTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                        var f = parent.$.modalDialog.handler.find('#resourceEditForm');
                        f.submit();
                    }
                } ]
            });
        }
    }

    function deleteResourceFun(id) {
        if (id != undefined) {
            resourceTreeGrid.treegrid('select', id);
        }
        var node = resourceTreeGrid.treegrid('getSelected');
        if (node) {
            parent.$.messager.confirm('询问', '您是否要删除当前资源？删除当前资源会连同子资源一起删除!', function(b) {
                if (b) {
                    progressLoad();
                    $.post('${ctx}/sysResource/delete', {
                        id : node.id
                    }, function(result) {
                        if (result.success) {
                            parent.$.messager.alert('提示', result.msg, 'info');
                            resourceTreeGrid.treegrid('reload');
                            parent.layout_west_tree.tree('reload');
                        }
                        progressClose();
                    }, 'JSON');
                }
            });
        }
    }

    function addResourceFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 400,
            height : 400,
            href : '${ctx}/sysResource/addPage',
            buttons : [ {
                text : '添加',
                handler : function() {
                    parent.$.modalDialog.openner_treeGrid = resourceTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#resourceAddForm');
                    f.submit();
                }
            } ]
        });
    }
</script>
</body>
</html>