package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.PageMaster;
import com.art.artcreator.entity.NamePackage;
import com.art.artcreator.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/story")
public class StoryController {

    @Autowired
    private StoryService storyService;

    /**
     * 创建姓名
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/createName")
    @ShowArgs
    public IResult createName(@RequestBody JSONObject data) {
        try {
            List<String> name = storyService.createName(data.getString("area"),
                    data.getString("category"),
                    data.getString("style"),
                    data.getIntValue("first_has_num"),
                    data.getIntValue("last_has_num"),
                    data.getBooleanValue("has_inner_name"));
            List packages = storyService.doPackage(name,
                    data.getString("style"),
                    data.getString("category"),
                    data.getString("area"));
            PageMaster res = PageMaster.create(packages,2);
            return IResult.success(res);
        } catch (Exception e) {
            return IResult.fail(null,e.getMessage(), R.CODE_FAIL);
        }
    }

    /**
     * 采用姓名
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/adoptName")
    @ShowArgs
    public IResult adoptName(@RequestBody JSONObject data) {
        try {
            String name = "com.art.artcommon.mongo.NameAdopted";
            String queryValue = data.getString("email");
            Object o = data.get("name");
            storyService.addAdoptedName((NamePackage) o,queryValue);
            return IResult.success(null);
        }catch (Exception e){
            return IResult.fail(null,e.getMessage(), R.CODE_FAIL);
        }
    }

    /**
     * 创建新章节
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/createChapter")
    public IResult createChapter(@RequestBody JSONObject data){
        boolean res = storyService.createChapter(data);
        if (res){
            return IResult.success();
        }
        return IResult.fail("创建失败",R.CODE_FAIL);
    }

    /**
     * 发布并审核章节
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/checkChapter")
    public IResult checkPublishChapter(@RequestBody JSONObject data){
        boolean flag = storyService.checkPublishChapter(data);
        if (flag){
            IResult.success("审核通过",null);
        }
        return IResult.fail("审核不通过，请联系管理员或自行修改发布内容",null);
    }

    /**
     * 获取指定章节内容
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/showOneChapter")
    public IResult showOneChapter(@RequestBody JSONObject data){
        int target = data.getIntValue("chapterNum");
        String email = data.getString("email");
        String novelName = data.getString("novelName");
        JSONObject chapter = storyService.showOneChapter(email,novelName,target);
        JSONObject object = new JSONObject();
        object.put("chapter",chapter);
        return IResult.success(object);
    }

    /**
     * 获取所有章节目录
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/showAllChapters")
    public IResult showAllChapters(@RequestBody JSONObject data){
        String email = data.getString("email");
        String novelName = data.getString("novelName");
        List<String> result = storyService.showAllChapters(email, novelName);
        JSONObject object = new JSONObject();
        object.put("chapters",result);
        return IResult.success(object);
    }

    /**
     * 创建新内容
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/createNovel")
    @ShowArgs
    public IResult createNovel(@RequestBody JSONObject data){
        int stat = storyService.createNovel(data);
        if (stat==1){
            return IResult.success("创建成功",null);
        }
        return IResult.fail("创建失败，请稍后重试",null);
    }

    /**
     * 界面展示指定内容
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/queryNovels")
    public IResult queryNovels(@RequestBody JSONObject data){
        JSONObject result = storyService.queryNovels(data);
        return IResult.success(result);
    }
}
