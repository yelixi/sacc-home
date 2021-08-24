package org.sacc.SaccHome.service;

import io.minio.errors.*;
import org.sacc.SaccHome.mbg.model.Activity;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by 林夕
 * Date 2021/8/23 20:03
 */
public interface ActivityService {
    boolean addActivity(String token, MultipartFile file, String activityName) throws Exception;

    boolean updateActivity(String token, MultipartFile file, String activityName,Integer activityId) throws Exception;

    boolean deleteActivity(Integer activityId) throws Exception;

    Activity getActivity(Integer id);

    List<Activity> getAllActivity();

    String get(String filename) throws Exception;
}
