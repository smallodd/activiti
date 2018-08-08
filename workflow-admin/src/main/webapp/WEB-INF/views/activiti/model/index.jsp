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
        <form id="modelSearchForm">
            <table>
                <tr>
                    <th>模型名称:</th>
                    <td><input name="name" placeholder="搜索条件"/></td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-magnifying-glass',plain:true" onclick="modelSearchFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-x-circle',plain:true" onclick="modelCleanFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
     </div>

    <div data-options="region:'center',border:false" id="dd">
        <table id="modelDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="modelToolbar" style="display: none;">
    <shiro:hasPermission name="/activiti/model/create">
        <a onclick="modelCreate();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="/activiti/model/create">
        <a href="javascript:exportModel()" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-upload icon-green'">导出模型</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="/activiti/model/create">
        <a href="javascript:importModel();" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-download icon-green'">导入模型</a>
    </shiro:hasPermission>
</div>
<div id="modelImage"></div>
<div id="modelImportDiv" style="display: none">
    <div data-options="region:'center',border:false" style="overflow: hidden;padding: 3px;" >
        <form id="importModelForm" method="POST" enctype="multipart/form-data">
            <table class="grid">
                <tr>
                    <td><input class="easyui-filebox" style="width:260px;height:29px;" name="modelFile" id="modelFile" data-options="prompt:'请选择要导入的文件',buttonText:'选择文件',accept:'application/json'"/></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    var modelDataGrid;
    $(function() {
        modelDataGrid = $('#modelDataGrid').datagrid({
            url : '${ctx}/activiti/model/modelDataGrid',
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : false,
            idField : 'id',
            sortName : 'id',
            sortOrder : 'asc',
            checkOnSelect: true,
            selectOnCheck: true,
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500],
            columns : [ [ {
                width : '30',
                title : "",
                field : 'ck',
                checkbox : true
            }, {
                width : '100',
                title : '流程模型ID',
                field : 'id'

            }, {
                width : '250',
                title : '流程模型名称',
                field : 'name'
            }, {
                width : '200',
                title : '流程模型KEY',
                field : 'key'
            }, {
                width : '60',
                title : '模型版本',
                field : 'version'
            },{
                width : '140',
                title : '最后修改时间',
                field : 'lastUpdateTime',
                formatter:function(value,row,index){
                    var unixTimestamp = new Date(value);
                    return unixTimestamp.toLocaleString();
                }
            },{
                width : '140',
                title : '部署id',
                field : 'deploymentId',
                hidden:true
            }, {
                field : 'action',
                title : '操作',
                width : 300,
                formatter : function(value, row, index) {
                    var str = '';

                    <shiro:hasPermission name="/activiti/model/edit">
                        str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="modelEdit(\'{0}\');" >编辑</a>', row.id);
                    </shiro:hasPermission>

                    <shiro:hasPermission name="/activiti/model/deploy">
                        str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-sleep" data-options="plain:true,iconCls:\'fi-upload icon-blue\'" onclick="processDeploy(\'{0}\');" >部署</a>', row.id);
                    </shiro:hasPermission>

                    <shiro:hasPermission name="/activiti/model/detail">
                        str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-active" data-options="plain:true,iconCls:\'fi-magnifying-glass icon-blue\'" onclick="modelDetail(\'{0}\');" >详情</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/activiti/model/copy">
                    str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-copy" data-options="plain:true,iconCls:\'fi-page-copy icon-blue\'" onclick="modelCopy(\'{0}\');" >复制</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/activiti/model/resetKey">
                    if(row.deploymentId==null||row.deploymentId==""){
                        str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-reset" data-options="plain:true,iconCls:\'fi-paperclip icon-blue\'" onclick="modelResetKey(\'{0}\');" >重置key</a>', row.id);
                    }
                    </shiro:hasPermission>

                    <shiro:hasPermission name="/activiti/model/delete">
                        str += $.formatString('<a href="javascript:void(0)" class="model-easyui-linkbutton-delete" data-options="plain:true,iconCls:\'fi-paperclip icon-blue\'" onclick="deleteModel(\'{0}\');" >删除</a>', row.id);
                    </shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.model-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.model-easyui-linkbutton-sleep').linkbutton({text:'部署'});
                $('.model-easyui-linkbutton-active').linkbutton({text:'详情'});
                $('.model-easyui-linkbutton-copy').linkbutton({text:'复制'});
                $('.model-easyui-linkbutton-reset').linkbutton({text:'重置key'});
                $('.model-easyui-linkbutton-delete').linkbutton({text:'删除'});
            },
            toolbar : '#modelToolbar'
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
                parent.$.modalDialog.openner_dataGrid = modelDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                var f = parent.$.modalDialog.handler.find('#modelAddForm');
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
    //window.open("/activiti/model/update/"+modelId);
    var url = "/activiti/model/update/"+modelId;
    var content = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
    var currTab =  self.parent.$('#index_tabs').tabs('getSelected'); //获得当前tab
    self.parent.$('#index_tabs').tabs('update', {
        tab : currTab,
        options : {
            content : content
        }
    });
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
    function modelResetKey(id){
        parent.$.modalDialog({
            title : '创建流程模型',
            width : 500,
            height : 300,
            href : '${ctx}/activiti/model/resetKey/'+id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = modelDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#modelAddForm');
                    f.submit();
                }
            } ]
    });
}
  function  modelCopy(id){
      parent.$.modalDialog({
          title : '创建流程模型',
          width : 500,
          height : 300,
          href : '${ctx}/activiti/model/copyPage/'+id,
          buttons : [ {
              text : '确定',
              handler : function() {
                  parent.$.modalDialog.openner_dataGrid = modelDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                  var f = parent.$.modalDialog.handler.find('#modelAddForm');
                  f.submit();
              }
          } ]
      });
      <%--parent.$.messager.confirm('询问', '您是否要复制该模型流程？复制后会出现一条完全一模一样的数据！', function(b) {--%>
          <%--if (b) {--%>
              <%--progressLoad();--%>
              <%--$.post('${ctx}/activiti/model/copy/'+id, {--%>
                  <%--id : id--%>
              <%--}, function(result) {--%>
                  <%--if (result.success) {--%>
                      <%--parent.$.messager.alert('提示', result.msg, 'info');--%>
                      <%--modelDataGrid.datagrid('reload');--%>
                  <%--} else {--%>
                      <%--parent.$.messager.alert('错误', result.msg, 'error');--%>
                  <%--}--%>
                  <%--progressClose();--%>
              <%--}, 'JSON');--%>
          <%--}--%>
      <%--});--%>
  }
    /**
     * 流程部署
     * @param url
     */
    function deleteModel(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = modelDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            modelDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '确定删除该模型？如果部署过删除后会有不可预知的错误，确定删除吗？', function(b) {
            if (b) {
                progressLoad();
                $.post('${ctx}/activiti/model/deleteModel', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        modelDataGrid.datagrid('reload');
                    } else {
                        parent.$.messager.alert('错误', result.msg, 'error');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }
/**
 * 流程部署
 * @param url
 */
function processDeploy(id) {
    if (id == undefined) {//点击右键菜单才会触发这个
        var rows = modelDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {//点击操作里面的删除图标会触发这个
        modelDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    parent.$.messager.confirm('询问', '您是否要使用该模型部署流程？', function(b) {
        if (b) {
            progressLoad();
            $.post('${ctx}/activiti/model/deploy/'+id, {
                id : id
            }, function(result) {
                if (result.success) {
                    parent.$.messager.alert('提示', result.msg, 'info');
                    modelDataGrid.datagrid('reload');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
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
        var rows = modelDataGrid.datagrid('getSelections');
        id = rows[0].id;
    } else {
        modelDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
    }
    var contentStr = $.formatString('<img src="${ctx}/activiti/model/image/{0}"></img>',id);
    $("#modelImage").window({
        title : '查看详情',
        width : 900,
        height : 500,
        content : contentStr,
        modal : true,
        buttons : [ {
            text : '关闭',
            handler : function() {
                $("#modelImage").dialog("close");
            }
        } ]
    });
}

/**
 * 导出模型
 */
function exportModel(){
    var selections = $('#modelDataGrid').datagrid('getSelections');
    if(selections == undefined || selections.length <= 0){
        parent.$.messager.alert('提示', "请选择要导出的模型", 'info');
        return;
    }
    progressLoad();
    var modelIds = new Array();
    $(selections).each(function(index, element){
        modelIds.push(element.id);
    });
    window.location.href = "/activiti/model/export?modelIds="+modelIds.join(",");
    progressClose();
}

/**
 * 导入模型
 */
function importModel(){
    $("#modelImportDiv").dialog({
        title : '导入模型',
        width : 300,
        height : 135,
        content : "",
        modal : true,
        buttons : [ {
            text : '确定',
            handler : function() {
                var file = $("#modelFile").filebox('getValue');
                if(file == null || file == ""){
                    parent.$.messager.alert('错误', "请选择文件", 'error');
                    return;
                }
                $("#importModelForm").submit();
            }
        } ]
    });
}

$('#importModelForm').form({
    url: "/activiti/model/import",
    method : "POST",
    onSubmit: function () {
        progressLoad();
        var isValid = $(this).form('validate');
        if (!isValid) {
            progressClose();
        }
        return isValid;
    },
    success: function (result) {
        progressClose();
        result = $.parseJSON(result);
        if (result.success) {
            //清除文件空间中所选文件
            $("#modelFile").filebox('clear');
            //关闭窗口
            $("#modelImportDiv").dialog("close");

            //之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
            modelDataGrid.datagrid('reload');
        } else {
            parent.$.messager.alert('错误', result.msg, 'error');
        }
    }
});

/**
 * 清除
 */
function modelCleanFun() {
    $('#modelSearchForm input').val('');
    modelDataGrid.datagrid('load', {});
}

/**
 * 搜索
 */
function modelSearchFun() {
    modelDataGrid.datagrid('load', $.serializeObject($('#modelSearchForm')));
}

</script>
</body>
</html>