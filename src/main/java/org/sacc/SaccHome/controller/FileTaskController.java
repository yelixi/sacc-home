package org.sacc.SaccHome.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.RoleEnum;
import org.sacc.SaccHome.mbg.model.File;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.StatusResult;
import org.sacc.SaccHome.mbg.model.TaskDetails;
import org.sacc.SaccHome.service.FileTaskService;
import org.sacc.SaccHome.util.RoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * created by weiwo
 *
 */
@Controller
@Api(tags = "fileController")
@RequestMapping("/file")
public class FileTaskController {
    private static final Logger logger = LoggerFactory.getLogger(FileTaskController.class);
    @Autowired
    private FileTaskService fileTaskService;
    @Autowired
    private RoleUtil roleUtil;


    @ApiOperation(value = "获取文件任务")
    @RequestMapping(value = "/getFileTask", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<FileTask> getFileTask(@RequestParam("id") Integer id,@RequestHeader String token) {
        if(roleUtil.hasAnyRole(token, RoleEnum.MEMBER,RoleEnum.ADMIN,RoleEnum.ROOT)) {
            FileTask fileTask = fileTaskService.getFileTask(id);
            if (fileTask == null) {
                logger.debug("获取文件任务失败,文件id为:{}", id);
                return CommonResult.failed("获取文件任务失败");
            } else {
                logger.info("文件任务获取成功");
                return CommonResult.success(fileTask, "获取文件任务成功");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation(value = "创建文件任务")
    @RequestMapping(value = "/createFileTask", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createFileTask(@RequestBody FileTask fileTask,@RequestHeader String token) {
        //只需要传 任务名称 命名规则 截止时间
        if(roleUtil.hasAnyRole(token, RoleEnum.MEMBER,RoleEnum.ADMIN,RoleEnum.ROOT)) {
            if(fileTask.getName() == null || fileTask.getName().length() <= 0){
                return CommonResult.failed("文件任务名不可为空");
            }
            if(fileTask.getRule()==null||fileTask.getRule().length()<=0){
                return CommonResult.failed("命名规则不可为空");
            }
            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(fileTask.getDeadline())){
                return CommonResult.failed("截止时间不可以在当前时间之前");
            }
            if (fileTask.getDeadline()==null){
                return CommonResult.failed("截止时间不可为空");
            }
            fileTask.setUserId(fileTaskService.getUserIdByToken(token));
            fileTask.setCreatedAt(LocalDateTime.now());
            fileTask.setUpdatedAt(LocalDateTime.now());
            int var = fileTaskService.createFileTask(fileTask);
            if (var == 1) {
                logger.info("文件任务创建成功");
                return CommonResult.success(fileTask.getId(),"创建文件任务成功");
            } else {
                logger.info("文件任务创建失败");
                return CommonResult.failed("文件任务创建失败");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation(value = "删除文件任务")
    @RequestMapping(value = "/deleteFileTask", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteFileTask(@RequestParam("id") Integer id,@RequestHeader String token) {
        if(roleUtil.hasAnyRole(token, RoleEnum.ADMIN,RoleEnum.ROOT)) {
            FileTask fileTask = fileTaskService.getFileTask(id);
            if (fileTask == null) {
                return CommonResult.failed("文件任务不存在");
            }

            int i = fileTaskService.deleteFileTask(id);
            if (i == 1) {
                logger.info("文件任务删除成功");
                return CommonResult.success(null, "文件任务删除成功");

            } else {
                logger.debug("文件任务删除失败,文件任务id是:{}", id);
                return CommonResult.failed("文件任务删除失败,文件任务id是:{}" + id);
            }
        }else if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            int userIdByToken=fileTaskService.getUserIdByToken(token);
            FileTask fileTask = fileTaskService.getFileTask(id);
            int userId=fileTask.getUserId();

            if(userId==userIdByToken) {
                int i = fileTaskService.deleteFileTask(id);
                if (i == 1) {
                    logger.info("文件任务删除成功");
                    return CommonResult.success(null, "文件任务删除成功");

                } else {
                    logger.debug("文件任务删除失败,文件任务id是:{}", id);
                    return CommonResult.failed("文件任务删除失败,文件任务id是:{}" + id);
                }
            }else{
                return CommonResult.failed("此文件任务并非您创建，无法删除");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation(value = "更新文件任务")
    @RequestMapping(value = "/updateFileTask", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateFileTask(@RequestParam("id") Integer id, @RequestBody
            FileTask fileTask,@RequestHeader String token) {
        //传 文件任务名 命名规则 截止时间
        if(fileTask.getName() == null || fileTask.getName().length() <= 0){
            return CommonResult.failed("文件任务名不可为空");
        }
        if(fileTask.getRule()==null||fileTask.getRule().length()<=0){
            return CommonResult.failed("命名规则不可为空");
        }
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(fileTask.getDeadline())){
            return CommonResult.failed("截止时间不可以在当前时间之前");
        }
        if (fileTask.getDeadline()==null){
            return CommonResult.failed("截止时间不可为空");
        }
        if(roleUtil.hasAnyRole(token, RoleEnum.ADMIN,RoleEnum.ROOT)) {

            int i = fileTaskService.updateFileTask(id, fileTask);
            if (i == 1) {
                logger.info("文件任务更新成功");
                return CommonResult.success(fileTask);
            } else {
                logger.debug("更新文件任务失败,文件任务id为:{}", id);
                return CommonResult.failed("文件任务更新失败");
            }
        }else if(roleUtil.hasRole(token,RoleEnum.MEMBER)){
            int userIdByToken=fileTaskService.getUserIdByToken(token);
            FileTask fileTask1 = fileTaskService.getFileTask(id);

            if(fileTask1.getUserId()==userIdByToken){
                fileTask.setUserId(userIdByToken);
                int i = fileTaskService.updateFileTask(id, fileTask);
                if (i == 1) {
                    logger.info("文件任务更新成功");
                    return CommonResult.success(fileTask);
                } else {
                    logger.debug("更新文件任务失败,文件任务id为:{}", id);
                    return CommonResult.failed("文件任务更新失败");
                }
            }else{
                return CommonResult.failed("此文件任务非您创建,不可修改");
            }
        }else{
            return  CommonResult.unauthorized(null);
        }
    }

    @ApiOperation(value = "获取发布的所有文件任务")
    @RequestMapping(value = "/listFileTasksByTimeAsc", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<StatusResult>> listFileTaskByTimeAsc(@RequestHeader String token) {
        if (roleUtil.hasAnyRole(token, RoleEnum.ADMIN, RoleEnum.ROOT, RoleEnum.MEMBER)) {
            List<FileTask> fileTasks = fileTaskService.listFileTasksByIdByTimeAsc(token);
            List<StatusResult> list = new ArrayList<>();

            for (FileTask fileTask : fileTasks) {
                int id = fileTask.getId();
                StatusResult fileTaskStatus = fileTaskService.getFileTaskStatus(id);
                list.add(fileTaskStatus);
            }
            return CommonResult.success(list, "获取文件任务成功");
        } else {
            return CommonResult.unauthorized(null);
        }
    }
    @ApiOperation(value = "获取发布的所有文件任务")
    @RequestMapping(value = "/listFileTasksByTimeDesc", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<StatusResult>> listFileTaskByTimeDesc(@RequestHeader String token) {
        if (roleUtil.hasAnyRole(token, RoleEnum.ADMIN, RoleEnum.ROOT, RoleEnum.MEMBER)) {
            List<FileTask> fileTasks = fileTaskService.listFileTasksByIdByTimeDesc(token);
            List<StatusResult> list=new ArrayList<>();
            for (FileTask fileTask:fileTasks){
                int id= fileTask.getId();
                StatusResult fileTaskStatus = fileTaskService.getFileTaskStatus(id);
                list.add(fileTaskStatus);
            }
            return CommonResult.success(list,"获取文件任务成功");
        }else {
            return CommonResult.unauthorized(null);
        }
    }
    @ApiOperation(value = "获取已经提交的文件和提交的用户信息")
    @RequestMapping(value = "/listDetailsByTaskId", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<File>> listFilesByTaskId(@RequestParam("id") Integer id, @RequestHeader String token) {
        //需登录
        if (roleUtil.hasAnyRole(token, RoleEnum.ADMIN, RoleEnum.ROOT)){
            //特权
            List<File> details = fileTaskService.getDetails(id);
            return CommonResult.success(details,"成功获取用户任务详情");
        }else if(roleUtil.hasAnyRole(token,RoleEnum.MEMBER)){
            FileTask fileTask1 = fileTaskService.getFileTask(id);
            if(fileTask1==null){
                return CommonResult.failed("该文件任务不存在");
            }
            //通过token获取自己发布的文件任务
            List<FileTask> fileTasks = fileTaskService.listFileTasksByIdByTimeDesc(token);
            boolean belong=false;
            for(FileTask fileTask:fileTasks){
                int taskId=fileTask.getId();
                if(taskId==id){
                    belong=true;
                }
            }
            if(belong==false){
                return CommonResult.failed("不是自己发布的文件任务详情不可获取");
            }
            List<File> details = fileTaskService.getDetails(id);

            return CommonResult.success(details,"成功获取用户任务详情");
        }else {
            return CommonResult.unauthorized(null);
        }

    }

}
