package com.hengtian.common.exception;

import com.common.common.CodeConts;
import com.common.exception.MyException;
import com.hengtian.common.base.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * 统一异常处理
 *
 * @author houjinrong@chtwm.com
 * date 2018/5/23 11:28
 */
@ControllerAdvice
@Slf4j
public class WebExceptionHandler {

    // ----------- CheckedException ----------

    /**
     * 无法找到对应类
     * @param ex ClassNotFoundException异常
     * @return BaseResponse
     */
    @ExceptionHandler(ClassNotFoundException.class)
    public @ResponseBody
    BaseResponse classnotfoundExp(ClassNotFoundException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -----异常已记录（"+ System.currentTimeMillis()+") ----- ########");
        return BaseResponse.failedCustom("无法找到对应类！").build();
    }

    /**
     * 未找到该方法
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public @ResponseBody
    BaseResponse mothodnotfoundExp(NoSuchMethodException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -----异常已记录（"+ System.currentTimeMillis()+") ----- ########");
        return BaseResponse.failedCustom("未找到该方法！").build();
    }

    /**
     * 未找到指定文件
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(FileNotFoundException.class)
    public @ResponseBody
    BaseResponse filenotfoundExp(FileNotFoundException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -----异常已记录（"+ System.currentTimeMillis()+") ----- ########");
        return BaseResponse.failedCustom("未找到指定文件！").build();
    }

    /**
     * I
     * @param ex BaseResponse异常
     * @return BaseResponse
     */
    @ExceptionHandler(IOException.class)
    public @ResponseBody
    BaseResponse ioExp(IOException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("I/O异常！").build();
    }

    // ---------------- RuntimeException ----------------------

    /**
     * 数组已越界
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public @ResponseBody
    BaseResponse indexOutOfBoundsExp(IndexOutOfBoundsException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("数组已越界！").build();
    }

    /**
     * 空指针异常
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(NullPointerException.class)
    public @ResponseBody
    BaseResponse nullPointerExp(NullPointerException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("空指针异常！").build();
    }

    /**
     * 类型强制转换异常
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(ClassCastException.class)
    public @ResponseBody
    BaseResponse classCastExp(ClassCastException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("类型强制转换异常！").build();
    }

    /**
     * 数组大小为负异常
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(NegativeArraySizeException.class)
    public @ResponseBody
    BaseResponse arraySizeExp(NegativeArraySizeException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("数组大小为负异常！").build();
    }

    /**
     * 算术异常
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(ArithmeticException.class)
    public @ResponseBody
    BaseResponse arithmeticExp(ArithmeticException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("算术异常！").build();
    }

    /**
     * 操作数据库异常
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(SQLException.class)
    public @ResponseBody
    BaseResponse sqlExp(SQLException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("操作数据库异常！").build();
    }

    /**
     * 违法的访问异常
     * @param ex 异常
     * @return BaseResponse
     */
    @ExceptionHandler(IllegalAccessException.class)
    public @ResponseBody
    BaseResponse illegalAccessExp(IllegalAccessException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("违法的访问异常！").build();
    }

    /**
     * 违法的监控状态异常
     * @param ex IllegalMonitorStateException异常
     * @return BaseResponse
     */
    @ExceptionHandler(IllegalMonitorStateException.class)
    public @ResponseBody
    BaseResponse illegalMonitorExp(IllegalMonitorStateException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("违法的监控状态异常！").build();
    }

    /**
     * 违法的监控状态异常
     * @param ex IllegalStateException异常
     * @return BaseResponse
     */
    @ExceptionHandler(IllegalStateException.class)
    public @ResponseBody
    BaseResponse illegalStateExp(IllegalStateException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return BaseResponse.failedCustom("违法的监控状态异常！").build();
    }

    /**
     * 网络连接异常
     * @param ex 网络连接异常
     * @return BaseResponse
     */
    @ExceptionHandler(ConnectException.class)
    public @ResponseBody
    BaseResponse operateExpNetException(ConnectException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return BaseResponse.failedCustom("网络连接异常！").build();
    }

    /**
     * 服务器异常
     * @param ex Exception异常
     * @return BaseResponse
     */
    @ExceptionHandler(Exception.class)
    public @ResponseBody
    BaseResponse exp(Exception ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return BaseResponse.failedCustom("服务器异常！").build();
    }

    /**
     * 自定义异常
     * @param ex MyException异常
     * @return BaseResponse
     */
    @ExceptionHandler(MyException.class)
    public @ResponseBody
    BaseResponse myExp(MyException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return BaseResponse.failedCustom(ex.getMessage()).build();
    }

    /**
     * 强制转换异常
     * @param ex ParseException异常
     * @return BaseResponse
     */
    @ExceptionHandler(ParseException.class)
    public @ResponseBody
    BaseResponse parseExp(ParseException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return BaseResponse.failedCustom("强制转换异常！").build();
    }

    /**
     * 请求超时异常
     * @param ex SocketTimeoutException异常
     * @return BaseResponse
     */
    @ExceptionHandler(SocketTimeoutException.class)
    public @ResponseBody
    BaseResponse timeoutExp(SocketTimeoutException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return BaseResponse.failedCustom("请求超时异常！").build();
    }

    /**
     * 参数验证异常 -- 对于@表单提交
     * @param ex BindException异常
     * @param request HttpServletRequest
     * @return BaseResponse
     */
    @ExceptionHandler(BindException.class)
    public @ResponseBody
    BaseResponse validateExp(BindException ex, HttpServletRequest request){
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("入参验证失败");
        log.info("请求的地址：{}", request.getRequestURI());
        log.info("字段:{}, 信息:{}", fieldError.getField(), fieldError.getDefaultMessage());
        return BaseResponse.failedCustom(fieldError.getDefaultMessage() + fieldError.getField()).build();
    }

    /**
     * 参数验证异常 -- 对于@RequestParam(required = true)
     * @param ex MissingServletRequestParameterException异常
     * @param request HttpServletRequest
     * @return BaseResponse
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    BaseResponse validateExp(MissingServletRequestParameterException ex, HttpServletRequest request){
        log.info("入参验证失败");
        log.info("请求的地址：{}", request.getRequestURI());
        log.info("类型{}, 字段{}", ex.getParameterType(), ex.getParameterName(), "不能为空，必填项！");
        return BaseResponse.failedCustom(ex.getParameterName() + "不能为空，必填项！类型："  + ex.getParameterType()).build();
    }

    /**
     * 参数验证异常 -- 对于@RquestBody
     * @param ex MethodArgumentNotValidException 异常
     * @param request HttpServletRequest
     * @return BaseResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody
    BaseResponse validateExp(MethodArgumentNotValidException ex, HttpServletRequest request){
        log.info("入参验证失败");
        log.info("请求的地址：{}", request.getRequestURI());
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("字段:{}, 信息:{}", fieldError.getField(), fieldError.getDefaultMessage());
        return BaseResponse.failedCustom(fieldError.getDefaultMessage() + fieldError.getField()).build();
    }

    /**
     * 验证用户是否登陆异常 -- 对于@RequestAttribute(InsuranceConstant.REQUEST_EMP_INFO)
     * @param ex ServletRequestBindingException
     * @param request HttpServletRequest
     * @return BaseResponse
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public @ResponseBody
    BaseResponse validateExp(ServletRequestBindingException ex, HttpServletRequest request){
        String REQUEST_EMP_INFO = "REQUEST_USER_INFO";
        if (ex.getMessage().contains(REQUEST_EMP_INFO)) {
            log.info("用户验证失败");
            log.info("请求的地址：{}", request.getRequestURI());
            return BaseResponse.failedCustom(CodeConts.LOGIN_FAILURE, "用户未登录（校验失败）").build();
        } else {
            return exp(ex);
        }
    }
}
