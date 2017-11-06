<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>查看审批进度</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow:auto;padding:6px;">
		<table class="grid">
			<tr><td width="150">意见列表:</td><td width="100"><c:if test="${empty comments}">暂无意见 ！</c:if></td><td width="100"></td><td width="100"></td><td width="250"></td></tr>
			<c:if test="${!empty comments}"><td><strong>审批时间</strong></td><td><strong>节点名称</strong></td><td><strong>审批人</strong></td><td><strong>审批结果</strong></td><td><strong>批注内容</strong></td></c:if>
            <c:forEach var="comment" items="${comments}">
				<tr>
					<td>[${comment.commentTime}]</td><td>${comment.commentTask}</td><td>${comment.commentUser}</td><td>${comment.commentResult}</td><td>${comment.commentContent}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
<script type="text/javascript">
</script>
</body>
</html>