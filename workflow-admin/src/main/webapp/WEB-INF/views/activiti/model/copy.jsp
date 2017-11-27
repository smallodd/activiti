<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>模型复制</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" >
    <div data-options="region:'center',border:false" style="overflow:hidden;padding:3px;">
        <form id="modelAddForm" method="post">
            <input name="id" type="hidden" value="${id}">
            <table class="grid">
                <tr>
                    <td>模型名称</td>
                    <td><input name="name" type="text" class="easyui-textbox" style="width:240px;height:29px;" data-options="required:true"></td>
                </tr>
                <tr>
                    <td>模型KEY</td>
                    <td><input name="key" type="text" class="easyui-textbox" style="width:240px;height:29px;"></td>
                </tr>

            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#modelAddForm').form({
            url : '${ctx}/activiti/model/copy',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success : function(result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    //window.location.href="/modeler.html?modelId="+result.obj;
                    //parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');

                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
    });


</script>
</body>
</html>