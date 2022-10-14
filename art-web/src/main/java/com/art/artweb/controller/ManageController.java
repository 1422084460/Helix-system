package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.entity.PersonalizedDress;
import com.art.artadmin.service.UserIndividuationService;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcreator.service.StoryManageService;
import com.art.artcreator.service.StoryNovelService;
import com.art.artmanage.service.ManageSystemService;
import com.art.artmanage.service.ManageUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    private UserIndividuationService userIndividuationService;
    @Autowired
    private ManageUserService manageUserService;
    @Autowired
    private StoryManageService storyManageService;
    @Autowired
    private StoryNovelService storyNovelService;
    @Autowired
    private ManageSystemService manageSystemService;

    /**
     * 修改用户角色权限
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/updateModule")
    public IResult updateModule(@RequestBody JSONObject data){
        boolean update = manageUserService.updateModule(data);
        return update ? IResult.success() : IResult.fail("权限修改失败，请重试",R.CODE_FAIL);
    }

    /**
     * 添加个性装扮
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/addPersonalizedDress")
    public IResult addPersonalizedDress(@RequestBody JSONObject data){
        int res = userIndividuationService.addPersonalizedDress(data);
        return res == 1 ? IResult.success() : IResult.fail("添加失败", R.CODE_FAIL);
    }

    /**
     * 展示个性装扮
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/showPersonalizedDress")
    public IResult showPersonalizedDress(@RequestBody JSONObject data){
        List<PersonalizedDress> res = userIndividuationService.showPersonalizedDress(data);
        JSONObject object = new JSONObject();
        object.put("PersonalizedDress",res);
        return IResult.success(object);
    }

    /**
     * 再次审核自动审核不通过的章节
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/checkChapter")
    public IResult checkChapter(@RequestBody JSONObject data){
        boolean flag = storyNovelService.checkChapter(data);
        return flag ? IResult.success("审核成功",null) : IResult.fail("审核失败，已通知再次修改",R.CODE_FAIL);
    }

    /**
     * 帮助开发者（查询姓、名）
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/helpQuery")
    public IResult helpQuery(@RequestBody JSONObject data){
        List list = storyManageService.helpQuery(data);
        JSONObject object = new JSONObject();
        object.put("list",list);
        return IResult.success(object);
    }

    /**
     * 帮助开发者（修改姓、名）
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/helpUpdate")
    public IResult helpUpdate(@RequestBody JSONObject data){
        int update = storyManageService.helpUpdate(data);
        return update==1 ? IResult.success() : IResult.fail("修改失败",R.CODE_FAIL);
    }

    /**
     * 更新系统日志
     * @param data 请求数据
     * @return IResult
     */
    @PostMapping("/updateSystemLog")
    public IResult updateSystemLog(@RequestBody JSONObject data){
        int i = manageSystemService.updateSystemLog(data);
        return i==1 ? IResult.success() : IResult.fail("系统日志添加失败",R.CODE_FAIL);
    }
}
