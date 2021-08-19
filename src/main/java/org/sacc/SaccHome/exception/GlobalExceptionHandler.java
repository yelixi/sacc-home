package org.sacc.SaccHome.exception;

import lombok.extern.slf4j.Slf4j;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.ResultCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * Created by 林夕
 * Date 2021/7/14 13:53
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有异常
     * @return 返回“服务器内部错误”
     */
    /*@ExceptionHandler(Exception.class)
    public CommonResult<ResultCode> exception(){
        return CommonResult.error(ResultCode.INTERNAL_SERVER_ERROR);
    }
*/
    /**
     * 全局处理NullPointerException类型的异常
     */
    @ExceptionHandler(NullPointerException.class)
    public CommonResult<ResultCode> NullPointerException(NullPointerException e){
        log.error("空指针异常",e);
        return CommonResult.error(ResultCode.NULL_POINT);

    }
    /**
     * 捕获请求方式异常
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public CommonResult<ResultCode> HttpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("错误请求",e);
        return CommonResult.error(ResultCode.BAD_REQUEST);
    }

    /**
     * 捕获400异常
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public CommonResult<ResultCode> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error("错误请求",e);
        return CommonResult.error(ResultCode.BAD_REQUEST);
    }

    /**
     *捕获数据库操作异常
     */
    @ExceptionHandler(value = SQLException.class)
    public CommonResult<ResultCode> SQLExceptionHandler(SQLException e){
        log.error("数据库操作异常:",e);
        return CommonResult.error(e.getErrorCode(),e.getMessage());
    }

    /**
     *捕获数据校验异常
     */
    @ExceptionHandler(value = BindException.class)
    public CommonResult<ResultCode> bindExceptionHandler(BindException e){
        log.error("数据校验异常",e);
        return CommonResult.error(ResultCode.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * 捕获自定义异常
     * */
    @ExceptionHandler(value = BusinessException.class)
    public CommonResult<ResultCode> businessException(BusinessException e){
        return CommonResult.error(e.getResultCode());
    }

}
