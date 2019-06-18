package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
/**
 * <p>
 * 邮件发送日志
 * </p>
 *
 * @author junyang.liu
 * @since 2017-10-19
 */
@TableName("t_mail_log")
public class TMailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private String id;
    /**
     * 邮件的接收者。可使用逗号分隔多个接收者
     */
    @TableField(value="mail_to")
    private String mailTo;
    /**
     * 邮件发送者的地址。如果不提供，会使用默认配置的地址。
     */
    @TableField(value="mail_from")
    private String mailFrom;
    /**
     * 邮件的主题
     */
    @TableField(value="mail_subject")
    private String mailSubject;
    /**
     * 邮件抄送人。可使用逗号分隔多个接收者。
     */
    @TableField(value="maill_cc")
    private String maillCc;
    /**
     * 邮件暗送人。可使用逗号分隔多个接收者。
     */
    @TableField(value="mail_bcc")
    private String mailBcc;
    /**
     * 邮件的文本内容。
     */
    @TableField(value="mail_text")
    private String mailText;
    /**
     * 邮件发送的时间。
     */
    @TableField(value="send_time")
    private java.util.Date sendTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMaillCc() {
        return maillCc;
    }

    public void setMaillCc(String maillCc) {
        this.maillCc = maillCc;
    }

    public String getMailBcc() {
        return mailBcc;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }

    public String getMailText() {
        return mailText;
    }

    public void setMailText(String mailText) {
        this.mailText = mailText;
    }

    public java.util.Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(java.util.Date sendTime) {
        this.sendTime = sendTime;
    }

}
