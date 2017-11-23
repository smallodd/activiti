package com.hengtian.common.utils;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ma on 2017/11/23.
 */
public class JsonDateValueProcessor implements JsonValueProcessor {
    private String format = "yyyy-MM-dd";

    public JsonDateValueProcessor() {
    }

    public JsonDateValueProcessor(String format) {
        this.format = format;
    }

    public Object processArrayValue(Object paramObject, JsonConfig paramJsonConfig) {
        return this.process(paramObject);
    }

    public Object processObjectValue(String paramString, Object paramObject, JsonConfig paramJsonConfig) {
        return this.process(paramObject);
    }

    private Object process(Object value) {
        if(value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(this.format, Locale.CHINA);
            return sdf.format(value);
        } else {
            return value == null?"":value.toString();
        }
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
