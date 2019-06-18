<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>意见征询回复</title>
</head>
<body>

<div style="width:575px;height:auto;background:#7190E0;padding:5px;">
    <div class="easyui-panel" title="意见征询内容" collapsible="true" style="width:100%;height:auto;padding:10px;">
        ${askComment.askComment}
    </div>
    <br/>
    <div class="easyui-panel" title="回复内容" collapsible="true" style="width:100%;height:auto;padding:10px;">
        ${askComment.answerComment}
    </div>
</div>
</body>
</html>