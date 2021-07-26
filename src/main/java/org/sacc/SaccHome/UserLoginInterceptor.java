package org.sacc.SaccHome;

import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.enums.ResultCode;
import org.sacc.SaccHome.exception.BusinessException;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.util.JwtToken;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by 林夕
 * Date 2021/7/12 18:55
 */

public class UserLoginInterceptor implements HandlerInterceptor {

    @Resource
    private JwtToken jwtToken;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        System.out.println(token);
        Claims claimByToken = jwtToken.getClaimByToken(token);
        String username = (String) claimByToken.get("username");
        String role = (String) claimByToken.get("role");
        Date date = claimByToken.getExpiration();
        boolean b = JwtToken.isTokenExpired(date);
        System.out.println("username:"+username+"role:"+role);
        if(userMapper.loginUser(username)==null)
            throw new BusinessException(ResultCode.TOKEN_IS_NOT_EXIT);
        else if(!b)
            throw  new BusinessException(ResultCode.TOKEN_IS_PASSED);
        return username.equals("admin") && role.equals("admin")&&b;
    }
}
