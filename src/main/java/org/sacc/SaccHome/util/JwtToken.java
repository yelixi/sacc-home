package org.sacc.SaccHome.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.sacc.SaccHome.mbg.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * Created by 林夕
 * Date 2021/7/26 19:30
 */
@Configuration
public class JwtToken {

    /** 秘钥 */
    @Value("${jwt.secret}")
    private String secret;

    /** 过期时间(秒) */
    @Value("${jwt.expire}")
    private long expire;


    /**
     * 生成jwt token
     */
    public String generateToken(User user) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .claim("username",user.getUsername())
                .claim("role",user.getRole())
                .compact();
    }

    public Claims getClaimByToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * token是否过期
     * @return  true：过期
     */
    public static boolean isTokenExpired(Date expiration) {
        return new Date().before(expiration);
    }

    // Getter && Setter
}
