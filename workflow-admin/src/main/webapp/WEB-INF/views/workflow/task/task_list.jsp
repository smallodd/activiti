<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
        <form id="taskSearchForm">
            <table>
                <tr>
                    <th>标题:</th>
                    <td><input name="title" placeholder="标题"/></td>
                    <th>业务主键:</th>
                    <td><input name="businessKey" placeholder="业务主键"/></td>
                    <th>申请人:</th>
                    <td><input name="creator" placeholder="申请人"/></td>
                    <th>当前审批人:</th>
                    <td><input name="assignee" placeholder="当前审批人"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton"
                           data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="taskSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton"
                           data-options="iconCls:'fi-x-circle',plain:true" onclick="taskCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>

    <div data-options="region:'center',border:false">
        <table id="taskDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="taskToolbar" style="display: none;">

</div>
<div id="showTaskWindow"></div>
<script type="text/javascript">
    var taskDataGrid;
    $(function () {
        taskDataGrid = $('#taskDataGrid').datagrid({
            url: '${ctx}/workflow/data/task',
            striped: true,
            rownumbers: true,
            pagination: true,
            singleSelect: true,
            fitColumns: true,
            idField: 'id',
            sortName: 'id',
            sortOrder: 'asc',
            pageSize: 20,
            pageList: [10, 20, 30, 40, 50, 100, 200, 300, 400, 500],
            columns: [[{
                width: '60',
                title: '主键',
                field: 'id',
                hidden: false
            }, {
                width: '100',
                title: '状态',
                field: 'taskState',
                sortable: true,
                formatter: function (value, row, index) {
                    switch (value) {
                        case '-1':
                            return '待签收';
                        case '0':
                            return '待受理';
                    }
                }
            }, {
                width: '100',
                title: '申请人',
                field: 'processOwner'
            }, {
                width: '350',
                title: '标题',
                field: 'businessName'
            }, {
                width: '140',
                title: '当前任务节点名称',
                field: 'taskName'
            }, {
                width: '100',
                title: '当前审批人',
                field: 'taskAssign',
                formatter: function (value, row, index) {
                    return $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-viewAssignee" data-options="plain:true,iconCls:\'fi-magnifying-glass icon-blue\'" onclick="viewTaskAssigneeFun(\'{0}\',\'{1}\');" >点击产看</a>', row.id, row.processDefinitionId);
                }
            }, {
                width: '140',
                title: '当前任务创建时间',
                field: 'taskCreateTime',
                sortable: true
            }, {
                width: '140',
                title: '业务主键',
                field: 'businessKey',

            }, {
                field: 'action',
                title: '操作',
                width: 350,
                formatter: function (value, row, index) {
                    var str = '';
                    <shiro:hasPermission name="/task/claim">
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-claimTask" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="claimTaskFun(\'{0}\',\'{1}\');" >签收</a>', row.id, row.processDefinitionId);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/task/unclaim">
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-unclaimTask" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="unclaimTaskFun(\'{0}\',\'{1}\');" >退签</a>', row.id, row.processDefinitionId);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/task/complete">
                        str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-complateTask" data-options="plain:true,iconCls:\'fi-monitor icon-purple\'" onclick="completeTaskFun(\'{0}\');" >办理</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/task/transfer">
                        str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-transferTask" data-options="plain:true,iconCls:\'fi-rewind-ten icon-red\'" onclick="transferTaskFun(\'{0}\');" >转办</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/task/jump">
                        str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-jumpTask" data-options="plain:true,iconCls:\'fi-share icon-yellow\'" onclick="jumpTaskFun(\'{0}\');" >跳转</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/ask/comment">
                        str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-askTask" data-options="plain:true,iconCls:\'fi-share icon-green\'" onclick="askTaskFun(\'{0}\');" >意见征询</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/task/progress">
                        str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                        str += $.formatString('<a href="javascript:void(0)" class="task-easyui-linkbutton-taskProgress" data-options="plain:true,iconCls:\'fi-arrow-right icon-grey\'" onclick="showTaskFun(\'{0}\', \'{1}\');" >进度</a>',row.processDefinitionId, row.processInstanceId);
                    </shiro:hasPermission>
                    return str;
                }
            }]],
            onLoadSuccess: function (data) {
                $('.task-easyui-linkbutton-claimTask').linkbutton({text: '签收'});
                $('.task-easyui-linkbutton-unclaimTask').linkbutton({text:'退签'});
                $('.task-easyui-linkbutton-complateTask').linkbutton({text: '办理'});
                $('.task-easyui-linkbutton-transferTask').linkbutton({text: '转办'});
                $('.task-easyui-linkbutton-jumpTask').linkbutton({text: '跳转'});
                $('.task-easyui-linkbutton-taskProgress').linkbutton({text: '进度'});
                $('.task-easyui-linkbutton-askTask').linkbutton({text: '意见征询'});
                $('.task-easyui-linkbutton-viewAssignee').linkbutton({text: '点击查看'});
            },
            toolbar: '#taskToolbar'
        });
    });


    /**
     * 办理任务
     */
    function completeTaskFun(id) {
        if (id == undefined) {
            var rows = taskDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            taskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title: '办理',
            width: 570,
            height: 450,
            href: '${ctx}/workflow/page/task/complete/' + id,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#completeTaskForm');
                    f.submit();
                }
            }]
        });
    }


    /**
     * 签收任务
     */
    function claimTaskFun1(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = taskDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            taskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否签收当前任务？', function (b) {
            if (b) {
                progressLoad();
                $.post('${ctx}/activiti/claimTask', {
                    id: id
                }, function (result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        taskDataGrid.datagrid('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

    /**
     * 退签任务
     */
    function claimTaskFun(taskId, procDefId) {
        if (taskId == undefined) {//点击右键菜单才会触发这个
            var rows = taskDataGrid.datagrid('getSelections');
            taskId = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            taskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title: '选择受理人',
            width: 500,
            height: 450,
            modal: true,
            href: '${ctx}/workflow/page/user/claim?taskId=' + taskId+"&claimType=1&procDefId="+procDefId,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;
                    var f = parent.$.modalDialog.handler.find('#taskClaimForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 退签任务
     */
    function unclaimTaskFun(taskId, procDefId) {
        if (taskId == undefined) {//点击右键菜单才会触发这个
            var rows = taskDataGrid.datagrid('getSelections');
            taskId = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            taskDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title: '选择受理人',
            width: 500,
            height: 450,
            modal: true,
            href: '${ctx}/workflow/page/user/claim?taskId=' + taskId+"&claimType=2&procDefId="+procDefId,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;
                    var f = parent.$.modalDialog.handler.find('#taskClaimForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 委派任务(同代办)
     */
    function delegateTaskFun(id) {
        parent.$.modalDialog({
            title: '选择受理人',
            width: 500,
            height: 450,
            modal: true,
            href: '${ctx}/activiti/taskDelegate',
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;
                    var f = parent.$.modalDialog.handler.find('#taskDelegateForm');
                    f.find("#taskId").val(id);
                    f.submit();
                }
            }]
        });
    }

    /**
     * 转办任务
     */
    function transferTaskFun(id) {
        parent.$.modalDialog({
            title: '选择受理人',
            width: 500,
            height: 450,
            modal: true,
            href: '${ctx}/workflow/page/task/transfer/' + id,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;
                    var f = parent.$.modalDialog.handler.find('#taskTransferForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 跳转任务
     */
    function jumpTaskFun(id) {
        parent.$.modalDialog({
            title: '选择任务节点',
            width: 300,
            height: 200,
            modal: true,
            href: '${ctx}/workflow/page/task/jump?taskId=' + id,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;
                    var f = parent.$.modalDialog.handler.find('#taskJumpForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     *意见征询
     */
    function askTaskFun(id) {
        parent.$.modalDialog({
            title: '意见征询',
            width: 500,
            height: 400,
            modal: true,
            href: '${ctx}/ask/comment?taskId=' + id,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_dataGrid = taskDataGrid;
                    var f = parent.$.modalDialog.handler.find('#taskJumpForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 查看任务进度
     */
    function showTaskFun(processDefinitionId, processInstanceId) {
        var contentStr = $.formatString('<iframe width="100%" height="100%" src="${ctx}/workflow/page/diagram?processDefinitionId={0}&processInstanceId={1}"></iframe>', processDefinitionId, processInstanceId);
        $("#showTaskWindow").window({
            title: '任务进度',
            width: 900,
            height: 500,
            content: contentStr,
            fit: "true",
            modal: true
        });
    }

    /**
     * 查看当前审批人
     */
    function viewTaskAssigneeFun(taskId){
        parent.$.modalDialog({
            title: '查看当前审批人',
            width: 333,
            height: 450,
            modal: true,
            href: '${ctx}/workflow/page/task/assignee/' + taskId
        });
    }

    /**
     * 清除
     */
    function taskCleanFun() {
        $('#taskSearchForm input').val('');
        taskDataGrid.datagrid('load', {});
    }

    /**
     * 搜索
     */
    function taskSearchFun() {
        taskDataGrid.datagrid('load', $.serializeObject($('#taskSearchForm')));
    }
</script>
</body>
</html>