package com.hengtian.common.base;

import com.common.common.CodeConts;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pagehelper.PageInfo;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Base response.
 * @author houjinrong@chtwm.com
 * date 2018/5/23 11:29
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BaseResponse {
    /**
     * 成功返回值
     */
    private final static String SUCCESS_CODE = CodeConts.SUCCESS;

    /**
     * 失败默认返回值
     */
    private final static String FAILED_CODE = CodeConts.FAILURE;

    /**
     * 成功回复报文包含的对象
     */
    @JsonProperty("data")
    private final Object data;
    /**
     * 返回值
     */
    @JsonProperty("status")
    private final String status;
    /**
     * 返回msg
     */
    @JsonProperty("message")
    private final String message;
    /**
     * 返回msg
     */
    @JsonProperty("pages")
    private final Integer pages;
    /**
     * 返回msg
     */
    @JsonProperty("pageNum")
    private final Integer pageNum;
    /**
     * 返回msg
     */
    @JsonProperty("pageSize")
    private final Integer pageSize;
    /**
     * 其余参数
     */
    @JsonProperty("params")
    private final Map<String, Object> params;

    private BaseResponse(final Object data, final String status, final String message,
                         final Integer pages, final Integer pageNum, final Integer pageSize,
                         final Map<String, Object> params) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.params = params;
        this.pages = pages;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 构造成功报文builder
     *
     * @return 成功报文builder builder
     */
    public static Builder successCustom() {
        return successCustom("操作成功！");
    }

    /**
     * 构造成功报文builder
     *
     * @param message the message
     * @return builder
     */
    public static Builder successCustom(final String message) {
        return new Builder(SUCCESS_CODE, message);
    }

    /**
     * 请求成功返回状态码和提示
     *
     * @param successCode 状态码
     * @param message     提示信息
     * @return builder
     */
    public static Builder successCustom(final String successCode, final String message) {
        return new Builder(successCode, message);
    }

    /**
     * 构造错误返回报文builder
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 错误返回报文builder builder
     */
    public static Builder failedCustom(final String errorCode, final String errorMsg) {
        return new Builder(errorCode, errorMsg);
    }

    /**
     * Failed custom builder.
     *
     * @param errorMsg the error msg
     * @return the builder
     */
    public static Builder failedCustom(final String errorMsg) {
        return new Builder(FAILED_CODE, errorMsg);
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        /**
         * 返回值
         */
        private final String status;
        /**
         * msg
         */
        private final String message;
        /**
         * 返回msg
         */
        private Integer pages;
        /**
         * 返回msg
         */
        private Integer pageNum;
        /**
         * 返回msg
         */
        private Integer pageSize;
        /**
         * 其他参数
         */
        private final Map<String, Object> params = new HashMap<>();
        /**
         * 任意可json化的对象
         */
        private Object data;

        private Builder(final String status, final String message) {
            this.status = status;
            this.message = message;
        }

        /**
         * 添加参数信息
         *
         * @param key   the key
         * @param value the value
         * @return builder
         */
        public Builder addParam(final String key, final String value) {
            this.params.put(key, value);
            return this;
        }

        /**
         * 设置result data
         *
         * @param data the data
         * @return data
         */
        public Builder setData(final Object data) {
            this.data = data;
            return this;
        }

        /**
         * 设置result 分页
         *
         * @param pageInfo the page info
         * @return data
         */
        public Builder setData(final PageInfo pageInfo) {
            this.data = pageInfo.getList();
            //当前页
            this.pageNum = pageInfo.getPageNum();
            this.pageSize = pageInfo.getPageSize();
            this.pages = pageInfo.getPages();
            return this;
        }

        /**
         * build BaseResponse
         *
         * @return base response
         */
        public BaseResponse build() {
            return new BaseResponse(this.data == null ? "" : this.data, this.status, this.message,
                    this.pages, this.pageNum, this.pageSize, this.params);
        }
    }
}
