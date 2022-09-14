package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.entity.PersonalizedDress;
import com.art.artadmin.service.ManageService;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcreator.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @Autowired
    private StoryService storyService;

    /**
     * 修改用户角色权限
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/updateModule")
    public IResult updateModule(@RequestBody JSONObject data){
        manageService.updateModule();
        return IResult.success();
    }

    /**
     * 添加个性装扮
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/addPersonalizedDress")
    public IResult addPersonalizedDress(@RequestBody JSONObject data){
        int res = manageService.addPersonalizedDress(data);
        if (res == 1){
            return IResult.success();
        }
        return IResult.fail("添加失败", R.CODE_FAIL);
    }

    /**
     * 展示个性装扮
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/showPersonalizedDress")
    public IResult showPersonalizedDress(@RequestBody JSONObject data){
        List<PersonalizedDress> res = manageService.showPersonalizedDress(data);
        JSONObject object = new JSONObject();
        object.put("PersonalizedDress",res);
        return IResult.success(object);
    }

    /**
     * 再次审核自动审核不通过的章节
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/checkChapter")
    public IResult checkChapter(@RequestBody JSONObject data){
        boolean flag = storyService.checkChapter(data);
        return flag ? IResult.success("审核成功",null) : IResult.fail("审核失败，已通知再次修改",R.CODE_FAIL);
    }
}
