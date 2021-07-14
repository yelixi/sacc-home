package org.sacc.SaccHome.service.Impl;

import org.sacc.SaccHome.enums.ResultCode;
import org.sacc.SaccHome.exception.BusinessException;
import org.sacc.SaccHome.service.TestService;
import org.springframework.stereotype.Service;

/**
 * Created by 林夕
 * Date 2021/7/14 14:41
 */
@Service
public class TestServiceImpl implements TestService {
    @Override
    public boolean test(Integer number) {
        if (number==0)
            return true;
        else throw new BusinessException(ResultCode.NOT_ZERO);
    }
}
