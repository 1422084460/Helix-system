package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.PageMaster;
import com.art.artcreator.mongo.NamePublished;
import com.art.artcreator.service.StoryNameService;
import com.art.artcreator.service.StoryNovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/story")
public class StoryController {

    @Autowired
    private StoryNameService storyNameService;
    @Autowired
    private StoryNovelService storyNovelService;

    /**
     * 创建姓名
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/createName")
    @ShowArgs
    public IResult createName(@RequestBody JSONObject data) {
        try {
            List<NamePublished> packages = storyNameService.createName(data.getString("area"),
                    data.getString("category"),
                    data.getString("style"),
                    data.getIntValue("first_has_num"),
                    data.getIntValue("last_has_num"),
                    data.getBooleanValue("has_inner_name"),
                    data.getString("email"));
            List<Object> finalNameList = storyNameService.getFinalNameList(packages);
            PageMaster res = PageMaster.create(finalNameList,10);
            return IResult.success(res);
        } catch (Exception e) {
            return IResult.fail(e.getMessage(), R.CODE_FAIL);
        }
    }

    /**
     * 采用姓名
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/addAdoptedName")
    @ShowArgs
    public IResult addAdoptedName(@RequestBody JSONObject data) {
        try {
            String queryValue = data.getString("email");
            String nameId = data.getString("nameId");
            storyNameService.addAdoptedName(nameId,queryValue);
            return IResult.success(null);
        }catch (Exception e){
            return IResult.fail(null,e.getMessage(), R.CODE_FAIL);
        }
    }

    /**
     * 对名字进行评分
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/markForName")
    public IResult markForName(@RequestBody JSONObject data){
        String email = data.getString("email");
        String nameId = data.getString("nameId");
        int score = data.getIntValue("score");
        storyNameService.markForName(nameId,email,score);
        return IResult.success();
    }

    /**
     * 展示名字详细信息
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/showNameDetails")
    public IResult showNameDetails(@RequestBody JSONObject data){
        JSONObject res = new JSONObject();
        String nameId = data.getString("nameId");
        JSONObject details = storyNameService.showNameDetails(nameId);
        res.put("details",details);
        return IResult.success(res);
    }

    /**
     * 创建新章节
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/createChapter")
    public IResult createChapter(@RequestBody JSONObject data){
        boolean res = storyNovelService.createChapter(data);
        return res ? IResult.success() : IResult.fail("创建失败",R.CODE_FAIL);
    }

    /**
     * 异步发布并自动审核章节内容
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/createAndCheckChapter")
    public IResult checkPublishChapter(@RequestBody JSONObject data){
        storyNovelService.checkPublishChapter(data);
        return IResult.success("发布成功，请稍后刷新审核状态！",null);
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
        JSONObject chapter = storyNovelService.showOneChapter(email,novelName,target);
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
        String authorEmail = data.getString("email");
        String novelName = data.getString("novelName");
        List<String> result = storyNovelService.showAllChapters(authorEmail, novelName);
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
        int stat = storyNovelService.createNovel(data);
        return stat==1 ? IResult.success("创建成功",null) : IResult.fail("创建失败，请稍后重试",null);
    }

    /**
     * 界面展示指定内容
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/queryNovels")
    public IResult queryNovels(@RequestBody JSONObject data){
        JSONObject result = storyNovelService.queryNovels(data);
        return IResult.success(result);
    }

    /**
     * 修改内容并保存至草稿
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/saveChapter")
    public IResult saveChapter(@RequestBody JSONObject data){
        boolean res = storyNovelService.saveChapter(data);
        return res ? IResult.success() : IResult.fail("保存失败",R.CODE_FAIL);
    }
}
