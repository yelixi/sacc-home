package org.sacc.SaccHome.service.Impl;

import cn.hutool.crypto.SecureUtil;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.ResultCode;
import org.sacc.SaccHome.exception.AuthenticationException;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.service.EmailService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.util.JwtToken;
import org.sacc.SaccHome.util.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:15
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtToken jwtToken;


    @Override
    public User getUser(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<User> getUsersByFileTask(Integer id) {
        return null;
    }

    @Override
    public String getEmailByUsername(String username) {
        return userMapper.getEmailByUsername(username);
    }

    @Override
    public void updateEmailByUsername(String username, String email) {
        //如果redis中对应value为true，表示用户没有通过刚才的验证（防止用户直接通过URL修改）
        String value = null;
        try {
            value = (String) redisTemplate.opsForValue().get(username);
        } catch (Exception e) {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        }
        if (value.equals("true")) {
            userMapper.updateEmailByUsername(username, email);
        } else {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        }
    }

    @Override
    public void updatePasswordByUsername(String username, String oldPassword, String newPassword) {
        //todo 等写登录和注册的xdm确定了密码加密方式后修改，目前仅作明文相等判断
        if (userMapper.findPasswordByUsername(username) == oldPassword) {
            userMapper.updatePasswordByUsername(username, newPassword,"");
        } else {
            throw new AuthenticationException(ResultCode.WRONG_PASSWORD);
        }
    }

    @Override
    public void forgetPassword(String username, String password) {
        String value = null;
        try {
            value = (String) redisTemplate.opsForValue().get(username);
        } catch (Exception e) {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        }
        //如果redis中对应value为true，表示用户没有通过刚才的验证（防止用户直接通过URL修改）
        if (value.equals("true")) {
            //todo 等密码加密方式
            userMapper.updatePasswordByUsername(username, password,"");
        } else {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        }
    }

    @Override
    public boolean judgeVerificationCode(String username, String inputVerificationCode) {
        String LocalVerificationCode = null;
        try {
            LocalVerificationCode = (String) redisTemplate.opsForValue().get(username);
        } catch (Exception e) {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        }
        if (LocalVerificationCode.equals(inputVerificationCode)) {
            //删除相应的key并返回true
            redisTemplate.opsForValue().set(username, "true", 1, TimeUnit.HOURS);
            return true;
        }
        return false;
    }

    @Override
    public void sendVerificationCodeEmail(String username) {
        String email = this.getEmailByUsername(username);
        String verificationCode = VerificationCodeGenerator.getVerificationCode(6);
        redisTemplate.opsForValue().setIfAbsent(username, verificationCode, 1, TimeUnit.HOURS);
        emailService.sendSimpleMail(email, verificationCode);
    }

    /*
    注册账号
    * */
    public CommonResult createAccount(User user) {
        String mdPwd = SecureUtil.md5(user.getPassword() + user.getSalt());
        user.setPassword(mdPwd);
        user.setCreatedAt(LocalDateTime.now());
        User u = userMapper.loginUser(user.getUsername());
        if (u!=null&&u.getJudge() == 1)
            return CommonResult.failed("该账号已经完成注册并验证了");

        List<User> result1 = userMapper.selectUserByUserName(user.getUsername());
        if (result1.isEmpty()) {
            int result2 = userMapper.insertUser(user);
            if (result2 > 0) {
                return CommonResult.success(null, "操作成功，请输入验证码验证码");
            } else {
                return CommonResult.failed("操作失败");
            }
        } else {
            return CommonResult.failed("已经注册过,但未验证");
        }
    }
    /**
     * 登录账号
     * @param user
     * @return
     */
    public CommonResult loginAccount(User user){
        //先根据用户名查询到该用户
        User md5 = userMapper.loginUser(user.getUsername());
        //判断是否能查询到
        //否则提醒进行激活
        if(md5==null||!md5.getUsername().equals(user.getUsername())){
            return CommonResult.failed("该账号未验证或不存在！");
        }
        //判断该用户是否激活
        //若激活，则继续判断密码是否正确

        String md5Pwd = SecureUtil.md5(user.getPassword()+md5.getSalt());
        if(!md5Pwd.equals(md5.getPassword())){
            return CommonResult.wrongPassword(405,"密码错误");
        } else {
            String token = jwtToken.generateToken(user);
            Map<String,String> m = new HashMap<>();
            m.put("token",token);
            return CommonResult.success(m,"登录成功");
        }
    }


    /**
     * 验证码
     * @param inCode
     * @param code
     * @return
     */
    public CommonResult verifyAccount(String username,String code, String inCode) {


        if (inCode.equals(code)){
            userMapper.updateUserByUserName(username);
            return CommonResult.success(null,"验证成功");
        } else {
            return CommonResult.failed("验证失败");
        }
    }


    /**
     * 使用Apache POI
     *
     * @param address
     * @return
     */
    @SneakyThrows
    public CommonResult registerAll(String address) {
        User user = new User();
        user.setSalt();
        String mdPwd = SecureUtil.md5("123456" + user.getSalt());
        user.setPassword(mdPwd);
        user.setCreatedAt(LocalDateTime.now());
        user.setJudge((byte) 1);
        FileInputStream inputStream = new FileInputStream(address);
        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rowTotalCount = sheet.getLastRowNum();
        int i = 1;
        for (i = 1;i<=rowTotalCount ; i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(0);
            String temp = cell.getStringCellValue();
            if (!temp.equals(null)) {
                user.setUsername(temp);
                user.setEmail(temp.toLowerCase()+"@njupt.edu.cn");
                int result = userMapper.insertUser(user);
                if (result <= 0){
                    inputStream.close();
                    return CommonResult.failed("操作失败");
                }
            } else {
                break;
            }
        }
        inputStream.close();
        return CommonResult.success(null,"录入结束");
    }
}
