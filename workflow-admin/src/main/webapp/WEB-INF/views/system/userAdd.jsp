<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/resource/common/global.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户新增</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow:hidden;padding:3px;">
        <form id="userAddForm" method="post">
            <table class="grid">
                <tr>
                    <td>登录名</td>
                    <td><input name="loginName" type="text" class="easyui-textbox" style="width:160px;height:29px;" data-options="required:true"></td>
                    <td>姓名</td>
                    <td><input name="userName" type="text" class="easyui-textbox" style="width:160px;height:29px;" data-options="required:true"></td>
                </tr>
                <tr>
                    <td>密码</td>
                    <td><input name="loginPwd" type="password" class="easyui-textbox" style="width:160px;height:29px;" data-options="required:true"></td>
                    <td>性别</td>
                    <td>
                        <select name="userSex" class="easyui-combobox" style="width:160px;height:29px;" data-options="panelHeight:'auto'">
                            <option value="0" selected="selected">男</option>
                            <option value="1" >女</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>用户类型</td>
                    <td>
                        <select name="userType" class="easyui-combobox" style="width:160px;height:29px;" data-options="panelHeight:'auto'">
                            <option value="0">管理员</option>
                            <option value="1" selected="selected">用户</option>
                        </select>
                    </td>
                    <td>电话</td>
                    <td>
                        <input name="userPhone" type="text" class="easyui-numberbox" style="width:160px;height:29px;" data-options="required:true" validType='phoneNum' missingMessage="电话不能空"/>
                    </td>
                </tr>
                <tr>
                    <td>部门</td>
                    <td><select id="userAddDepartmentId" name="departmentId" style="width:160px;height:29px;" data-options="required:true"></select></td>
                    <td>角色</td>
                    <td><select id="userAddRoleIds" name="roleIds" style="width:160px;height:29px;"></select></td>
                </tr>
                <!-- <tr>
                    <td>用户状态</td>
                    <td>
                        <select name="status" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                                <option value="0">正常</option>
                                <option value="1">停用</option>
                        </select>
                    </td> 
                </tr>-->
                <tr>
                    <td>邮箱</td>
                    <td>
                        <input name="userEmail" type="text" class="easyui-textbox" style="width:160px;height:29px;" data-options="required:true" validType='email' missingMessage="邮箱不能为空" invalidMessage="请输入正确的邮箱"/>
                    </td>
                    <td></td><td></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $('#userAddDepartmentId').combotree({
            url : '${ctx}/sysDepartment/tree',
            parentField : 'parentId',
            lines : true,
            panelHeight : 'auto'
        });

        $('#userAddRoleIds').combotree({
            url: '${ctx}/sysRole/tree',
            multiple: true,
            required: true,
            panelHeight : 'auto'
        });

        $('#userAddForm').form({
            url : '${ctx}/sysUser/add',
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
                    parent.$.messager.alert('提示', result.msg, 'warning');
                }
            }
        });
        
    });
</script>
</body>
</html>