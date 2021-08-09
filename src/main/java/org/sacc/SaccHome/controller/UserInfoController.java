package org.sacc.SaccHome.controller;

import io.jsonwebtoken.Claims;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.model.UserInfo;
import org.sacc.SaccHome.service.UserInfoService;
import org.sacc.SaccHome.util.JwtToken;
import org.sacc.SaccHome.vo.UserInfoVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by 林夕
 * Date 2021/8/9 19:33
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private JwtToken jwtToken;

    @GetMapping("/getThis")
    public CommonResult<UserInfoVo> getThis(@RequestHeader String token){
        Claims claims = jwtToken.getClaimByToken(token);
        String username = (String) claims.get("username");
        return CommonResult.success(userInfoService.getThis(username));
    }

    @GetMapping("/getOne")
    public CommonResult<UserInfoVo> getOne(@RequestParam Integer userId){
        return CommonResult.success(userInfoService.getOne(userId));
    }

    @PostMapping("/updateUserInfo")
    public CommonResult<Boolean> updateUserInfo(@RequestBody UserInfo userInfo,@RequestHeader String token){
        Claims claims = jwtToken.getClaimByToken(token);
        String username = (String) claims.get("username");
        return CommonResult.success(userInfoService.updateUserInfo(username,userInfo));
    }

    @PostMapping("/uploadAvatar")
    public CommonResult<String> Upload(MultipartFile file,@RequestHeader String token) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        String bucketname = "avatar";
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            boolean isExist = minioClient.bucketExists(bucketname);
            if(isExist) {
                System.out.println("文件夹已经存在了");
            }
            else {
                System.out.println("文件夹还没存在");
                minioClient.makeBucket(bucketname);
            }
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String newName = UUID.randomUUID().toString()+"."+suffix;
            minioClient.putObject(bucketname, newName, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
            Claims claims = jwtToken.getClaimByToken(token);
            String username = (String) claims.get("username");
            String imgUrl = "http://116.62.110.191:8888" + "/download/" + "?"+"bucketname=avatar"+"&" + "filename"+ "=" + URLEncoder.encode(newName);
            return CommonResult.success(userInfoService.uploadAvatar(username,imgUrl));
        }   catch(MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.error(1005,"Error occurred: " + e);
        }
    }

    @PostMapping("/updateAvatar")
    public CommonResult<String> update(MultipartFile file,@RequestHeader String token) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        String bucketname = "avatar";
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            boolean isExist = minioClient.bucketExists(bucketname);
            if(isExist) {
                System.out.println("文件夹已经存在了");
            }
            else {
                System.out.println("文件夹还没存在");
                minioClient.makeBucket(bucketname);
            }
            //删除原有头像
            Claims claims = jwtToken.getClaimByToken(token);
            String username = (String) claims.get("username");
            UserInfoVo userInfoVo = userInfoService.getThis(username);
            String url = userInfoVo.getUserInfo().getImgUrl();
            String oldFilename = URLDecoder.decode(url.substring(url.lastIndexOf("=")+1));
            minioClient.statObject(bucketname, oldFilename);
            minioClient.removeObject(bucketname, oldFilename);

            //上传当前头像
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String newName = UUID.randomUUID().toString()+"."+suffix;
            minioClient.putObject(bucketname, newName, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");

            String imgUrl = "http://116.62.110.191:8888" + "/download/" + "?"+"bucketname=avatar" +"&" + "filename"+ "=" + URLEncoder.encode(newName);
            return CommonResult.success(userInfoService.uploadAvatar(username,imgUrl));
        }   catch(MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.error(1005,"Error occurred: " + e);
        }
    }

    @GetMapping(value = "/download")
    public CommonResult<String> Download(@RequestParam String bucketname, String filename,HttpServletResponse resp) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            if (minioClient.bucketExists(bucketname)) {
                //得到bucket里test中filename的文件流
                InputStream in = minioClient.getObject(bucketname, filename, 0L);
                //读文件流
                byte[] buf = new byte[16384];
                int bytesRead;
                resp.reset();
                //Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
                // Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
                // 文件直接在浏览器上显示或者在访问时弹出文件下载对话框。
                resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
                resp.setContentType("application/x-msdownload");
                resp.setCharacterEncoding("utf-8");
                OutputStream out = new BufferedOutputStream(resp.getOutputStream());
                while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
                    out.write(buf, 0, bytesRead);//将缓冲区的数据输出到客户端浏览器
                }
                out.close();
                in.close();
                System.out.println("true");
                return CommonResult.success("true");
            } else{
                System.out.println("false");
                return CommonResult.failed("不存在的bucketname");
            }
        }catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.failed("Error occurred: " + e);
        }
    }
}
