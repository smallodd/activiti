<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff">
        <form id="searchUserForm">
            <table>
                <tr>
                    <th>姓名:</th>
                    <td><input tepe="text" name="userName" placeholder="请输入用户姓名"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="searchUserFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="cleanUserFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div data-options="region:'center',border:true,title:'用户列表'" >
        <table id="userDataGrid" data-options="fit:true,border:false"></table>
    </div>
    <div data-options="region:'west',border:true,split:false,title:'组织机构'"  style="width:150px;">
        <ul id="departmentTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
</div>
<div id="userToolbar" style="display: none;">
    <shiro:hasPermission name="/sysUser/add">
        <a onclick="addUserFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加</a>
    </shiro:hasPermission>
</div>
<script type="text/javascript">
    var userDataGrid;
    var departmentTree;

    $(function() {
        departmentTree = $('#departmentTree').tree({
            url : '${ctx}/sysDepartment/tree',
            parentField : 'parentId',
            lines : true,
            onClick : function(node) {
                userDataGrid.datagrid('load', {
                    departmentId: node.id
                });
            }
        });

        userDataGrid = $('#userDataGrid').datagrid({
            url : '${ctx}/sysUser/dataGrid',
            fit : true,
            striped : true,
            rownumbers : true,
            pagination : true,
            fitColumns : true,
            singleSelect : true,
            idField : 'id',
            sortName : 'registerTime',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            columns : [ [ {
                width : '80',
                title : '登录名',
                field : 'loginName'
            }, {
                width : '80',
                title : '姓名',
                field : 'userName'
            },{
                width : '80',
                title : '部门ID',
                field : 'departmentId',
                hidden : true
            },{
                width : '80',
                title : '所属部门',
                field : 'departmentName'
            },{
                width : '200',
                title : '角色',
                field : 'roleList',
                formatter : function(value, row, index) {
                    var roles = [];
                    for(var i = 0; i< value.length; i++) {
                        roles.push(value[i].roleName);  
                    }
                    return(roles.join('\n'));
                }
            },{
                width : '130',
                title : '创建时间',
                field : 'registerTime'
            },{
                width : '50',
                title : '性别',
                field : 'userSex',
                formatter : function(value, row, index) {
                    switch (value) {
                    case '0':
                        return '男';
                    case '1':
                        return '女';
                    }
                }
            },{
                width : '120',
                title : '电话',
                field : 'userPhone'
            }, {
                width : '160',
                title : '邮箱',
                field : 'userEmail'
            }, 
            {
                width : '60',
                title : '用户类型',
                field : 'userType',
                formatter : function(value, row, index) {
                    if(value == '0') {
                        return "管理员";
                    }else if(value == '1') {
                        return "用户";
                    }
                    return "未知类型";
                }
            }/*,{
                width : '60',
                title : '状态',
                field : 'status',
                sortable : true,
                formatter : function(value, row, index) {
                    switch (value) {
                    case 0:
                        return '正常';
                    case 1:
                        return '停用';
                    }
                }
            }  */,{
                field : 'action',
                title : '操作',
                width : 200,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/sysUser/edit">
                            str += $.formatString('<a href="javascript:void(0)" class="user-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editUserFun(\'{0}\');" >编辑</a>', row.id);
                        </shiro:hasPermission>
                        <shiro:hasPermission name="/sysUser/delete">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="user-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteUserFun(\'{0}\');" >删除</a>', row.id);
                        </shiro:hasPermission>
                    return str;
                }
            }] ],
            onLoadSuccess:function(data){
                $('.user-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.user-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#userToolbar'
        });
    });
    
    function addUserFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 500,
            height : 300,
            href : '${ctx}/sysUser/addPage',
            buttons : [ {
                text : '添加',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = userDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#userAddForm');
                    f.submit();
                }
            } ]
        });
    }
    
    function deleteUserFun(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = userDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            userDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要删除当前用户？', function(b) {
            if (b) {
                var currentUserId = '${sessionInfo.id}';/*当前登录用户的ID*/
                if (currentUserId != id) {
                    progressLoad();
                    $.post('${ctx}/sysUser/delete', {
                        id : id
                    }, function(result) {
                        if (result.success) {
                            parent.$.messager.alert('提示', result.msg, 'info');
                            userDataGrid.datagrid('reload');
                        }
                        progressClose();
                    }, 'JSON');
                } else {
                    parent.$.messager.show({
                        title : '提示',
                        msg : '不可以删除自己！'
                    });
                }
            }
        });
    }
    
    function editUserFun(id) {
        if (id == undefined) {
            var rows = userDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            userDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '编辑',
            width : 500,
            height : 300,
            href : '${ctx}/sysUser/editPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = userDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#userEditForm');
                    f.submit();
                }
            } ]
        });
    }
    
    function searchUserFun() {
        userDataGrid.datagrid('load', $.serializeObject($('#searchUserForm')));
    }
    function cleanUserFun() {
        $('#searchUserForm input').val('');
        userDataGrid.datagrid('load', {});
    }
</script>
</body>
</html>