package org.sacc.SaccHome.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sacc.SaccHome.enums.ResultCode;

/**
 * 通用返回对象
 * Created by weiwo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;

    public CommonResult(long code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param resultCode 错误码
     */
    public static <T> CommonResult<T> failed(ResultCode resultCode) {
        return new CommonResult<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    /**
     * 出现异常时的返回结果
     */
    public static <T> CommonResult<T> error(ResultCode resultCode){
        return new CommonResult<T>(resultCode.getCode(),resultCode.getMessage());
    }

    /**
     * 出现异常时的返回结果
     */
    public static <T> CommonResult<T> error(long code,String message){
        return new CommonResult<T>(code,message);
    }

    /**
     * 密码错误
     */
    public static <T> CommonResult<T> wrongPassword(long code,String message){
        return new CommonResult<T>(code,message);
    }
}
