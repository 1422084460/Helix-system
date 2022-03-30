package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.NamePackage;
import com.art.artservice.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/story")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @RequestMapping("/createName")
    public IResult createName(@RequestBody String data) {
        System.out.println(data);
        Map map = JSON.parseObject(data);
        try {
            List<String> name = storyService.createName(map.get("area").toString(),
                    map.get("category").toString(),
                    map.get("style").toString(),
                    Integer.parseInt(map.get("first_has_num").toString()),
                    Integer.parseInt(map.get("last_has_num").toString()),
                    Boolean.parseBoolean(map.get("has_inner_name").toString()));
            JSONObject object = new JSONObject();
            List<NamePackage> packages = storyService.doPackage(name,
                    map.get("style").toString(),
                    map.get("category").toString(),
                    map.get("area").toString());
            object.put("nameList",packages);
            object.put("listSize",name.size());
            return IResult.success(object);
        } catch (Exception e) {
            return IResult.fail(null,e.getMessage(), R.CODE_FAIL);
        }
    }

    @RequestMapping("/adoptName")
    public String adoptName(@RequestBody String data) {
        return "success";
    }

    @RequestMapping("/test")
    public String test(@RequestBody String data) {
        return "success";
    }
}
