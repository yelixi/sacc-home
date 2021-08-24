package org.sacc.SaccHome.service.Impl;

import io.jsonwebtoken.Claims;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.mapper.ActivityMapper;
import org.sacc.SaccHome.mbg.mapper.UserInfoMapper;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.mbg.model.Activity;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.service.ActivityService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.util.JwtToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by 林夕
 * Date 2021/8/23 20:04
 */

@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private JwtToken jwtToken;

    @Resource
    private UserService userService;

    @Override
    public boolean addActivity(String token, MultipartFile file, String activityName) throws Exception {
        Claims claims = jwtToken.getClaimByToken(token);
        String username = (String) claims.get("username");
        User u = userService.getUserInfo(username);

        String bucketname = "activity";
        MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
        boolean isExist = minioClient.bucketExists(bucketname);
        if (!isExist) {
            minioClient.makeBucket(bucketname);
        }

        String filename;
        if (activityName==null)
            filename = file.getOriginalFilename();
        else filename = activityName;
        //上传
        minioClient.putObject(bucketname, filename, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");

        Activity activity = new Activity();
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());
        String url = "http://116.62.110.191:8888" + "/home/get/" + "?" + "filename" + "=" + URLEncoder.encode(Objects.requireNonNull(filename),
                StandardCharsets.UTF_8);
        activity.setFileUrl(url);
        activity.setUserId(u.getId());
        activity.setActivityName(filename);
        activityMapper.insert(activity);
        return true;
    }

    @Override
    public boolean updateActivity(String token, MultipartFile file, String activityName,Integer activityId) throws Exception {
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        String bucketname = "activity";
        MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
        minioClient.statObject(bucketname, activity.getActivityName());
        minioClient.removeObject(bucketname, activity.getActivityName());

        Claims claims = jwtToken.getClaimByToken(token);
        String username = (String) claims.get("username");
        User u = userService.getUserInfo(username);

        String filename;
        if (activityName==null)
            filename = file.getOriginalFilename();
        else filename = activityName;
        minioClient.putObject(bucketname, filename, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
        String url = "http://116.62.110.191:8888" + "/home/get/" + "?" + "filename" + "=" + URLEncoder.encode(Objects.requireNonNull(filename),
                StandardCharsets.UTF_8);
        activity.setId(activityId);
        activity.setUserId(u.getId());
        activity.setActivityName(filename);
        activity.setFileUrl(url);
        activity.setUpdatedAt(LocalDateTime.now());
        activityMapper.updateByPrimaryKeySelective(activity);
        return true;
    }

    @Override
    public boolean deleteActivity(Integer activityId) throws Exception{
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        String bucketname = "activity";
        MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
        minioClient.statObject(bucketname, activity.getActivityName());
        minioClient.removeObject(bucketname, activity.getActivityName());
        return true;
    }

    @Override
    public Activity getActivity(Integer id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Activity> getAllActivity() {
        return activityMapper.selectAll();
    }

    @Override
    public String get(String filename) throws Exception{
        MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
        String bucketname = "activity";
        InputStream in = minioClient.getObject(bucketname, filename, 0L);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}

