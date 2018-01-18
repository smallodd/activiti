package com.hengtian.common.utils;

import java.util.Date;
import java.util.Map;

public class MailTemplateUtils {

	/**
	 * 获取邮件内容
	 */
	public static String getMailTemplate(Map<String,String> templateStr){
		String dateStr= DateUtils.formatDateToString(new Date());
		String resultText = templateStr.get("deptName")+"的"+templateStr.get("complateUserName")+"于"
		+dateStr+"已经办理了您的单号为"+templateStr.get("vacationCode")+"请假申请，办理的结果为:"+templateStr.get("resultStr")
		+"，办理人的批注信息为:"+templateStr.get("commentContent")+"。";
		return resultText;
	}
}
