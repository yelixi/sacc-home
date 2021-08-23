package org.sacc.SaccHome.service;


import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.StatusResult;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public interface FileTaskService {

//创建文件任务
    int createFileTask(FileTask fileTask) ;

//更新文件任务
    int updateFileTask(Integer id,FileTask fileTask);

//删除文件任务
    int deleteFileTask(Integer id) ;

//获取文件任务
    FileTask getFileTask(Integer id) ;

//获取文件任务状态
    StatusResult getFileTaskStatus(Integer id);
//通过token获取用户id
    int getUserIdByToken(String token);




}
