package org.sacc.SaccHome.service;


import org.sacc.SaccHome.mbg.model.File;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.StatusResult;
import org.sacc.SaccHome.mbg.model.TaskDetails;

import java.util.List;


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
    //获取所有本账号发布的任务 按照创建时间顺排序
    List<FileTask> listFileTasksByIdByTimeAsc(String token);
    //获取所有本账号发布的任务 按照创建时间逆排序
    List<FileTask> listFileTasksByIdByTimeDesc(String token);

    //获取已经提交的文件
    TaskDetails getDetails(Integer fileTaskId);

}
