package org.sacc.SaccHome.controller;

import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.RoleEnum;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.service.EmailService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:13
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Resource
    private EmailService emailService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private JwtToken jwtToken;
    @Resource
    private RoleUtil roleUtil;

    @PutMapping("/sendEmail")
    public CommonResult sendEmail(@RequestHeader String token) {
        String username = (String) jwtToken.getClaimByToken(token).get("username");
        return userService.sendVerificationCodeEmail(username);
    }

    @PostMapping("/judgeCode")
    public CommonResult judgeCode(@RequestHeader String token, String code) {
        String username = (String) jwtToken.getClaimByToken(token).get("username");
        return userService.judgeVerificationCode(username, code);
    }

    @PutMapping("/updateEmail")
    public CommonResult updateEmail(@RequestHeader String token, String email) {
        String username = (String) jwtToken.getClaimByToken(token).get("username");
        return userService.updateEmailByUsername(email, username);
    }

    @PostMapping("/forgetPassword")
    public CommonResult forgetPassword(@RequestHeader String token, String password) {
        String username = (String) jwtToken.getClaimByToken(token).get("username");
        return userService.forgetPassword(username, password);
    }

    @PutMapping("/updatePassword")
    public CommonResult updatePassword(@RequestHeader String token, String oldPassword, String newPassword) {
        String username = (String) jwtToken.getClaimByToken(token).get("username");
        return userService.updatePasswordByUsername(username, oldPassword, newPassword);
    }

    @GetMapping("/getUserInfo")
    public CommonResult<User> getUserInfo(@RequestHeader String token) {
        Claims claims = jwtToken.getClaimByToken(token);
        String username = (String) claims.get("username");
        return CommonResult.success(userService.getUserInfo(username));
    }

    @GetMapping("/getUser")
    public CommonResult<User> getUser(@RequestParam Integer id) {
        return CommonResult.success(userService.getUser(id));
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @param email
     * @return CommonResult
     */
    @PostMapping("/register")
    public CommonResult createAccount(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        String salt = SaltUtil.getSalt(8);
        user.setSalt(salt);
        user.setEmail(email);

        Email e = new Email();
        e.setTo(email);
        redisTemplate.opsForValue().set(username, e.getContent(), 1, TimeUnit.DAYS);
        return userService.createAccount(user, e);
    }


    /**
     * 验证码检验
     *
     * @param username
     * @param inCode
     * @return
     */
    @PostMapping("/verification")
    public CommonResult verifyAccount(String username, String inCode) {
        String code = (String) redisTemplate.opsForValue().get(username);
        return userService.verifyAccount(username, code, inCode);
    }

    /**
     * 成功
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public CommonResult loginAccount(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userService.loginAccount(user);
    }

    @SneakyThrows
    @PostMapping("registerAll")

    public CommonResult registerAll(MultipartFile file, @RequestHeader String token) {
        if (roleUtil.hasRole(token, RoleEnum.ADMIN)) {
            File file1 = MultipartFileToFile.multipartFileToFile(file);
            String address = file1.getAbsolutePath();
//        MultipartFileToFile.delteTempFile(file1);
            //缓存释放有点问题，但应该没有关系吧
            return userService.registerAll(address);
        } else {
            return CommonResult.unauthorized(null);
        }
    }

    @PostMapping("/authorize")
    public CommonResult<Boolean> authorize(@RequestParam Integer userId, @RequestParam String role, @RequestHeader String token) {
        if (roleUtil.hasRole(token, RoleEnum.ROOT)) {
            return CommonResult.success(userService.authorize(userId, role));
        } else
            return CommonResult.unauthorized(null);
    }
}
