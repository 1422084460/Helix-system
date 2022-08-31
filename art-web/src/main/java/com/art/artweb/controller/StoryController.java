package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcreator.entity.NamePackage;
import com.art.artcreator.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            JSONObject object = new JSONObject();
            List<NamePackage> packages = storyService.doPackage(name,
                    data.getString("style"),
                    data.getString("category"),
                    data.getString("area"));
            object.put("nameList",packages);
            object.put("listSize",name.size());
            return IResult.success(object);
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
     * 获取所有章节
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/showAllChapters")
    public IResult showAllChapters(@RequestBody JSONObject data){
        return IResult.success();
    }
}
