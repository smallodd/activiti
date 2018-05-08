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
        <form id="processDefSearchForm">
            <table>
                <tr>
                    <th>流程定义KEY:</th>
                    <td><input name="key" placeholder="搜索条件"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="processDefSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="processDefCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>

    <div data-options="region:'center',border:true,title:''">
        <table id="processDefDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="processDefToolbar" style="display: none;">
    <shiro:hasPermission name="/activiti/deploy">
        <a onclick="processDefDeployFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-upload icon-green'">流程部署</a>
    </shiro:hasPermission>
</div>
<script type="text/javascript">
    var processDefDataGrid;
    $(function() {
        processDefDataGrid = $('#processDefDataGrid').datagrid({
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
                width : '50',
                title : '版本',
                field : 'version'
            }, {
                width : '130',
                title : '部署时间',
                field : 'deployTime'
            }, {
                width : '140',
                title : '资源名称',
                field : 'resourceName',
                formatter : function(value, row, index){
                    var str = $.formatString('<a target="_blank" href="${ctx}/activiti/getProcessResource?type=xml&pdid={0}">{1}</a>', row.id ,row.resourceName);
                    return str;
                }
            }, {
                width : '140',
                title : '流程图片名称',
                field : 'imageName',
                formatter : function(value, row, index){
                    var str = $.formatString('<a target="_blank" href="${ctx}/activiti/getProcessResource?type=image&pdid={0}">{1}</a>', row.id ,row.imageName);
                    return str;
                }
            }, {
                width : '80',
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
                width : 250,
                formatter : function(value, row, index) {
                    var str = '';
                    if(row.suspended==='1'){
                        <shiro:hasPermission name="/activiti/sleep">
                        str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-sleep" data-options="plain:true,iconCls:\'fi-stop icon-blue\'" onclick="processDefSleepFun(\'{0}\');" >挂起</a>', row.id);
                        </shiro:hasPermission>
                    }
                    if(row.suspended==='2'){
                        <shiro:hasPermission name="/activiti/active">
                        str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-active" data-options="plain:true,iconCls:\'fi-play-circle icon-green\'" onclick="processDefActiveFun(\'{0}\');" >激活</a>', row.id);
                        </shiro:hasPermission>
                    }
                    <shiro:hasPermission name="/assignee/config/page">
                        str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-configUser" data-options="plain:true,iconCls:\'fi-torsos-male-female icon-green\'" onclick="configAssigneeFun(\'{0}\');" >设定人员</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/task/start">
                        str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-start" data-options="plain:true,iconCls:\'fi-torsos-male-female icon-green\'" onclick="startProcessInstance(\'{0}\');" >模拟开启</a>', row.key);
                    </shiro:hasPermission>
                    return str;
                }
            }]],
            onLoadSuccess:function(data){
                $('.processdef-easyui-linkbutton-sleep').linkbutton({text:'挂起'});
                $('.processdef-easyui-linkbutton-active').linkbutton({text:'激活'});
                $('.processdef-easyui-linkbutton-configUser').linkbutton({text:'设定人员'});
                $('.processdef-easyui-linkbutton-start').linkbutton({text:'模拟开启'});
            },
            toolbar : '#processDefToolbar'
        });
    });

    function startProcessInstance(processKey){
        $.ajax({
            type: 'POST',
            dataType : 'json',
            url: '${ctx}/activiti/startTask',
            data: {"processKey":processKey},
            success: function(json){
                parent.$.messager.alert('提示', json.msg, 'info');
            }
        });
    }
    /**
     * 设定人员
     */
    function configAssigneeFun(id) {
        if (id == undefined) {
            var rows = processDefDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            processDefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '设定人员',
            width : 800,
            height : 450,
            href :  '${ctx}/assignee/config/page/' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = processDefDataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#configAssigneeForm');
                    f.submit();
                }
            } ]
        });
    }

    /**
     * 流程部署
     * @param url
     */
    function processDefDeployFun() {
        parent.$.modalDialog({
            title : '流程部署',
            width : 300,
            height : 200,
            href : '${ctx}/activiti/deployPage',
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = processDefDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#processDefDeployForm');
                    f.submit();
                }
            } ]
        });
    }

    /**
     * 挂起
     */
    function processDefSleepFun(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = processDefDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            processDefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要挂起当前流程？', function(b) {
            if (b) {
                progressLoad();
                $.post('${ctx}/activiti/sleep', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        processDefDataGrid.datagrid('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

    /**
     * 激活
     */
    function processDefActiveFun(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = processDefDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            processDefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要激活当前流程？', function(b) {
            if (b) {
                progressLoad();
                $.post('${ctx}/activiti/active', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        processDefDataGrid.datagrid('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

    /**
     * 清除
     */
    function processDefCleanFun() {
        $('#processDefSearchForm input').val('');
        processDefDataGrid.datagrid('load', {});
    }

    /**
     * 搜索
     */
    function processDefSearchFun() {
        processDefDataGrid.datagrid('load', $.serializeObject($('#processDefSearchForm')));
    }
</script>
</body>
</html>