package com.art.artcreator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artcreator.entity.FirstName;
import com.art.artcreator.entity.LastName;
import com.art.artcreator.mapper.ChapterMapper;
import com.art.artcreator.mapper.FirstNameMapper;
import com.art.artcreator.mapper.LastNameMapper;
import com.art.artcreator.mapper.NovelChapterListMapper;
import com.art.artcreator.novel.Chapter;
import com.art.artcreator.novel.NovelChapterList;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * description
 * 小说服务类
 * @author lou
 * @create 2022/9/27
 */
@Service
@Slf4j
public class StoryNovelService {

    @Autowired
    private FirstNameMapper first;
    @Autowired
    private LastNameMapper last;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private NovelChapterListMapper novelChapterListMapper;

    /**
     * 创建新小说
     * @param data 请求数据
     * @return boolean
     */
    public int createNewNovel(JSONObject data){
        String email = data.getString("email");
        String authorName = data.getString("authorName");
        String novelName = data.getString("novelName");
        String novelType = data.getString("novelType");
        String introduction = data.getString("introduction");
        String cover = data.getString("cover");
        List<String> ids = new ArrayList<>();
        NovelChapterList chapterList = new NovelChapterList()
                .setEmail(email)
                .setAuthor_name(authorName)
                .setNovel_name(novelName)
                .setCover(cover)
                .setNovel_type(novelType)
                .setIntroduction(introduction)
                .setChapter_ids(ids)
                .setParas_count(0)
                .setNovel_score("0")
                .setPara_current(0);
        return novelChapterListMapper.insert(chapterList);
    }

    /**
     * 创建新章节并保存为草稿
     * @param data 请求数据
     * @return int
     */
    @Transactional
    public boolean createChapter(JSONObject data){
        Long timestamp = data.getLong("timestamp");
        String email = data.getString("email");
        String novelName = data.getString("novelName");
        int paraCurrent = data.getIntValue("paraCurrent");
        String chapterName = data.getString("chapterName");
        String des = data.getString("details");
        List<String> details = JSON.parseObject(des,new TypeReference<List<String>>(){});
        String createTime = Tools.date_To_Str(timestamp);
        String chapter_id = Tools.getCode() + timestamp;
        List<Chapter.ChapterPara> list = new ArrayList<>();
        for (String de : details){
            Chapter.ChapterPara para = new Chapter().generatePara(de);
            list.add(para);
        }
        int count = Tools.countParas(details);
        Chapter chapter = new Chapter()
                .setEmail(email)
                .setChapter_id(chapter_id)
                .setChapterName(chapterName)
                .setDetail(list)
                .setCount(count)
                .setCreate_time(createTime)
                .setStatus(R.STATUS_DRA);
        int stat1 = 0;
        int stat2 = 0;
        try {
            stat1 = chapterMapper.insert(chapter);
            QueryWrapper<NovelChapterList> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("novel_name",novelName);
            NovelChapterList one = novelChapterListMapper.selectOne(queryWrapper);
            List<String> oldIds = one.getChapter_ids();
            oldIds.add(chapter_id);
            UpdateWrapper<NovelChapterList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("chapter_ids",oldIds)
                    .eq("email",email)
                    .eq("novel_name",novelName);
            stat2 = novelChapterListMapper.update(null,queryWrapper);
        }catch (Exception e){
            RedisUtil.set(email+"_"+novelName+"_"+paraCurrent,
                    JSON.toJSONString(chapter),
                    24,
                    TimeUnit.HOURS);//此处逻辑为先查询是否有缓存，如果有则先进行保存
        }
        return (stat1==1)&&(stat2==1);
    }

    /**
     * 获取指定章节内容
     * @param email 邮箱
     * @param novelName 作品名
     * @param target 目标
     * @return JSONObject
     */
    public JSONObject showOneChapter(String email,String novelName,int target){
        QueryWrapper<NovelChapterList> wrapper = new QueryWrapper<>();
        wrapper.eq("b.email",email)
                .eq("b.novel_name",novelName)
                .eq("b.para_current",target)
                .apply("a.novel_name = b.novel_name")
                .apply("a.email = b.email");
        return chapterMapper.queryOneChapter(wrapper);
    }

    /**
     * 获取所有章节目录
     * @param email 邮箱
     * @param novelName 作品名
     * @return List<String>
     */
    public List<String> showAllChapters(String email,String novelName){
        List<String> stringList = null;
        if (!RedisUtil.hasKey(email+"_"+novelName+"_cL")) {
            QueryWrapper<NovelChapterList> wrapper = new QueryWrapper<>();
            wrapper.eq("b.email",email)
                    .eq("b.novel_name",novelName)
                    .eq("a.status",R.STATUS_PUB)
                    .apply("a.email = b.email")
                    .apply("a.novel_name = b.novel_name")
                    .orderByAsc("b.para_current");
            List<JSONObject> list = chapterMapper.queryChapters(wrapper);
            stringList = Tools.convertChapters(list);
            RedisUtil.set(email+"_"+novelName+"_cL",JSON.toJSONString(stringList),24,TimeUnit.HOURS);
        }else {
            String s = RedisUtil.get(email + "_" + novelName + "_cL");
            stringList = JSON.parseObject(s,new TypeReference<List<String>>(){});
        }
        return stringList;
    }

    /**
     * 修改内容并保存至草稿
     * @param data 请求数据
     * @return boolean
     */
    public boolean saveChapter(JSONObject data){
        String details = data.getString("details");
        String chapterName = data.getString("chapterName");
        String chapter_id = data.getString("chapter_id");
        UpdateWrapper<Chapter> wrapper = new UpdateWrapper<>();
        wrapper.set("chapterName",chapterName)
                .set("details",details)//可能存在问题
                .eq("chapter_id",chapter_id);
        int update = chapterMapper.update(null, wrapper);
        return update==1;
    }

    /**
     * 发布并自动审核
     * @param data 请求数据
     */
    public void checkPublishChapter(JSONObject data){
        CompletableFuture.supplyAsync(()->{
            String chapter_id = data.getString("chapter_id");
            QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("chapter_id",chapter_id);
            Chapter chapter = chapterMapper.selectOne(queryWrapper);
            List<Chapter.ChapterPara> detail = chapter.getDetail();
            List<String> list = new ArrayList<>();
            for (Chapter.ChapterPara c : detail){
                list.add(c.getPara());
            }
            return Tools.checkIfChapterLegal(list);
        }).thenAccept(res->{
            if (res){
                UpdateWrapper<Chapter> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("status",R.STATUS_PUB)
                        .eq("chapter_id",data.getString("chapter_id"));
                chapterMapper.update(null,updateWrapper);
            }
        });
    }

    /**
     * 再次审核自动审核不通过的章节
     * @param data 请求数据
     * @return boolean
     */
    public boolean checkChapter(JSONObject data){
        String chapter_id = data.getString("chapter_id");
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chapter_id",chapter_id)
                .eq("status",R.STATUS_EXA);
        Chapter chapter = chapterMapper.selectOne(queryWrapper);
        List<Chapter.ChapterPara> detail = chapter.getDetail();
        List<String> list = new ArrayList<>();
        for (Chapter.ChapterPara c : detail){
            list.add(c.getPara());
        }
        boolean legal = Tools.checkIfChapterLegal(list);
        if (legal){
            UpdateWrapper<Chapter> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status",R.STATUS_PUB)
                    .eq("chapter_id",chapter_id)
                    .eq("status",R.STATUS_EXA);
            int update = chapterMapper.update(null, updateWrapper);
            return update==1;
        }
        return false;
    }

    /**
     * 创建新内容
     * @param data 请求数据
     * @return int
     */
    public int createNovel(JSONObject data){
        String s = data.toJSONString();
        NovelChapterList entity = JSON.parseObject(s, new TypeReference<NovelChapterList>(){});
        return novelChapterListMapper.insert(entity);
    }

    /**
     * 界面展示指定内容
     * @param data 请求数据
     * @return JSONObject
     */
    public JSONObject queryNovels(JSONObject data){
        String type = data.getString("novelType");
        String fuzzy = data.getString("fuzzyWord");
        QueryWrapper<NovelChapterList> wrapper = new QueryWrapper<>();
        if (type.equals("0")){
            wrapper.ne("novelType",type);
        }else {
            wrapper.eq("novelType",type);
        }
        wrapper.like(!fuzzy.equals(""),"novel_name",fuzzy);
        wrapper.groupBy("novel_name");
        List<JSONObject> chapterLists = novelChapterListMapper.queryUniqueChaptersForShow(wrapper);
        JSONObject o = new JSONObject();
        o.put("NovelsResult",chapterLists);
        return o;
    }

    /**
     * 帮助开发者（查询姓、名）
     * @param data 请求数据
     * @return List
     */
    public List helpQuery(JSONObject data){
        String flag = data.getString("flag");
        List list;
        if (flag.equals("first")){
            QueryWrapper<FirstName> wrapper = new QueryWrapper<>();
            list = first.selectList(wrapper);
        }else {
            QueryWrapper<LastName> wrapper = new QueryWrapper<>();
            list = last.selectList(wrapper);
        }
        return list;
    }
}
