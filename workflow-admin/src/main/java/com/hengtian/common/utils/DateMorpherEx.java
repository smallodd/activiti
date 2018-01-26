package com.hengtian.common.utils;

import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.object.AbstractObjectMorpher;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ma on 2017/11/23.
 */
public class DateMorpherEx extends AbstractObjectMorpher {
    private Date defaultValue;
    private String[] formats;
    private boolean lenient;
    private Locale locale;

    public DateMorpherEx(String[] formats) {
        this(formats, Locale.getDefault(), false);
    }

    public DateMorpherEx(String[] formats, boolean lenient) {
        this(formats, Locale.getDefault(), lenient);
    }

    public DateMorpherEx(String[] formats, Date defaultValue) {
        this(formats, defaultValue, Locale.getDefault(), false);
    }

    public DateMorpherEx(String[] formats, Date defaultValue, Locale locale, boolean lenient) {
        super(true);
        if(formats != null && formats.length != 0) {
            this.formats = formats;
            if(locale == null) {
                this.locale = Locale.getDefault();
            } else {
                this.locale = locale;
            }

            this.lenient = lenient;
            this.setDefaultValue(defaultValue);
        } else {
            throw new MorphException("invalid array of formats");
        }
    }

    public DateMorpherEx(String[] formats, Locale locale) {
        this(formats, locale, false);
    }

    DateMorpherEx(String[] formats, Locale locale, boolean lenient) {
        if(formats != null && formats.length != 0) {
            this.formats = formats;
            if(locale == null) {
                this.locale = Locale.getDefault();
            } else {
                this.locale = locale;
            }

            this.lenient = lenient;
        } else {
            throw new MorphException("invalid array of formats");
        }
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(!(obj instanceof DateMorpherEx)) {
            return false;
        } else {
            DateMorpherEx other = (DateMorpherEx)obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.formats, other.formats);
            builder.append(this.locale, other.locale);
            builder.append(this.lenient, other.lenient);
            if(super.isUseDefault() && other.isUseDefault()) {
                builder.append(this.getDefaultValue(), other.getDefaultValue());
                return builder.isEquals();
            } else {
                return !super.isUseDefault() && !other.isUseDefault()?builder.isEquals():false;
            }
        }
    }

    public Date getDefaultValue() {
        return this.defaultValue != null?(Date)this.defaultValue.clone():this.defaultValue;
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.formats);
        builder.append(this.locale);
        builder.append(this.lenient);
        if(super.isUseDefault()) {
            builder.append(this.getDefaultValue());
        }

        return builder.toHashCode();
    }

    public Object morph(Object value) {
        if(value == null) {
            return null;
        } else if(Date.class.isAssignableFrom(value.getClass())) {
            return (Date)value;
        } else if(!this.supports(value.getClass())) {
            throw new MorphException(value.getClass() + " is not supported");
        } else {
            String strValue = (String)value;
            SimpleDateFormat dateParser = null;
            int i = 0;

            while(i < this.formats.length) {
                if(dateParser == null) {
                    dateParser = new SimpleDateFormat(this.formats[i], this.locale);
                } else {
                    dateParser.applyPattern(this.formats[i]);
                }

                dateParser.setLenient(this.lenient);

                try {
                    return dateParser.parse(strValue.toLowerCase());
                } catch (ParseException var6) {
                    ++i;
                }
            }

            if(super.isUseDefault()) {
                return this.defaultValue;
            } else {
                throw new MorphException("Unable to parse the date " + value);
            }
        }
    }

    public Class morphsTo() {
        return Date.class;
    }

    public void setDefaultValue(Date defaultValue) {
        if(defaultValue != null) {
            this.defaultValue = (Date)defaultValue.clone();
        } else {
            this.defaultValue = null;
        }

    }

    public boolean supports(Class clazz) {
        return String.class.isAssignableFrom(clazz);
    }
}
