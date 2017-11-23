package com.hengtian.common.utils;

/**
 * Created by ma on 2017/11/23.
 */
public class RedisConfigBean {
    private String proPrefix;
    private String host;
    private Integer port;

    public RedisConfigBean() {
    }

    public String getProPrefix() {
        return this.proPrefix;
    }

    public void setProPrefix(String proPrefix) {
        this.proPrefix = proPrefix;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String toString() {
        StringBuilder suff = new StringBuilder();
        suff.append("proPrefix=");
        suff.append(this.proPrefix);
        suff.append("; host=");
        suff.append(this.host);
        suff.append("; port=");
        suff.append(this.port);
        return suff.toString();
    }
}
