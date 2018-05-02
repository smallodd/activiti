<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>问询回复</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="overflow:auto;padding-top:30px;text-align:center">
        <form id="taskJumpForm" method="post">
                <dl>
                    <dt>
                    <dd style="float:left;" >问询内容:</dd>
                    <dd><div style="width: 200px;height: auto;margin-left: 40%">${askComment.askComment}</div></dd>
                    </dt>
                    <dt style="margin-top: 30px">
                    <dd style="float:left;">回复内容:</dd>
                    <dd><div style="width: 200px;height: auto;margin-left: 40%">${askComment.answerComment}</div></dd>
                    </dt>
                </dl>
		</form>
    </div>
</div>
</body>
</html>