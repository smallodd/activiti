package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * Created by ma on 2018/5/8.
 */
@TableName("t_button")
public class TButton implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(value="button_key")
    private String buttonKey;
    @TableField(value="class_name")
    private String className;
    @TableField(value="desc")
    private String desc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getButtonKey() {
        return buttonKey;
    }

    public void setButtonKey(String buttonKey) {
        this.buttonKey = buttonKey;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
