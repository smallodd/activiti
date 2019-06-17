<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>办理</title>
</head>
<body>
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
        <div data-options="region:'center'" title="操作" style="height: 350px;overflow: hidden">
            <form id="completeTaskForm" method="post">
                <input type="hidden" name="taskId" id="taskId" value="${task.id}">
                <input type="hidden" name="assignee" id="assignee"/>
                <input type="hidden" name="jsonVariable" id="jsonVariable"/>
                <input type="hidden" name="assigneeNext" id="assigneeNext"/>
                <input type="hidden" name="needSetNext" id="needSetNext"/>
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
                    <div title="自定义参数" style="padding:10px;display:none;">
                        <div class="easyui-panel" title='自定义参数 例子：{"a":"b"}' style="width: 98%;padding:1px;">
                            <%--<textarea data-options="multiline:true" name="jsonVariable" style="width:100%;height: 88%;overflow-y:auto;resize:none" placeholder="自定义参数"></textarea>--%>
                            <div class="var_" style="margin: 5px;display: none" id="varTemplate"><input style="height:28px;" placeholder="属性名称"/><span style="margin: 22px">:</span><input style="height:28px;" placeholder="属性值"/></div>
                            <div class="var_" style="margin: 5px;"><input style="height:28px" placeholder="属性名称"/><span style="margin: 22px">:</span><input style="height:28px" placeholder="属性值"/></div>
                            <div style="margin: 5px;" id="addTemplate"><a href="javascript:addVariable();" class="easyui-linkbutton" iconCls="icon-add" style="width:100%;height:30px">添加属性</a></div>
                        </div>
                    </div>
                    <c:if test="${needSetNext == 1}">
                        <div title="下步审批人" data-options="closable:true" style="padding:10px;display:none;">
                            <ul id="assigneeNextTree" class="easyui-tree" style="padding-top: 5px"></ul>
                        </div>
                    </c:if>
                </div>
            </form>
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

                    //设置下一节点审批人
                    selectAssigneeNext();
                    setVariables();
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
            loadAssigneeNext();
        });

        var loadAssigneeNextFlag = false;
        function loadAssigneeNext(){
            var needSetNext = $("#needSetNext").val();
            if(needSetNext == 0){
                return;
            }
            var taskId = $("#taskId").val();
            if(!loadAssigneeNextFlag){
                if(taskId != undefined && taskId != ""){
                    $('#assigneeNextTree').tree({
                        url : "${ctx}/workflow/data/task/assignee/next?taskId="+taskId,
                        animate : true,
                        checkbox : true,
                        lines : true
                    });
                }
            }
        }

        /**
         * 设置下一节点审批人
         */
        function selectAssigneeNext(){
            var needSetNext = $("#needSetNext").val();
            if(needSetNext == 0){
                return;
            }
            var roots = $('#assigneeNextTree').tree('getRoots');
            roots = JSON.parse(JSON.stringify(roots))
            if(roots != null && roots != undefined){
                for(var m=0;m<roots.length;m++){
                    var child = roots[m].children;
                    for(var n=0;n<child.length;n++){
                        if(child[n].checkState == "checked"){
                            var id = child[n].id;
                            var text = child[n].text;
                            child[n] = {};
                            child[n].userCode = id;
                            child[n].userName = text;
                        }else{
                            child.splice(n,1);
                            n--;
                        }
                    }
                    if(child == undefined || child == null || child.length == 0){
                        roots.splice(m, 1);
                        m--;
                    }else{
                        var id = roots[m].id;
                        roots[m] = {};
                        roots[m].taskDefinitionKey = id;
                        roots[m].assignee = child;
                    }
                }
            }
            $("#assigneeNext").val(JSON.stringify(roots));
        }

        /**
         * 添加属性列
         */
        function addVariable(){
            $("#addTemplate").before($("#varTemplate").clone().css("display","block"))
        }

        /**
         * 设置属性值
         */
        function setVariables(){
            progressClose();
            var vars = $(".var_");
            if(vars == undefined || vars == null){
                return;
            }
            var varObj = {};
            $.each(vars, function(i,var_){
                var vals = $(var_).find('input');
                var v0 = $(vals[0]).val();
                var v1 = $(vals[1]).val()
                if(v0 != '' && v1 != ''){
                    varObj[v0] = v1;
                }
            })
            $("#jsonVariable").val(JSON.stringify(varObj));
        }
    </script>
</body>
</html>