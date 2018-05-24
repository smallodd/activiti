package com.hengtian.common.exception;

import com.common.common.CodeConts;
import com.common.exception.MyException;
import com.hengtian.common.result.Result;
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
     * @return Result
     */
    @ExceptionHandler(ClassNotFoundException.class)
    public @ResponseBody
    Result classnotfoundExp(ClassNotFoundException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -----异常已记录（"+ System.currentTimeMillis()+") ----- ########");
        return new Result("无法找到对应类！");
    }

    /**
     * 未找到该方法
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public @ResponseBody
    Result mothodnotfoundExp(NoSuchMethodException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -----异常已记录（"+ System.currentTimeMillis()+") ----- ########");
        return new Result("未找到该方法！");
    }

    /**
     * 未找到指定文件
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(FileNotFoundException.class)
    public @ResponseBody
    Result filenotfoundExp(FileNotFoundException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -----异常已记录（"+ System.currentTimeMillis()+") ----- ########");
        return new Result("未找到指定文件！");
    }

    /**
     * I
     * @param ex Result异常
     * @return Result
     */
    @ExceptionHandler(IOException.class)
    public @ResponseBody
    Result ioExp(IOException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("I/O异常！");
    }

    // ---------------- RuntimeException ----------------------

    /**
     * 数组已越界
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public @ResponseBody
    Result indexOutOfBoundsExp(IndexOutOfBoundsException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("数组已越界！");
    }

    /**
     * 空指针异常
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(NullPointerException.class)
    public @ResponseBody
    Result nullPointerExp(NullPointerException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("空指针异常！");
    }

    /**
     * 类型强制转换异常
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(ClassCastException.class)
    public @ResponseBody
    Result classCastExp(ClassCastException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("类型强制转换异常！");
    }

    /**
     * 数组大小为负异常
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(NegativeArraySizeException.class)
    public @ResponseBody
    Result arraySizeExp(NegativeArraySizeException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("数组大小为负异常！");
    }

    /**
     * 算术异常
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(ArithmeticException.class)
    public @ResponseBody
    Result arithmeticExp(ArithmeticException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("算术异常！");
    }

    /**
     * 操作数据库异常
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(SQLException.class)
    public @ResponseBody
    Result sqlExp(SQLException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("操作数据库异常！");
    }

    /**
     * 违法的访问异常
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(IllegalAccessException.class)
    public @ResponseBody
    Result illegalAccessExp(IllegalAccessException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("违法的访问异常！");
    }

    /**
     * 违法的监控状态异常
     * @param ex IllegalMonitorStateException异常
     * @return Result
     */
    @ExceptionHandler(IllegalMonitorStateException.class)
    public @ResponseBody
    Result illegalMonitorExp(IllegalMonitorStateException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("违法的监控状态异常！");
    }

    /**
     * 违法的监控状态异常
     * @param ex IllegalStateException异常
     * @return Result
     */
    @ExceptionHandler(IllegalStateException.class)
    public @ResponseBody
    Result illegalStateExp(IllegalStateException ex){
        log.error(ex.getMessage(),ex);
        log.info("##### -------异常已记录（"+ System.currentTimeMillis()+") ---- ########");
        return new Result("违法的监控状态异常！");
    }

    /**
     * 网络连接异常
     * @param ex 网络连接异常
     * @return Result
     */
    @ExceptionHandler(ConnectException.class)
    public @ResponseBody
    Result operateExpNetException(ConnectException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return new Result("网络连接异常！");
    }

    /**
     * 服务器异常
     * @param ex Exception异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    public @ResponseBody
    Result exp(Exception ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return new Result("服务器异常！");
    }

    /**
     * 自定义异常
     * @param ex MyException异常
     * @return Result
     */
    @ExceptionHandler(MyException.class)
    public @ResponseBody
    Result myExp(MyException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return new Result(ex.getMessage());
    }

    /**
     * 强制转换异常
     * @param ex ParseException异常
     * @return Result
     */
    @ExceptionHandler(ParseException.class)
    public @ResponseBody
    Result parseExp(ParseException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return new Result("强制转换异常！");
    }

    /**
     * 请求超时异常
     * @param ex SocketTimeoutException异常
     * @return Result
     */
    @ExceptionHandler(SocketTimeoutException.class)
    public @ResponseBody
    Result timeoutExp(SocketTimeoutException ex){
        log.error(ex.getMessage(),ex);
        log.info("####### -----异常已记录（"+ System.currentTimeMillis()+") -----########");
        return new Result("请求超时异常！");
    }

    /**
     * 参数验证异常 -- 对于@表单提交
     * @param ex BindException异常
     * @param request HttpServletRequest
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    public @ResponseBody
    Result validateExp(BindException ex, HttpServletRequest request){
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("入参验证失败");
        log.info("请求的地址：{}", request.getRequestURI());
        log.info("字段:{}, 信息:{}", fieldError.getField(), fieldError.getDefaultMessage());
        return new Result(fieldError.getDefaultMessage() + fieldError.getField());
    }

    /**
     * 参数验证异常 -- 对于@RequestParam(required = true)
     * @param ex MissingServletRequestParameterException异常
     * @param request HttpServletRequest
     * @return Result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    Result validateExp(MissingServletRequestParameterException ex, HttpServletRequest request){
        log.info("入参验证失败");
        log.info("请求的地址：{}", request.getRequestURI());
        log.info("类型{}, 字段{}", ex.getParameterType(), ex.getParameterName(), "不能为空，必填项！");
        return new Result(ex.getParameterName() + "不能为空，必填项！类型："  + ex.getParameterType());
    }

    /**
     * 参数验证异常 -- 对于@RquestBody
     * @param ex MethodArgumentNotValidException 异常
     * @param request HttpServletRequest
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody
    Result validateExp(MethodArgumentNotValidException ex, HttpServletRequest request){
        log.info("入参验证失败");
        log.info("请求的地址：{}", request.getRequestURI());
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("字段:{}, 信息:{}", fieldError.getField(), fieldError.getDefaultMessage());
        return new Result(fieldError.getDefaultMessage() + fieldError.getField());
    }

    /**
     * 验证用户是否登陆异常 -- 对于@RequestAttribute(InsuranceConstant.REQUEST_EMP_INFO)
     * @param ex ServletRequestBindingException
     * @param request HttpServletRequest
     * @return Result
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public @ResponseBody
    Result validateExp(ServletRequestBindingException ex, HttpServletRequest request){
        String REQUEST_EMP_INFO = "REQUEST_USER_INFO";
        if (ex.getMessage().contains(REQUEST_EMP_INFO)) {
            log.info("用户验证失败");
            log.info("请求的地址：{}", request.getRequestURI());
            return new Result(CodeConts.LOGIN_FAILURE, "用户未登录（校验失败）");
        } else {
            return exp(ex);
        }
    }
}
