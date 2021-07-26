package org.sacc.SaccHome.controller;

import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.util.JwtToken;
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

    @GetMapping("/test")
    public Map<String,String> test1(@RequestHeader String token){
        Map<String,String> m = new HashMap<>();
        Claims claim = jwtToken.getClaimByToken(token);
        String role = (String)claim.get("role");
        if(!role.equals("root")){
            m.put("code","400");
            m.put("data","没有权限");
        }
        else {
            m.put("code", "200");
            m.put("data", "true");
        }
        return m;
    }
}
