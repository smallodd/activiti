<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>办理</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" style="overflow: auto;padding: 3px;">
        <div class="easyui-layout" style="width:750px;height:370px;">
            <div data-options="region:'west',split:true,collapsible:false" title="意见列表" style="width:180px;">
                <c:if test="${empty comments}">暂无意见 ！</c:if>
                <c:forEach var="comment" items="${comments}">
                    <div class="easyui-panel" title="${comment.commentTime}" style="height:auto;padding:5px;background-color: #fafbfd;word-break:break-all" data-options="closable:true,collapsible:true">
                        <a class="easyui-linkbutton" style="height: 25px;background-color: #282828;color: white">${comment.commentUser}</a>${comment.commentContent}
                    </div>
                </c:forEach>
            </div>
            <div data-options="region:'east',split:true,collapsible:false" title="审批人列表" style="width:180px;">
                <ul id="assigneeTree" style="padding-top: 5px"></ul>
            </div>
            <div data-options="region:'center'" title="操作">
                <form id="completeTaskForm" method="post">
                    <input type="hidden" name="taskId" id="taskId" value="${task.id}">
                    <input type="hidden" name="assignee" id="assignee"/>
                    <div id="tt" class="easyui-tabs" style="width:100%;height:99%">
                        <div title="审批结果/意见" style="padding:10px;display:none;">
                            <div class="easyui-panel" title="审批意见" style="width: 100%;padding:1px;">
                                <textarea data-options="multiline:true" name="commentContent" style="width:100%;height: 180px;overflow-y:auto;resize:none" placeholder="审批意见"></textarea>
                            </div>
                            <div class="easyui-panel" title="审批结果" style="width: 100%;padding:10px;">
                                <input type="radio" name="commentResult" style="cursor:pointer;" value=1 checked="checked">同意</input>
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="radio" name="commentResult" style="cursor:pointer;" value=2>不同意</input>
                            </div>
                        </div>
                        <div title="自定义参数" data-options="closable:true" style="overflow:auto;padding:10px;display:none;">
                            <div class="easyui-panel" title='自定义参数 例子：{"a":"b"}' style="width: 100%;padding:1px;">
                                <textarea data-options="multiline:true" name="jsonVariable" style="width:100%;height: 88%;overflow-y:auto;resize:none" placeholder="自定义参数"></textarea>
                            </div>
                        </div>
                        <div title="下步审批人" data-options="closable:true" style="padding:10px;display:none;">
                            <div class="easyui-panel" title="下步审批人" style="width: 100%;padding:1px;">
                                <textarea data-options="multiline:true" name="assigneeNext" style="width:100%;height: 88%;overflow-y:auto;resize:none" placeholder="下步审批人"></textarea>
                            </div>
                        </div>
                            <%--<table class="grid">
                                <tr>
                                    <td width="100px">自定义参数<br/>例子：{"a":"b"}</td>
                                    <td><input class="easyui-textbox" data-options="multiline:true" name="jsonVariable" style="width:260px;height: 70px;"></td>
                                </tr>
                                <tr>
                                    <td>下步审批人</td>
                                    <td>
                                        <input class="easyui-textbox" data-options="multiline:true" name="assigneeNext" style="width:260px;height: 70px;">
                                    </td>
                                </tr>
                                <tr>
                                    <td>我的意见</td>
                                    <td>
                                        <input class="easyui-textbox" data-options="multiline:true" name="commentContent" style="width:260px;height: 120px;">
                                    </td>
                                </tr>
                                <tr>
                                    <td>是否同意</td>
                                    <td>
                                        <input type="radio" name="commentResult" style="cursor:pointer;" value=1 checked="checked">同意</input>
                                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                        <input type="radio" name="commentResult" style="cursor:pointer;" value=2>不同意</input>
                                    </td>
                                </tr>
                            </table>--%>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function () {

        var taskId = parent.$("#taskId").val();
        if(taskId != undefined && taskId != ""){
            $('#assigneeTree').tree({
                url: '${ctx}/workflow/data/task/transfer/tree/'+taskId,
                singleSelect: true
            });
        }

        $('#completeTaskForm').form({
            url: '${ctx}/workflow/action/task/complete',
            onSubmit: function () {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }

                var assignee = $('#assigneeTree').tree('getSelected');
                if(!assignee){
                    progressClose();
                    $.messager.alert('提示', "请先选择任务办理人",'info');
                    return false;
                }else{
                    var children = $('#assigneeTree').tree("getChildren",assignee.target);
                    if(children != null && children.length > 0){
                        progressClose();
                        $.messager.alert('提示', "请先选择任务办理人",'info');
                        return false;
                    }
                }
                $("#assignee").val(assignee.id);

                return isValid;
            },
            success: function (result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    //之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
    });
</script>
</body>
</html>