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
        Long timestamp = data.getLong("timestamp");
        String email = data.getString("email");
        String chapterName = data.getString("chapterName");
        String des = data.getString("details");
        List<String> details = JSON.parseObject(des,new TypeReference<List<String>>(){});
        int res = storyService.createChapter(chapterName,details,email,timestamp);
        if (res==1){
            return IResult.success();
        }
        return IResult.fail("创建失败",R.CODE_FAIL);
    }
}
