package org.sacc.SaccHome.util;

import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.enums.RoleEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by 林夕
 * Date 2021/8/2 19:31
 *
 * 权限判断工具类
 */

@Component
public class RoleUtil {

    @Resource
    private JwtToken jwtToken;

    /**
     * 是否拥有特定权限
     * @param token 传入的token
     * @param role 需要判断的权限
     * @return 有返回true否则返回false
     */
    public boolean hasRole(String token, RoleEnum role){
        return hasAnyRole(token,role);
    }

    /**
     * 是否拥有多个权限中的一个
     * @param token 传入的token
     * @param roleEnums 多个权限
     * @return 有返回true否则返回false
     */
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
