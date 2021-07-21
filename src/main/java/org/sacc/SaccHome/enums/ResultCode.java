package org.sacc.SaccHome.enums;


import lombok.AllArgsConstructor;

/**
 * 枚举了一些常用API操作码
 *  by weiwo
 */
public enum ResultCode  {
    SUCCESS(200, "操作成功"),

    FAILED(500, "操作失败"),

    INTERNAL_SERVER_ERROR(500, "服务器内部错误!"),

    NULL_POINT(500,"空指针异常"),

    VALIDATE_FAILED(404, "参数检验失败"),

    UNAUTHORIZED(401, "暂未登录或token已经过期"),

    FORBIDDEN(403, "没有相关权限"),

    UNSUPPORTED_MEDIA_TYPE(415,"请求的数据格式不符!"),

    BAD_REQUEST(400,"错误请求"),

    NOT_ZERO(1000,"非零"),

    WRONG_PASSWORD(405,"密码错误"),

    FAILED_VERIFICATION(406,"未通过验证"),

    EMAIL_SENDING_ABNORMAL(407,"邮件发送错误")



    ;
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
