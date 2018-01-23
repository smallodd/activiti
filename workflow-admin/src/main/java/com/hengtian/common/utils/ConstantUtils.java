package com.hengtian.common.utils;

/**
 * 数据字典 及 系统常量
 * @author liu.junyang
 */
public class ConstantUtils {
	/**
	 * 菜单
	 */
	public static final int RESOURCE_MENU = 0;
	/**
	 * 业务类型为请假业务 TVacation,CounterSign,MailTest,WebserviceTest,SVacation
	 */
	public final static String VACATION = "SVacation";
	/**
	 * 获取业务实体的KEY
	 */
	public final static String MODEL_KEY = "modelkey";
	/**
	 * 通过率
	 */
	public final static double PERCENT = 0.6;
	/**
	 * 发送邮件流程的KEY值
	 */
	public final static String MAILKEY = "MailTest";
	/**
	 * 发送邮件的默认系统地址
	 */
	public final static String MAIL_ADDRESS = "18092035350@163.com";
	/**
	 * 管理员ID
	 */
	public final static String ADMIN_ID = "1";
	
	/**
	 * @author liujunyang
	 * 自动生成编码前缀,必须为两位大写字母
	 */
	public static enum prefixCode{
		NO("资源编码","NO"),
		SN("资源编码","SN");
		//...
		private String name;
		private String value;
		private prefixCode(String name,String value){
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	
	/**
	 * SysOperLog(系统日志表)
	 */
	public static enum operStatus{
		SUCCESS("成功",1),
		FAIL("失败",2);
		
		private String name;
		private Integer value;
		
		private operStatus(String name,Integer value){
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
	}
	
	
	
	/**
	 * TVacation(请假表) 请假状态
	 */
	public static enum vacationStatus{
		APPROVING("正在审批",1),
		PASSED("审批通过",2),
		NOT_PASSED("审批不通过",3);
		
		private String name;
		private Integer value;
		
		private vacationStatus(String name,Integer value){
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
	}
	
	/**
	 * TVacation(请假表) 请假类型
	 */
	public static enum vacationType{
		PERSONAL("事假",1),
		SICK("病假",2);
		
		private String name;
		private Integer value;
		
		private vacationType(String name,Integer value){
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
	}
}
