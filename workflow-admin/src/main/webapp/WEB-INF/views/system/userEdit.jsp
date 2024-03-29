<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户编辑</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="userEditForm" method="post">
            <table class="grid">
                <tr>
                    <td>登录名</td>
                    <td><input name="id" type="hidden"  value="${user.id}">
                    <input name="loginName" type="text" class="easyui-textbox" placeholder="请输入登录名称" class="easyui-validatebox" data-options="required:true" value="${user.loginName}" style="width: 140px; height: 29px;"></td>
                    <td>姓名</td>
                    <td><input name="userName" type="text" class="easyui-textbox" placeholder="请输入姓名" class="easyui-validatebox" data-options="required:true" value="${user.userName}" style="width: 140px; height: 29px;"></td>
                </tr>
                <tr>
                    <td>邮箱</td>
                    <td>
                        <input name="userEmail" type="text" class="easyui-textbox" style="width:140px;height:29px;" data-options="required:true" value="${user.userEmail}" validType='email' missingMessage="邮箱不能为空" invalidMessage="请输入正确的邮箱"/>
                        	
                    </td>
                    <td>性别</td>
                    <td><select id="userEditSex" name="userSex" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                            <option value="0">男</option>
                            <option value="1">女</option>
                    </select></td>
                </tr>
                <tr>
                    <td>用户类型</td>
                    <td><select id="userEditUserType" name="userType" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                            <option value="0">管理员</option>
                            <option value="1">用户</option>
                    </select></td>
                    <td>电话</td>
                    <td>
                        <input type="text" name="userPhone" class="easyui-numberbox" style="width: 140px; height: 29px;" value="${user.userPhone}" validType='phoneNum' missingMessage="电话不能空"/>
                    </td>
                </tr>
                <tr>
                    <td>部门</td>
                    <td><select id="userEditDepartmentId" name="departmentId" style="width: 140px; height: 29px;" class="easyui-validatebox" data-options="required:true"></select></td>
                    <td>角色</td>
                    <td><input  id="userEditRoleIds" name="roleIds" style="width: 140px; height: 29px;"/></td>
                </tr>
                <tr>
                    <%-- <td>用户状态</td>
                    <td><select id="userEditStatus" name="status" value="${user.status}" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                            <option value="0">正常</option>
                            <option value="1">停用</option>
                    </select></td> --%>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#userEditDepartmentId').combotree({
            url : '${ctx}/sysDepartment/tree',
            parentField : 'pid',
            lines : true,
            panelHeight : 'auto',
            value : '${user.departmentId}'
        });

        $('#userEditRoleIds').combotree({
            url : '${ctx}/sysRole/tree',
            lines : true,
            panelHeight : 'auto',
            multiple : true,
            required : true,
            cascadeCheck : true,
            value : '${user.roleIds}'
        });

        $('#userEditForm').form({
            url : '${ctx}/sysUser/edit',
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
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    parent.$.messager.alert('错误', result.msg, 'error');
                }
            }
        });
        $("#userEditSex").val('${user.userSex}');
        $("#userEditUserType").val('${user.userType}');
    });
</script>
</body>
</html>