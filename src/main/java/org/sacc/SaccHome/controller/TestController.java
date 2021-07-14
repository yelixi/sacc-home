package org.sacc.SaccHome.controller;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 林夕
 * Date 2021/7/14 14:40
 */
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public CommonResult<Boolean> test(@RequestParam Integer number){
        return CommonResult.success(testService.test(number));
    }
}
