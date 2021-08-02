package org.sacc.SaccHome.util;

import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.enums.RoleEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by 林夕
 * Date 2021/8/2 19:31
 */

@Component
public class RoleUtil {

    @Resource
    private JwtToken jwtToken;

    public boolean hasRole(String token, RoleEnum role){
        return hasAnyRole(token,role);
    }

    public boolean hasAnyRole(String token, RoleEnum... roleEnums){
        Claims claim = jwtToken.getClaimByToken(token);
        String role = (String)claim.get("role");
        for (RoleEnum roleEnum : roleEnums) {
            if (roleEnum.name().equals(role))
                return true;
        }
        return false;
    }
}
