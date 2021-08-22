package org.sacc.SaccHome.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.StatusResult;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.mbg.model.UserParam;
import org.sacc.SaccHome.service.FileTaskService;
import org.sacc.SaccHome.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * created by weiwo
 *
 */
@Controller
@Api(tags = "fileController" ,description = "weiwo-任务3-文件任务")
@RequestMapping("/file")
public class FileTaskController {
    private static final Logger logger= LoggerFactory.getLogger(FileTaskController.class);
    @Autowired
    private FileTaskService fileTaskService;
    @Autowired
    private UserService userService;



    @ApiOperation(value = "获取文件任务")
    @RequestMapping(value = "/getFileTask",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<FileTask> getFileTask(@RequestParam("id") Integer id){
        FileTask fileTask = fileTaskService.getFileTask(id);
        if(fileTask==null) {
            logger.debug("获取文件任务失败,文件id为:{}",id);
            return CommonResult.failed("获取文件任务失败");
        }else{
            logger.info("文件获取成功");
            return CommonResult.success(fileTask,"获取文件任务成功");
        }

    }

    @ApiOperation(value ="创建文件任务")
    @RequestMapping(value = "/createFileTask",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createFileTask(@RequestBody FileTask fileTask){
        int var = fileTaskService.createFileTask(fileTask);


        if(var==1){
            logger.info("文件创建成功");
            return CommonResult.success(fileTask);
        }else{
            logger.info("文件创建失败");
            return CommonResult.failed("文件创建失败");
        }



    }
    @ApiOperation(value = "删除文件任务")
    @RequestMapping(value = "/deleteFileTask",method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult    deleteFileTask(@RequestParam("id") Integer id){
        FileTask fileTask = fileTaskService.getFileTask(id);
        if(fileTask==null){
            return CommonResult.failed("文件不存在");
        }

        int i = fileTaskService.deleteFileTask(id);
        if(i==1){
            logger.info("文件删除成功");
            return CommonResult.success(null,"文件删除成功");

        }else{
            logger.debug("文件删除失败,文件id是:{}",id);
            return CommonResult.failed("文件删除失败,文件id是:{}"+id);
        }
    }

    @ApiOperation(value = "更新文件任务")
    @RequestMapping(value = "/updateFileTask",method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateFileTask(@RequestParam("id") Integer id,@RequestBody
            FileTask fileTask){
        int i = fileTaskService.updateFileTask(id, fileTask);
        if(i==1){
            logger.info("文件更新成功");
            return CommonResult.success(fileTask);
        }else{
            logger.debug("更新文件任务失败,文件id为:{}",id);
            return CommonResult.failed("文件更新失败");
        }
    }

    @ApiOperation(value = "获取文件任务状态")
    @RequestMapping(value = "/getFileTaskStatus",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<StatusResult> getFileTaskStatus(@RequestParam("id") Integer id){
        //获取剩余时间
        StatusResult statusResult=new StatusResult();
        FileTask fileTask = fileTaskService.getFileTask(id);
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime deadline=fileTask.getDeadline();
        Duration duration=Duration.between(now,deadline);
        String hours = String.valueOf(duration.toHoursPart());
        String days= String.valueOf(duration.toDaysPart());

        String seronds = String.valueOf(duration.toSecondsPart());
        String minutes = String.valueOf(duration.toMinutesPart());
        String exp=days+"天,"+hours+"小时,"+minutes+"分钟,"+seronds+"秒";
        System.out.println(exp);
        statusResult.setExp_left(exp);
        //获取提交的用户列表
        List<User> users = userService.getUsersByFileTask(id);
        List<UserParam> userParams=new ArrayList<>();

        for(User user:users){
            UserParam userParam=new UserParam();
            userParam.setId(user.getId());
            userParam.setUseName(user.getUsername());
            userParams.add(userParam);
        }
        statusResult.setCommittedUsers(userParams);
        //获取提交的用户数
        int numsCommitted =userParams.size();
        statusResult.setNumsCommitted(numsCommitted);
        Integer userId = fileTask.getId();
        statusResult.setId(userId);
        return CommonResult.success(statusResult,"获取文件任务状态成功");
    }





}
