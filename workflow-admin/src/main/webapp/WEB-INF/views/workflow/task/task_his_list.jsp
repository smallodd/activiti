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
            url: '${ctx}/workflow/data/task/his',
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
                hidden: true
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
                        case '1':
                            return '同意';
                        case '2':
                            return '拒绝';
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
                title: '任务节点名称',
                field: 'taskName'
            }, {
                width: '200',
                title: '审批人',
                field: 'taskAssign'
            }, {
                width: '140',
                title: '创建时间',
                field: 'taskCreateTime',
                sortable: true
            }, {
                width: '140',
                title: '业务主键',
                field: 'businessKey',

            }]],
            onLoadSuccess: function (data) {

            },
            toolbar: '#taskToolbar'
        });
    });

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