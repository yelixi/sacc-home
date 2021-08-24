package org.sacc.SaccHome.controller;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.model.Activity;
import org.sacc.SaccHome.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林夕
 * Date 2021/8/23 20:03
 */

@RestController
@RequestMapping("/home")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @GetMapping("/getAllActivity")
    public CommonResult<List<Activity>> getAllActivity(){
        return CommonResult.success(activityService.getAllActivity());
    }

    @GetMapping("/getActivity")
    public CommonResult<Activity> getActivity(@RequestParam Integer activityId){
        return CommonResult.success(activityService.getActivity(activityId));
    }

    @PostMapping("/addActivity")
    public CommonResult<Boolean> addActivity(@RequestHeader String token, MultipartFile file,String activityName) throws Exception {
        return CommonResult.success(activityService.addActivity(token,file,activityName));
    }

    @PostMapping("/updateActivity")
    public CommonResult<Boolean> updateActivity(@RequestHeader String token, MultipartFile file,String activityName,Integer activityId) throws Exception {
        return CommonResult.success(activityService.updateActivity(token,file,activityName,activityId));
    }

    @DeleteMapping("/deleteActivity")
    public CommonResult<Boolean> deleteActivity(@RequestParam Integer activityId) throws Exception {
        return CommonResult.success(activityService.deleteActivity(activityId));
    }

    @GetMapping("/get")
    public CommonResult<String> get(@RequestParam String filename) throws Exception {
        return CommonResult.success(activityService.get(filename));
    }
}
