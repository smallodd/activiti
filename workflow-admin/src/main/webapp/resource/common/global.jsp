<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!--easyui插件引入-->
<link rel="stylesheet" type="text/css" href="/resource/jeasyui/themes/material/easyui.css?v=10">
<link rel="stylesheet" type="text/css" href="/resource/jeasyui/themes/icon.css">
<script type="text/javascript" src="/resource/jeasyui/jquery.min.js"></script>
<script type="text/javascript" src="/resource/jeasyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="/resource/jeasyui/locale/easyui-lang-zh_CN.js"></script>
<%-- [echarts图标js] --%>
<script type="text/javascript" src="/resource/echarts/echarts.min.js" charset="utf-8"></script>
<%-- [my97日期时间控件] --%>
<script type="text/javascript" src="/resource/static/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
<%-- [扩展JS] --%>
<script type="text/javascript" src="/resource/system/system.js" charset="utf-8"></script>
<%-- [扩展样式] --%>
<link rel="stylesheet" type="text/css" href="/resource/system/system.css?v=10" />
<link rel="stylesheet" type="text/css" href="/resource/static/foundation-icons/foundation-icons.css" />
<link rel="stylesheet" type="text/css" href="/resource/static/style/icons-view.css"/>
<script type="text/javascript" src="/resource/static/clipboard/clipboard.min.js" charset="utf-8"></script>
<%--ctx--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
var basePath = "${ctx}";
</script>
