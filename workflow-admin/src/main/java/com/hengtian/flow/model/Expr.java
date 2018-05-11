package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 表达式
 * @author houjinrong@chtwm.com
 * date 2018/5/11 9:54
 */
@TableName("t_expr")
public class Expr {
    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private long id;
    /**
     * 表达式名称
     */
    @TableField(value = "name")
    private String name;
    /**
     * 表达式
     */
    @TableField(value = "expr")
    private String expr;
    /**
     * desc
     */
    @TableField(value = "desc")
    private String desc;
    /**
     * desc
     */
    @TableField(value = "status")
    private Float status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Float getStatus() {
        return status;
    }

    public void setStatus(Float status) {
        this.status = status;
    }
}
