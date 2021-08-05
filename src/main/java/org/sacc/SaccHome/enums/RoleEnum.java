package org.sacc.SaccHome.enums;

import lombok.Getter;

/**
 * Created by 林夕
 * Date 2021/8/2 19:32
 * 暂定为这些权限
 */
@Getter
public enum RoleEnum {

    ROOT,

    MEMBER,

    ADMIN

    ;

    public static boolean isExist(String role) {
        if (role == null) {
            return false;
        }
        for (RoleEnum e : values()) {
            if (role.equals(e.name())) {
                return true;
            }
        }
        return false;
    }

}
