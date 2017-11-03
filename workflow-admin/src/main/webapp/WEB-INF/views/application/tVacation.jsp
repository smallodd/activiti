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
        <form id="tVacationSearchForm">
            <table>
                <tr>
                    <th>名称:</th>
                    <td><input name="name" placeholder="搜索条件"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="tVacationSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="tVacationCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
     </div>
 
    <div data-options="region:'center',border:false">
        <table id="tVacationDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="tVacationToolbar" style="display: none;">
    <shiro:hasPermission name="/tVacation/add">
        <a onclick="tVacationAddFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">发起申请</a>
    </shiro:hasPermission>
</div>
<div id="tVacationGetComments"></div>
<div id="tVacationGetProcessImage"></div>
<script type="text/javascript">
    var tVacationDataGrid;
    $(function() {
        tVacationDataGrid = $('#tVacationDataGrid').datagrid({
        url : '${ctx}/tVacation/dataGrid',
        striped : true,
        rownumbers : true,
        fitColumns:true,
        pagination : true,
        singleSelect : true,
        idField : 'id',
        sortName : 'applyDate',
        sortOrder : 'desc',
        pageSize : 20,
        pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500],
        columns : [ [ {
            width : '60',
            title : '编号',
            field : 'id',
            hidden : true,
            sortable : true
        }, {
            width : '120',
            title : '请假单号',
            field : 'vacationCode',
            sortable : true
        }, {
            width : '100',
            title : '请假类型',
            field : 'vacationType',
            formatter : function(value, row, index) {
                switch (value) {
                case 1:
                    return '事假';
                case 2:
                    return '病假';
                }
            }
        }, {
            width : '140',
            title : '开始日期',
            field : 'beginDate'
        }, {
            width : '80',
            title : '请假天数',
            field : 'workDays'
        }, {
            width : '140',
            title : '结束日期',
            field : 'endDate'
        }, {
            width : '140',
            title : '申请日期',
            field : 'applyDate'
        }, {
            width : '200',
            title : '请假原因',
            field : 'vacationReason'
        }, {
            width : '140',
            title : '审批状态',
            field : 'vacationStatus',
            sortable : true,
            formatter : function(value, row, index) {
                switch (value) {
                case 1:
                    return '正在审批';
                case 2:
                    return '审批通过';
                case 3:
                    return '审批不通过';
                }
            }
        }, {
            field : 'action',
            title : '操作',
            width : 200,
            formatter : function(value, row, index) {
                var str = '';
               	<shiro:hasPermission name="/tVacation/getComments">
                    str += $.formatString('<a href="javascript:void(0)" class="tVacation-easyui-linkbutton-getComments" data-options="plain:true,iconCls:\'fi-list icon-blue\'" onclick="tVacationGetCommentsFun(\'{0}\');" >查看审批进度</a>', row.id);
                </shiro:hasPermission>
                <shiro:hasPermission name="/tVacation/getProcessImage">
                	str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                	str += $.formatString('<a href="javascript:void(0)" class="tVacation-easyui-linkbutton-getProcessImage" data-options="plain:true,iconCls:\'fi-magnifying-glass icon-green\'" onclick="tVacationGetProcessImageFun(\'{0}\');" >查看流程图</a>', row.id);
            	</shiro:hasPermission>
                return str;
            }
        } ] ],
        onLoadSuccess:function(data){
            $('.tVacation-easyui-linkbutton-getComments').linkbutton({text:'查看审批进度'});
            $('.tVacation-easyui-linkbutton-getProcessImage').linkbutton({text:'查看流程图'});
        },
        toolbar : '#tVacationToolbar'
    });
});

/**
 * 添加框
 * @param url
 */
function tVacationAddFun() {
    parent.$.modalDialog({
        title : '添加',
        width : 400,
        height : 350,
        href : '${ctx}/tVacation/addPage',
        buttons : [ {
            text : '确定',
            handler : function() {
                parent.$.modalDialog.openner_dataGrid = tVacationDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#tVacationAddForm');
                f.submit();
            }
        } ]
    });
}

/**
 * 查看审批进度
 */
function tVacationGetCommentsFun(id) {
    if (id == undefined) {
        var rows = tVacationDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
        tVacationDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    $("#tVacationGetComments").dialog({
        title : '查看审批进度',
        width : 700,
        height : 400,
        href :  '${ctx}/tVacation/tVacationGetCommentsPage?id=' + id,
        buttons : [ {
            text : '关闭',
            handler : function() {
            	$("#tVacationGetComments").dialog("close");
            }
        } ]
    });
}

/**
 * 查看流程图
 */
function tVacationGetProcessImageFun(id) {
    if (id == undefined) {
        var rows = tVacationDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
        tVacationDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    var contentStr= $.formatString('<img src="${ctx}/tVacation/tVacationGetProcessImage?id={0}"></img>',id);
    $("#tVacationGetProcessImage").window({
        title : '查看流程图',
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
function tVacationCleanFun() {
    $('#tVacationSearchForm input').val('');
    tVacationDataGrid.datagrid('load', {});
}

/**
 * 搜索
 */
function tVacationSearchFun() {
     tVacationDataGrid.datagrid('load', $.serializeObject($('#tVacationSearchForm')));
}
</script>
</body>
</html>