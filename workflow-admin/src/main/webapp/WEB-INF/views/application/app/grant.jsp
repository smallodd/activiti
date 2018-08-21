<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>模型授权</title>
</head>
<body>
<div id="roleGrantLayout" class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'west'" title="流程模型" style="width: 300px; padding: 1px;">
        <div class="well well-small">
            <form id="modelGrantForm" method="post">
                <input name="id" type="hidden" value="${id}" readonly="readonly">
                <ul id="modelTree"></ul>
                <input id="modelKeys" name="modelKeys" type="hidden"/>
            </form>
        </div>
    </div>
    <div data-options="region:'center'" title="" style="overflow: hidden; padding: 10px;">
        <div>
            <button class="btn btn-success" onclick="checkAll();">全选</button>
            <br/> <br/>
            <button class="btn btn-warning" onclick="checkInverse();">反选</button>
            <br/> <br/>
            <button class="btn btn-inverse" onclick="uncheckAll();">取消</button>
        </div>
    </div>
</div>
<script type="text/javascript">
    var modelTree;
    $(function () {
        modelTree = $('#modelTree').tree({
            url: '${ctx}/activiti/model/allTrees',
            parentField: 'pid',
            lines: true,
            checkbox: true,
            onClick: function (node) {
            },
            onBeforeLoad:function(node,param){
                param.id = ${id};
            },
            onLoadSuccess: function (node, data) {
                progressLoad();
                $.post('${ctx}/app/findModelKeyListByAppId', {
                    id: '${id}'
                }, function (result) {
                    var maps;
                    if (result.success == true && result.obj != undefined) {
                        maps = result.obj;
                    }
                    for (var key in maps) {
                        var flag = maps[key];

                        if (modelTree.tree('find', key)) {
                            modelTree.tree('check', modelTree.tree('find', key).target);
                            $(modelTree.tree('find', key)).attr("flag", flag);
                            if (flag) {
                                $(modelTree.tree('find', key).target).unbind().click(function () {
                                    return false;
                                });
                            }
                        }
                    }

                }, 'json');
                progressClose();
            },
            cascadeCheck: false
        });

        $('#modelGrantForm').form({
            url: '${ctx}/app/grant',
            onSubmit: function () {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                var checknodes = modelTree.tree('getChecked');
                var ids = [];
                if (checknodes && checknodes.length > 0) {
                    for (var i = 0; i < checknodes.length; i++) {
                        ids.push(checknodes[i].id);
                    }
                }
                $('#modelKeys').val(ids);
                return isValid;
            },
            success: function (result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
    });

    function checkAll() {
        var nodes = modelTree.tree('getChecked', 'unchecked');
        if (nodes && nodes.length > 0) {

            for (var i = 0; i < nodes.length; i++) {
                modelTree.tree('check', nodes[i].target);
            }
        }
    }

    function uncheckAll() {
        var nodes = modelTree.tree('getChecked');
        if (nodes && nodes.length > 0) {
            debugger;
            for (var i = 0; i < nodes.length; i++) {
                var flag = nodes[i].flag;
                if (flag) {
                    continue;
                }
                modelTree.tree('uncheck', nodes[i].target);
            }
        }
    }

    function checkInverse() {
        var unchecknodes = modelTree.tree('getChecked', 'unchecked');
        var checknodes = modelTree.tree('getChecked');
        if (unchecknodes && unchecknodes.length > 0) {
            for (var i = 0; i < unchecknodes.length; i++) {
                modelTree.tree('check', unchecknodes[i].target);
            }
        }
        if (checknodes && checknodes.length > 0) {
            for (var i = 0; i < checknodes.length; i++) {
                var flag = checknodes[i].flag;
                if (flag) {
                    continue;
                }
                modelTree.tree('uncheck', checknodes[i].target);
            }
        }
    }
</script>
</body>
</html>