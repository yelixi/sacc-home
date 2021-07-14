package org.sacc.SaccHome.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sacc.SaccHome.enums.ResultCode;

/**
 * Created by 林夕
 * Date 2021/7/14 14:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException{

    private ResultCode resultCode;

    public BusinessException(ResultCode resultCode){
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
