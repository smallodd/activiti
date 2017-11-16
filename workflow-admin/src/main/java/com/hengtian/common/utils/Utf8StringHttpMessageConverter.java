package com.hengtian.common.utils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.http.converter.AbstractHttpMessageConverter;

/**
 * Created by ma on 2017/11/16.
 */
public class Utf8StringHttpMessageConverter extends
        AbstractHttpMessageConverter<String> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final Charset defaultCharset;

    private final List<Charset> availableCharsets;

    private boolean writeAcceptCharset = true;

    public Utf8StringHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public Utf8StringHttpMessageConverter(Charset defaultCharset) {
        super(new MediaType("text", "plain", defaultCharset), MediaType.ALL);
        this.defaultCharset = defaultCharset;
        this.availableCharsets = new ArrayList<Charset>(Charset
                .availableCharsets().values());
    }

    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    protected String readInternal(@SuppressWarnings("rawtypes") Class clazz,
                                  HttpInputMessage inputMessage) throws IOException {
        Charset charset = getContentTypeCharset(inputMessage.getHeaders()
                .getContentType());
        return FileCopyUtils.copyToString(new InputStreamReader(inputMessage
                .getBody(), charset));
    }

    @Override
    protected Long getContentLength(String s, MediaType contentType) {
        Charset charset = getContentTypeCharset(contentType);
        try {
            return (long) s.getBytes(charset.name()).length;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected void writeInternal(String s, HttpOutputMessage outputMessage)
            throws IOException {
        if (this.writeAcceptCharset) {
            outputMessage.getHeaders().setAcceptCharset(getAcceptedCharsets());
            if(outputMessage.getHeaders().getContentType().getSubtype().equals("x-ms-application")){//针对IE的兼容性处理，统一转化为text/plain
                outputMessage.getHeaders().setContentType(MediaType.TEXT_PLAIN);
            }
        }
        Charset charset = getContentTypeCharset(outputMessage.getHeaders()
                .getContentType());
        FileCopyUtils.copy(s, new OutputStreamWriter(outputMessage.getBody(),
                charset));
    }

    protected List<Charset> getAcceptedCharsets() {
        return this.availableCharsets;
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            return contentType.getCharSet();
        } else {
            return this.defaultCharset;
        }
    }

}
