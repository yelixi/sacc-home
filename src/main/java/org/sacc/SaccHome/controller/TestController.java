package org.sacc.SaccHome.controller;

import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.enums.RoleEnum;
import org.sacc.SaccHome.util.JwtToken;
import org.sacc.SaccHome.util.RoleUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 林夕
 * Date 2021/7/26 21:08
 */
@RestController
public class TestController {
    @Resource
    private JwtToken jwtToken;

    @Resource
    private RoleUtil roleUtil;

    /**
     * 测试样例
     * @RequestHeader 从http header中取参数
     * @param token 从http header中取得的token
     */
    @GetMapping("/test")
    public Map<String,String> test1(@RequestHeader String token){
        Map<String,String> m = new HashMap<>();
        System.out.println(RoleEnum.ADMIN.name());
        if(!roleUtil.hasRole(token, RoleEnum.ADMIN)){
            m.put("code", "200");
            m.put("data", "true");
        }
        else {
            m.put("code","400");
            m.put("data","没有权限");
        }
        return m;
    }
}
