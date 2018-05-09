<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta charset="UTF-8">
        <title>权限按钮管理</title>
    </head>
    <body>
        <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff;" id="tb_button">
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-clipboard-pencil'" onclick="selectAll();">全选</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'fi-clipboard'" onclick="clearSelections();">取消</a>
        </div>

        <div class="easyui-layout" data-options="fit:true,border:false" style="overflow: auto;padding: 8px;">
            <table data-options="region:'center'" id="taskCandidateButtonGrid" style="width:470px;height:360px;"></table>
        </div>
        <script>
            var taskDefKey = $("#taskKey").val();
            var dataGrid;
            $(function () {
                dataGrid = $('#taskCandidateButtonGrid').datagrid({
                    url: '${ctx}/button/list',
                    border: true,
                    singleSelect: false,
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
                        width: '100px',
                        title: '键值',
                        field: 'buttonKey',
                        sortable: true
                    }, {
                        width: '160px',
                        title: '名称',
                        field: 'name',
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
                        if (checkedConf != undefined && checkedConf.buttonKey != undefined) {
                            var buttonKeyArray = (checkedConf.buttonKey+"").split(",");
                            $.each(data.rows, function (i, obj) {
                                if ($.inArray(obj.buttonKey + "", buttonKeyArray) >= 0) {
                                    $('#taskCandidateButtonGrid').datagrid('selectRow', i);
                                }
                            })
                        }
                    },
                    toolbar : "#tb_button"
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
                            if (conf.buttonKey) {
                                if ($.inArray(rowData.buttonKey + "", conf.buttonKey.split(",")) < 0) {
                                    conf.buttonKey = ((conf.buttonKey == "") ? "" : (conf.buttonKey + ",")) + rowData.buttonKey;
                                    conf.buttonName = ((conf.buttonName == "") ? "" : (conf.buttonName + ",")) + rowData.name;
                                    jsonStr = JSON.stringify(array);
                                    $("#taskJsonSelect").val(jsonStr);
                                    break;
                                }
                            } else {
                                conf.buttonKey = ((conf.buttonKey == "" || conf.buttonKey == undefined) ? "" : (conf.buttonKey + ",")) + rowData.buttonKey;
                                conf.buttonName = ((conf.buttonName == "" || conf.buttonName == undefined) ? "" : (conf.buttonName + ",")) + rowData.name;
                                jsonStr = JSON.stringify(array);
                                $("#taskJsonSelect").val(jsonStr);
                                break;
                            }
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
                        var buttonKey = conf.buttonKey;
                        var buttonName = conf.buttonName;

                        var buttonKeyArray = buttonKey.split(",");
                        var buttonNameArray = buttonName.split(",");

                        buttonKeyArray.splice($.inArray(rowData.buttonKey, buttonKeyArray), 1);
                        buttonNameArray.splice($.inArray(rowData.name, buttonNameArray), 1);

                        conf.buttonKey = buttonKeyArray.join(",");
                        conf.buttonName = buttonNameArray.join(",");

                        break;
                    }
                }
                jsonStr = JSON.stringify(array);
                $("#taskJsonSelect").val(jsonStr);
            }

            //全选
            function selectAll() {
                $("#taskCandidateButtonGrid").datagrid('selectAll');
                var data = $("#taskCandidateButtonGrid").datagrid("getRows");
                $.each(data, function (i, obj) {
                    selectOne(obj);
                })
            }

            //全清
            function clearSelections() {
                $("#taskCandidateButtonGrid").datagrid('clearSelections');
                var data = $("#taskCandidateButtonGrid").datagrid("getRows");
                $.each(data, function (i, obj) {
                    unselectOne(obj);
                })
            }
        </script>
    </body>
</html>