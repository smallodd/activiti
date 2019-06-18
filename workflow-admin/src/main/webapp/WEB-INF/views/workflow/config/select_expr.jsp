<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta charset="UTF-8">
        <title>表达式管理</title>
    </head>
    <body>
        <div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
            <table data-options="region:'center'" id="taskCandidateExprGrid" style="width:470px;height:360px;"></table>
        </div>
        <script>
            var taskDefKey = $("#taskKey").val();
            var dataGrid;
            $(function () {
                dataGrid = $('#taskCandidateExprGrid').datagrid({
                    url: '${ctx}/expr/list',
                    border: true,
                    singleSelect: true,
                    fitColumns: true,
                    striped: true,
                    idField: 'id',
                    sortName: 'id',
                    sortOrder: 'asc',
                    columns: [[{
                        title: '主键',
                        field: 'id',
                        sortable: true,
                        hidden: true
                    }, {
                        width: '160px',
                        title: '表达式名称',
                        field: 'name',
                        sortable: true
                    }, {
                        width: '108px',
                        title: '表达式',
                        field: 'expr',
                        sortable: true
                    }, {
                        width: '200px',
                        title: '描述',
                        field: 'desc',
                        sortable: true
                    }]],
                    onSelect: function (rowIndex, rowData) {
                        selectOne(rowData);
                    },
                    onUnselect: function (rowIndex, rowData) {
                        unselectOne(rowData);
                    },
                    onLoadSuccess: function (data) {
                        var jsonStr = $("#taskJson").val();
                        if (jsonStr === '') {
                            return;
                        }
                        var json = JSON.parse(jsonStr);

                        var checkedConf;
                        $.each(json, function (i, o) {
                            if (o.taskDefKey == taskDefKey) {
                                checkedConf = o;
                                return false;
                            }
                        })
                        if (checkedConf != undefined && checkedConf.code != undefined) {
                            var buttonKeyArray = (checkedConf.code+"").split(",");
                            $.each(data.rows, function (i, obj) {
                                if ($.inArray(obj.expr + "", buttonKeyArray) >= 0) {
                                    $('#taskCandidateExprGrid').datagrid('selectRow', i);
                                }
                            })
                        }
                    }
                });
            });

            function selectOne(rowData){
                var jsonStr = $("#taskJsonSelect").val();
                if (jsonStr == "") {
                    jsonStr = $("#taskJson").val();
                }

                if(jsonStr != "" && jsonStr != undefined){
                    var array = JSON.parse(jsonStr);
                    for (var i = 0; i < array.length; i++) {
                        if (array[i].taskDefKey == taskDefKey) {
                            var conf = array[i];
                            conf.code = rowData.expr;
                            conf.name = rowData.name;
                            jsonStr = JSON.stringify(array);
                            $("#taskJsonSelect").val(jsonStr);
                            break;
                        }
                    }
                }
            }

            function unselectOne(rowData){
                var jsonStr = $("#taskJsonSelect").val();
                if (jsonStr == "") {
                    jsonStr = $("#taskJson").val();
                }
                var array = JSON.parse(jsonStr);
                for (var i = 0; i < array.length; i++) {
                    if (array[i].taskDefKey == taskDefKey) {
                        var conf = array[i];
                        var code = conf.code;
                        var name = conf.name;

                        var codeArray = code.split(",");
                        var nameArray = name.split(",");

                        codeArray.splice($.inArray(rowData.expr, codeArray), 1);
                        nameArray.splice($.inArray(rowData.name, nameArray), 1);

                        conf.code = codeArray.join(",");
                        conf.name = nameArray.join(",");

                        break;
                    }
                }
                jsonStr = JSON.stringify(array);
                $("#taskJsonSelect").val(jsonStr);
            }
        </script>
    </body>
</html>