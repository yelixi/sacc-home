package org.sacc.SaccHome.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.RoleEnum;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.StatusResult;
import org.sacc.SaccHome.service.FileTaskService;
import org.sacc.SaccHome.util.RoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


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
        if(roleUtil.hasAnyRole(token, RoleEnum.MEMBER,RoleEnum.ADMIN,RoleEnum.ROOT)) {
            fileTask.setUserId(fileTaskService.getUserIdByToken(token));
            fileTask.setCreatedAt(LocalDateTime.now());
            fileTask.setUpdatedAt(LocalDateTime.now());
            int var = fileTaskService.createFileTask(fileTask);
            if (var == 1) {
                logger.info("文件任务创建成功");
                return CommonResult.success(fileTask);
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
    public CommonResult deleteFileTask(@RequestParam("id") Integer id,@RequestParam int userId,@RequestHeader String token) {
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
            int userId=fileTask.getUserId();
            if(userId==userIdByToken){
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

    @ApiOperation(value = "获取文件任务状态")
    @RequestMapping(value = "/getFileTaskStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<StatusResult> getFileTaskStatus(@RequestParam("id") Integer id,@RequestParam int userId, @RequestHeader String token) {
        //获取剩余时间
        if (roleUtil.hasAnyRole(token, RoleEnum.ADMIN, RoleEnum.ROOT)) {
            StatusResult statusResult = fileTaskService.getFileTaskStatus(id);
            return CommonResult.success(statusResult, "获取文件任务状态成功");

        }else if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            int userIdByToken=fileTaskService.getUserIdByToken(token);
            if(userId==userIdByToken){
               StatusResult statusResult=fileTaskService.getFileTaskStatus(id);
               if(statusResult==null){
                   return CommonResult.failed("获取文件任务状态失败");
               }
                return CommonResult.success(statusResult,"获取文件任务状态成功");
            }else{
                return CommonResult.failed("此文件任务非您创建,不可获取");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }
}
