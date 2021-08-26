package org.sacc.SaccHome.service.Impl;

import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.mapper.FileMapper;
import org.sacc.SaccHome.mbg.mapper.FileTaskMapper;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.mbg.model.*;
import org.sacc.SaccHome.service.FileTaskService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileTaskServiceImpl implements FileTaskService {
    @Autowired
    private FileTaskMapper fileTaskMapper;
    @Autowired
    private FileTaskService fileTaskService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private JwtToken jwtToken;

    @Override
    public int getUserIdByToken(String token) {
        Claims claim=jwtToken.getClaimByToken(token);
        String username= (String) claim.get("username");
        List<User> users=userMapper.selectUser(username);
        int id = 0;
        for(User user:users){
            id=user.getId();
        }
        return id;
    }


    @Override
    public StatusResult getFileTaskStatus(Integer id) {
        StatusResult statusResult = new StatusResult();
        FileTask fileTask = fileTaskService.getFileTask(id);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = fileTask.getDeadline();
        Duration duration = Duration.between(now, deadline);
        String hours = String.valueOf(duration.toHoursPart());
        String days = String.valueOf(duration.toDaysPart());

        String seronds = String.valueOf(duration.toSecondsPart());
        String minutes = String.valueOf(duration.toMinutesPart());
        String exp = days + "天," + hours + "小时," + minutes + "分钟," + seronds + "秒";
        System.out.println(exp);
        statusResult.setExp_left(exp);
        //获取提交的用户列表
        List<User> users = userService.getUsersByFileTask(id);
//        List<UserParam> userParams = new ArrayList<>();
//        for (User user : users) {
//            UserParam userParam = new UserParam();
//            userParam.setId(user.getId());
//            userParam.setUseName(user.getUsername());
//            userParams.add(userParam);
//        }

        //获取提交的用户数
        int numsCommitted = users.size();
        statusResult.setNumsCommitted(numsCommitted);
        statusResult.setTaskName(fileTask.getName());
        statusResult.setTaskId(fileTask.getId());
        return statusResult;
    }

    @Override
    public int createFileTask(FileTask fileTask){
        int i = fileTaskMapper.insertSelective(fileTask);
        return i;
    }



    @Override
    public int updateFileTask(Integer id, FileTask fileTask) {
        fileTask.setUpdatedAt(LocalDateTime.now());
        fileTask.setId(id);
        return fileTaskMapper.updateByPrimaryKeySelective(fileTask);
    }

    @Override
    public List<FileTask> listFileTasksByIdByTimeDesc(String token) {
        Claims claim=jwtToken.getClaimByToken(token);
        String username= (String) claim.get("username");
        List<User> users=userMapper.selectUser(username);
        int id = 0;
        for(User user:users){
            id=user.getId();
        }
        //现在这个id是当前用户id
        FileTaskExample example=new FileTaskExample();
        example.createCriteria().andUserIdEqualTo(id);
        example.setOrderByClause("created_at desc");
        List<FileTask> fileTasks = fileTaskMapper.selectByExample(example);
        return fileTasks;
    }



    @Override
    public List<FileTask> listFileTasksByIdByTimeAsc(String token) {
        Claims claim=jwtToken.getClaimByToken(token);
        String username= (String) claim.get("username");
        List<User> users=userMapper.selectUser(username);
        int id = 0;
        for(User user:users){
            id=user.getId();
        }
        //现在这个id是当前用户id
        FileTaskExample example=new FileTaskExample();
        example.createCriteria().andUserIdEqualTo(id);
        example.setOrderByClause("created_at asc");
        List<FileTask> fileTasks = fileTaskMapper.selectByExample(example);
        return fileTasks;
    }

    @Override
    public int deleteFileTask(Integer id) {
        return fileTaskMapper.deleteByPrimaryKey(id);
    }

    @Override
    public FileTask getFileTask(Integer id) {
        return fileTaskMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<File> getDetails(Integer fileTaskId) {
//        FileExample example=new FileExample();
//        example.createCriteria().andFileTaskIdEqualTo(fileTaskId);
//        List<File> files = fileMapper.selectByExample(example);
        //通过文件任务id获取文件
        List<File> files = fileTaskMapper.getFilesByFileTaskId(fileTaskId);
//        List<User> users = userService.getUsersByFileTask(fileTaskId);
//        List<UserParam> userParams = new ArrayList<>();
//        for (User user : users) {
//            UserParam userParam = new UserParam();
//            userParam.setId(user.getId());
//            userParam.setUseName(user.getUsername());
//            userParams.add(userParam);
//        }
//        TaskDetails taskDetails=new TaskDetails();
//        taskDetails.setFiles(files);
//        taskDetails.setUserInfos(userParams);
        return files;
    }




}
