<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>部门管理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false"  style="overflow: hidden;">
        <table id="departmentTreeGrid"></table>
    </div>
    <div id="orgToolbar" style="display: none;">
        <shiro:hasPermission name="/sysDepartment/add">
            <a onclick="addDepartmentFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加</a>
        </shiro:hasPermission>
    </div>
</div>
<script type="text/javascript">
    var departmentTreeGrid;
    $(function() {
        departmentTreeGrid = $('#departmentTreeGrid').treegrid({
            url : '${ctx}/sysDepartment/treeGrid',
            idField : 'id',
            treeField : 'departmentName',
            parentField : 'parentId',
            fit : true,
            rownumbers : true,
            fitColumns : false,
            border : false,
            autoRowHeight :false,
            columns : [ [ {
                title : 'id',
                field : 'id',
                width : 40,
                hidden : true
            } ] ],
            columns : [ [ {
                field : 'departmentCode',
                title : '部门编号',
                width : 120
            },{
                field : 'departmentName',
                title : '部门名称',
                width : 280
            }, {
                field : 'sequence',
                title : '排序',
                width : 40
            }, {
                field : 'departmentIcon',
                title : '图标',
                width : 120
            }, {
                width : '130',
                title : '创建时间',
                field : 'createTime'
            },{
                field : 'parentId',
                title : '上级资源ID',
                width : 150,
                hidden : true
            }, {
                field : 'description',
                title : '描述',
                width : 200
            }, {
                field : 'action',
                title : '操作',
                width : 200,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/sysDepartment/edit">
                            str += $.formatString('<a href="javascript:void(0)" class="department-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editDepartmentFun(\'{0}\');" >编辑</a>', row.id);
                        </shiro:hasPermission>
                        <shiro:hasPermission name="/sysDepartment/delete">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="department-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteDepartmentFun(\'{0}\');" >删除</a>', row.id);
                        </shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.department-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.department-easyui-linkbutton-del').linkbutton({text:'删除'});
                $('#departmentTreeGrid').treegrid('collapseAll');
            },
            toolbar : '#orgToolbar'
        });
    });
    
    function editDepartmentFun(id) {
        if (id != undefined) {
            departmentTreeGrid.treegrid('select', id);
        }
        var node = departmentTreeGrid.treegrid('getSelected');
        if (node) {
            parent.$.modalDialog({
                title : '编辑',
                width : 380,
                height : 380,
                href : '${ctx}/sysDepartment/editPage?id=' + node.id,
                buttons : [ {
                    text : '编辑',
                    handler : function() {
                        parent.$.modalDialog.openner_treeGrid = departmentTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                        var f = parent.$.modalDialog.handler.find('#departmentEditForm');
                        f.submit();
                    }
                } ]
            });
        }
    }
    
    function deleteDepartmentFun(id) {
        if (id != undefined) {
            departmentTreeGrid.treegrid('select', id);
        }
        var node = departmentTreeGrid.treegrid('getSelected');
        if (node) {
            parent.$.messager.confirm('询问', '您是否要删除当前资源？删除当前资源会连同子资源一起删除!', function(b) {
                if (b) {
                    progressLoad();
                    $.post('${ctx}/sysDepartment/delete', {
                        id : node.id
                    }, function(result) {
                        if (result.success) {
                            parent.$.messager.alert('提示', result.msg, 'info');
                            departmentTreeGrid.treegrid('reload');
                        }else{
                            parent.$.messager.alert('提示', result.msg, 'info');
                        }
                        progressClose();
                    }, 'JSON');
                }
            });
        }
    }
    
    function addDepartmentFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 380,
            height : 380,
            href : '${ctx}/sysDepartment/addPage',
            buttons : [ {
                text : '添加',
                handler : function() {
                    parent.$.modalDialog.openner_treeGrid = departmentTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#departmentAddForm');
                    f.submit();
                }
            } ]
        });
    }
</script>
</body>
</html>