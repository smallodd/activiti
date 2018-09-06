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
                    <th>流程定义KEY/名称:</th>
                    <td><input name="nameOrKey" placeholder="流程定义KEY/名称"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="processDefSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="processDefCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div data-options="region:'center',border:true,title:'流程定义列表'">
        <table id="processDefDataGrid" data-options="fit:true,border:false"></table>
    </div>
    <div data-options="region:'west',border:true,split:false,title:'应用系统'"  style="width:150px;">
        <ul id="appTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
</div>
<div id="processDetailWindow"></div>
<div id="processDefToolbar" style="display: none;">
    <shiro:hasPermission name="/activiti/deploy">
        <a onclick="processDefDeployFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-upload icon-green'">流程部署</a>
    </shiro:hasPermission>
</div>
<div id="configSelect">
    <input type="hidden" id="processDefinitionId"/>
    <a id="btn1" href="javascript:configAssigneeFun_(1)" class="easyui-linkbutton" data-options="iconCls:'fi-widget'" style="margin: 10px;width: 120px;height: 50px;">标准设置</a>
    <a id="btn2" href="javascript:configAssigneeFun_(2)" class="easyui-linkbutton" data-options="iconCls:'fi-wrench'" style="margin: 10px;width: 120px;height: 50px;">快速设置</a>
</div>
<script type="text/javascript">
    var processDefDataGrid;
    var appTree;
    $(function() {
        appTree = $('#appTree').tree({
            url : '${ctx}/app/dataGrid',
            //parentField : '',
            lines : true,
            loadFilter:function(data){
                //过滤操作
                $.each(data, function(index, node){
                    node.iconCls = "fi-paperclip";
                })
                return data;
            },
            formatter: function (node){
                return node.name;
            },
            onClick : function(node) {
                processDefDataGrid.datagrid('load', {
                    appKey: node.key
                });
            }
        });

        processDefDataGrid = $('#processDefDataGrid').datagrid({
            url : '${ctx}/workflow/data/process/def/list',
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            idField : 'id',
            sortName : 'id',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200],
            columns : [[{
                width : '200',
                title : '流程定义ID',
                field : 'id',
                sortable : true
            }, {
                width : '200',
                title : '流程定义名称',
                field : 'name',
                formatter : function(value, row, index){
                    var str = $.formatString('<a target="_blank" href="javascript:processDetail(\'{0}\')">{1}</a>', row.id, row.name==null?"|+|+|+|+|+|":row.name);
                    return str;
                }
            }, {
                width : '200',
                title : '流程定义KEY',
                field : 'key'
            }, {
                width : '30',
                title : '版本',
                field : 'version'
            }, {
                width : '200',
                title : '资源名称',
                field : 'resourceName',
                formatter : function(value, row, index){
                    var str = $.formatString('<a target="_blank" href="${ctx}/activiti/getProcessResource?resourceType=xml&pdid={0}">{1}</a>', row.id ,row.resourceName);
                    return str;
                }
            }, {
                width : '130',
                title : '部署时间',
                field : 'deployTime'
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
                width : 'auto',
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
                        str += $.formatString('<a href="javascript:void(0)" class="processdef-easyui-linkbutton-start" data-options="plain:true,iconCls:\'fi-play icon-green\'" onclick="startProcessInstance(\'{0}\');" >模拟开启</a>', row.id);
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

    /**
     * 开启流程实例
     */
    function startProcessInstance(processDefinitionId){
        parent.$.modalDialog({
            title : '开启流程',
            width : 400,
            height : 300,
            href : '${ctx}/workflow/page/process/start/'+processDefinitionId,
            buttons : [ {
                text : '开启流程',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = processDefDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#processStartForm');
                    f.submit();
                }
            } ]
        });
    }

    /**
     * 设定人员-选择
     */
    function configAssigneeFun(processDefinitionId) {
        if (processDefinitionId == undefined) {
            var rows = processDefDataGrid.datagrid('getSelections');
            processDefinitionId = rows[0].id;
        } else {
            processDefDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        $("#processDefinitionId").val(processDefinitionId);

        $.post("${ctx}/assignee/config/type?processDefinitionId=" + processDefinitionId, function(result){
            if(JSON.stringify(result) > 0){
                configAssigneeFun_(3);
            }else{
                $("#configSelect").dialog({
                    title : '设定人员选择',
                    width : 300,
                    height : 120,
                });
            }
        });
    }

    /**
     * 设定人员
     */
    function configAssigneeFun_(type) {
        if(1==type || 2==type){
            $("#configSelect").dialog("close");
        }

        var processDefinitionId = $("#processDefinitionId").val();
        parent.$.modalDialog({
            title : '设定人员',
            width : 960,
            height : 450,
            href :  '${ctx}/assignee/config/page/' + processDefinitionId+"?type="+type,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = processDefDataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#configAssigneeForm');
                    f.submit();
                }
            }]
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
     * 查看流程图
     */
    function processDetail(processDefinitionId) {
        var contentStr = $.formatString('<iframe width="100%" height="100%" src="${ctx}/workflow/page/diagram?processDefinitionId={0}"></iframe>', processDefinitionId);
        $("#processDetailWindow").window({
            title: '任务进度',
            width: 900,
            height: 500,
            content: contentStr,
            fit: "true",
            modal: true
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