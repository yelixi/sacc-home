package org.sacc.SaccHome.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sacc.SaccHome.enums.ResultCode;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 04:24
 *
 * 认证异常
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthenticationException extends RuntimeException {
    private ResultCode resultCode;

    public AuthenticationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
