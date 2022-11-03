package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.PageMaster;
import com.art.artcreator.dto.ChapterInfo;
import com.art.artcreator.dto.CreatorBaseInfo;
import com.art.artcreator.dto.NameBaseInfo;
import com.art.artcreator.dto.NovelInfo;
import com.art.artcreator.mongo.NamePublished;
import com.art.artcreator.service.StoryNameService;
import com.art.artcreator.service.StoryNovelPageService;
import com.art.artcreator.service.StoryNovelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * description
 * 小说管理控制器
 * @author lou
 * @create 2022/9/9
 */
@Api("小说管理接口")
@RestController
@RequestMapping("/api/story")
public class StoryController {

    @Autowired
    private StoryNameService storyNameService;
    @Autowired
    private StoryNovelService storyNovelService;
    @Autowired
    private StoryNovelPageService storyNovelPageService;

    /**
     * 创建姓名
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("创建姓名")
    @PostMapping("/createName")
    @ShowArgs
    public IResult createName(@RequestBody @Valid NameBaseInfo data) {
        try {
            List<NamePublished> packages = storyNameService.createName(
                    data.getArea(),
                    data.getCategory(),
                    data.getStyle(),
                    data.getFirst_has_num(),
                    data.getLast_has_num(),
                    data.getHas_inner_name(),
                    data.getEmail());
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
    @ApiOperation("采用姓名")
    @PostMapping("/addAdoptedName")
    @ShowArgs
    public IResult addAdoptedName(@RequestBody @Valid CreatorBaseInfo data) {
        try {
            storyNameService.addAdoptedName(data.getNameId(),data.getEmail());
            return IResult.success();
        }catch (Exception e){
            return IResult.fail(e.getMessage(), R.CODE_FAIL);
        }
    }

    /**
     * 对名字进行评分
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("评分")
    @PostMapping("/markForName")
    public IResult markForName(@RequestBody @Valid CreatorBaseInfo data){
        storyNameService.markForName(data.getNameId(),data.getEmail(),data.getScore());
        return IResult.success();
    }

    /**
     * 展示名字详细信息
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("展示名字详细信息")
    @PostMapping("/showNameDetails")
    public IResult showNameDetails(@RequestBody @Valid CreatorBaseInfo data){
        JSONObject res = new JSONObject();
        JSONObject details = storyNameService.showNameDetails(data.getNameId());
        res.put("details",details);
        return IResult.success(res);
    }

    /**
     * 创建新章节
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("创建新章节")
    @PostMapping("/createChapter")
    public IResult createChapter(@RequestBody @Valid ChapterInfo data){
        System.out.println(data.getDetail());
        boolean res = storyNovelService.createChapter(
                data.getTimestamp(),
                data.getEmail(),
                data.getNovelName(),
                data.getParaCurrent(),
                data.getChapterName(),
                data.getDetail());
        return res ? IResult.success() : IResult.fail("创建失败",R.CODE_FAIL);
    }

    /**
     * 异步发布并自动审核章节内容
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("发布并审核章节")
    @PostMapping("/createAndCheckChapter")
    public IResult checkPublishChapter(@RequestBody ChapterInfo data){
        storyNovelService.checkPublishChapter(data.getChapter_id());
        return IResult.success("发布成功，请稍后刷新审核状态！",null);
    }

    /**
     * 获取指定章节内容
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("获取章节")
    @PostMapping("/showOneChapter")
    public IResult showOneChapter(@RequestBody ChapterInfo data){
        JSONObject chapter = storyNovelService.showOneChapter(data.getEmail(),data.getNovelName(),data.getParaCurrent());
        JSONObject object = new JSONObject();
        object.put("chapter",chapter);
        return IResult.success(object);
    }

    /**
     * 获取所有章节目录
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("获取所有章节目录")
    @PostMapping("/showAllChapters")
    public IResult showAllChapters(@RequestBody ChapterInfo data){
        List<String> result = storyNovelService.showAllChapters(data.getEmail(), data.getNovelName());
        JSONObject object = new JSONObject();
        object.put("chapters",result);
        return IResult.success(object);
    }

    /**
     * 创建新小说
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("创建新内容")
    @PostMapping("/createNovel")
    @ShowArgs
    public IResult createNovel(@RequestBody @Valid NovelInfo data){
        int stat = storyNovelService.createNovel(data);
        return stat==1 ? IResult.success("创建成功",null) : IResult.fail("创建失败，请稍后重试",null);
    }

    /**
     * 界面展示指定类型小说
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("界面展示")
    @PostMapping("/queryNovels")
    public IResult queryNovels(@RequestBody NovelInfo data){
        JSONObject result = storyNovelService.queryNovels(data.getNovel_type(),data.getFuzzyWord());
        return IResult.success(result);
    }

    /**
     * 修改内容并保存至草稿
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("修改并保存内容")
    @PostMapping("/saveChapter")
    public IResult saveChapter(@RequestBody JSONObject data){
        boolean res = storyNovelService.saveChapter(data);
        return res ? IResult.success() : IResult.fail("保存失败",R.CODE_FAIL);
    }

    /**
     * 展示首页小说各类排行榜
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("展示小说排行榜")
    @PostMapping("/showNovelsRank")
    public IResult showNovelsRank(@RequestBody JSONObject data){
        JSONObject object = new JSONObject();
        Set<String> result = storyNovelPageService.showNovelsRank(data.getString("sortMode"), data.getBooleanValue("asc"));
        object.put("set",result);
        return IResult.success(object);
    }
}
