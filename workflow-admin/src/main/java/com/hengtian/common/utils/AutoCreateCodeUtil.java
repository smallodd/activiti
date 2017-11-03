package com.hengtian.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liujunyang
 * 自动生成编码工具类
 * 默认编码格式为 NO201710170001
 */
public class AutoCreateCodeUtil {
	
	/**
	 * 自动生成系统编码方法
	 * @param prefixCode 编码前缀,例如: "NO"
	 * @param maxCode 传入数据库查询出的最大编码
	 * @return
	 */
	public static String autoCreateSysCode(String prefixCode,String maxCode){
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		String currentDateStr= sf.format(new Date());//获取日期字符串
		if(StringUtils.isBlank(maxCode ) || !maxCode.contains(prefixCode)){
			return prefixCode+currentDateStr+String.format("%04d", Integer.parseInt("0000")+1);
		}
		String prefix = maxCode.substring(0, 2);//截取编码两位字母
		String dateStr= maxCode.substring(2, maxCode.length()-4);//截取8位日期字符串
		String suffix = maxCode.substring(maxCode.length()-4);//截取后四位流水号
		
		if(currentDateStr.equals(dateStr)){
			String flowStr = String.format("%04d", Integer.parseInt(suffix)+1);
			return prefix+currentDateStr+flowStr;
		}else{
			String flowStr = String.format("%04d", Integer.parseInt("0000")+1);
			return prefix+currentDateStr+flowStr;
		}
	}
}
