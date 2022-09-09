package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.service.ManageService;
import com.art.artcommon.entity.IResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 * 后台管理控制器
 * @author lou
 * @create 2022/9/9
 */
@RestController
@RequestMapping("/api/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;

    @RequestMapping("/updateModule")
    public IResult updateModule(@RequestBody JSONObject data){
        manageService.updateModule();
        return IResult.success();
    }

    @RequestMapping("/addPersonalizedDress")
    public IResult addPersonalizedDress(@RequestBody JSONObject data){
        manageService.updateModule();
        return IResult.success();
    }
}
