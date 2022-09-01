package com.art.artcreator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcreator.entity.FirstName;
import com.art.artcreator.entity.LastName;
import com.art.artcreator.entity.NamePackage;
import com.art.artcreator.mapper.ChapterMapper;
import com.art.artcreator.mapper.FirstNameMapper;
import com.art.artcreator.mapper.LastNameMapper;
import com.art.artcreator.mapper.NovelChapterListMapper;
import com.art.artcreator.mongo.NameAdopted;
import com.art.artcommon.utils.MongoUtils;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artcreator.novel.Chapter;
import com.art.artcreator.novel.NovelChapterList;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StoryService {

    @Autowired
    private FirstNameMapper first;
    @Autowired
    private LastNameMapper last;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private NovelChapterListMapper novelChapterListMapper;

    private static final String FULL_CLASS_NAME = "com.art.artcreator.mongo.NameAdopted";

    /**
     * 创建名字总方法，默认生成30个
     * @param area 地域
     * @param has_inner_name 是否包含中间名
     * @param category 性别分类
     * @param style 风格
     * @param first_has_num 姓包含字数
     * @param last_has_num 名包含字数
     * @return List<String>
     */
    public List<String> createName(String area,String category,String style,int first_has_num,int last_has_num,boolean has_inner_name){
        StopWatch watch = new StopWatch();
        watch.start();
        int default_firstNameNum = 30;
        int default_lastNameNum = 30*2;
        List<String> finalNameList = new ArrayList<>();
        StringBuilder name = new StringBuilder();
        List<FirstName> firstNameList = createFirst(area, category, style, first_has_num);
        List<LastName> lastNameList = createLast(area, category, style);
        List<Integer> firstRandom = Tools.getRandom(Math.min(default_firstNameNum, firstNameList.size()));
        List<Integer> lastRandom = Tools.getRandom(Math.min(default_lastNameNum, lastNameList.size()));
        List<Integer> lastRandom2 = null;
        if (last_has_num!=1){
            lastRandom2 = Tools.reSort(lastRandom.size());
        }
        //筛选出的集合数小于30的情况
        int size = Math.min(firstRandom.size(),lastRandom.size());
        if (firstRandom.size()!=default_firstNameNum){
            while (size>0){
                name.append(firstNameList.get(firstRandom.get(0)).getFirst_name());
                name.append(lastNameList.get(lastRandom.get(0)).getLast_name());
                if (last_has_num!=1){
                    name.append(lastNameList.get(lastRandom2.get(0)).getLast_name());
                    lastRandom2.remove(0);
                }
                firstRandom.remove(0);
                lastRandom.remove(0);
                finalNameList.add(name.toString());
                name.delete(0,name.length());
                size--;
            }
        }else {
            //筛选出的集合数大于等于30的情况
            while (size>0){
                List<Integer> newFirstRandom = Tools.removeCursor(firstRandom, firstNameList.size());
                name.append(firstNameList.get(newFirstRandom.get(0)).getFirst_name());
                if (lastRandom.size()!=default_lastNameNum){
                    List<Integer> newLastRandom = Tools.removeCursor(lastRandom, lastNameList.size());
                    name.append(lastNameList.get(newLastRandom.get(0)).getLast_name());
                    newLastRandom.remove(0);
                    if (last_has_num!=1){
                        List<Integer> newLastRandom2 = Tools.removeCursor(lastRandom2, lastNameList.size());
                        name.append(lastNameList.get(newLastRandom2.get(0)).getLast_name());
                        newLastRandom2.remove(0);
                    }
                }else {
                    name.append(lastNameList.get(lastRandom.get(0)).getLast_name());
                    lastRandom.remove(0);
                    if (last_has_num!=1){
                        name.append(lastNameList.get(lastRandom2.get(0)).getLast_name());
                        lastRandom2.remove(0);
                    }
                }
                newFirstRandom.remove(0);
                finalNameList.add(name.toString());
                name.delete(0,name.length());
                size--;
            }
        }
        watch.stop();
        log.info("创建该组名字共花费时间"+watch.getTotalTimeMillis()+"毫秒");
        return finalNameList;
    }

    /**
     * 获取包含地域、性别偏向、风格的姓的集合
     * @param area 地域
     * @param category 性别分类
     * @param style 风格
     * @param has_num 包含字数
     * @return List<FirstName>
     */
    private List<FirstName> createFirst(String area,String category,String style,int has_num){
        if (!RedisUtil.hasKey("firstNameWith"+area)){
            redisSetFirstNameWithArea(area);
        }
        String firstNameJson = RedisUtil.get("firstNameWith" + area);
        List<FirstName> firstNameList = JSON.parseObject(firstNameJson,new TypeReference<List<FirstName>>(){});
        List<FirstName> finalFirstNameList = firstNameList.stream()
                .filter(c -> category.equals("") || c.getCategory().equals(category))
                .filter(s -> style.equals("") || s.getStyle().equals(style))
                .filter(n -> has_num==0  || n.getHas_num() == has_num)
                .collect(Collectors.toList());
        return finalFirstNameList;
    }

    /**
     * 获取包含地域、性别偏向、风格的名的集合
     * @param area 地域
     * @param category 性别分类
     * @param style 风格
     * //@param has_num 包含字数
     * @return List<LastName>
     */
    private List<LastName> createLast(String area,String category,String style){
        if (!RedisUtil.hasKey("lastNameWith"+area)){
            redisSetLastNameWithArea(area);
        }
        String lastNameJson = RedisUtil.get("lastNameWith" + area);
        List<LastName> lastNameList = JSON.parseObject(lastNameJson,new TypeReference<List<LastName>>(){});
        List<LastName> finalLastNameList = lastNameList.stream()
                .filter(c -> category.equals("") || c.getCategory().equals(category))
                .filter(s -> style.equals("") || s.getStyle().equals(style))
                .collect(Collectors.toList());
        return finalLastNameList;
    }

    /**
     * 将姓按地域划分成集合加载到 redis
     * @param area 地域
     * @return void
     */
    private void redisSetFirstNameWithArea(String area){
        QueryWrapper<FirstName> wrapper = new QueryWrapper<>();
        wrapper.eq("area",area);
        List<FirstName> firstNameList = first.selectList(wrapper);
        String s = JSON.toJSONString(firstNameList);
        RedisUtil.set("firstNameWith"+area,s,60*60, TimeUnit.SECONDS);
    }

    /**
     * 将名按地域划分成集合加载到 redis
     * @param area 地域
     * @return void
     */
    private void redisSetLastNameWithArea(String area){
        QueryWrapper<LastName> wrapper = new QueryWrapper<>();
        wrapper.eq("area",area);
        List<LastName> lastNameList = last.selectList(wrapper);
        String s = JSON.toJSONString(lastNameList);
        RedisUtil.set("lastNameWith"+area,s,60*60, TimeUnit.SECONDS);
    }

    /**
     * 将生成结果打包
     * @return List
     */
    public List<NamePackage> doPackage(List<String> nameList,String style,String category,String area){
        List<NamePackage> list = new ArrayList<>();
        int nid = 1;
        String cate;
        String a;
        if (category.equals("1")){
            cate = "男";
        }else if (category.equals("2")){
            cate = "女";
        }else {
            cate = "中性";
        }
        if (area.equals("ch")){
            a = "中式";
        }else {
            a = "非中式";
        }
        for (String name : nameList) {
            NamePackage namePackage = new NamePackage();
            namePackage.setNid(nid)
                    .setName(name)
                    .setStyle(style)
                    .setCategory(cate)
                    .setArea(a);
            list.add(namePackage);
            nid++;
        }
        return list;
    }

    /**
     * 获取对应采用名集合
     * @param email 邮箱名
     * @return List<NamePackage>
     */
    public List<NamePackage> getAdoptedName(String email){
        List<?> list = MongoUtils.queryByFilterOne(FULL_CLASS_NAME, "email", email);
        if (list!=null){
            NameAdopted n = (NameAdopted) list.get(0);
            return n.getNameList();
        }
        return null;
    }

    /**
     * 添加采用名字
     * @param name 打包名
     */
    public void addAdoptedName(NamePackage name,String email){
        List<NamePackage> list = getAdoptedName(email);
        if (list == null){
            list = new ArrayList<>();
        }
        list.add(name);
        MongoUtils.updateOne(FULL_CLASS_NAME,"email","nameList",email,list);
    }

    /**
     * 初始化表信息
     * @param email 邮箱名
     */
    public void initTableNameAdopted(String email){
        String[] f = {"email"};
        Class<?>[] c = {email.getClass()};
        Object[] o = {email};
        MongoUtils.saveOne(FULL_CLASS_NAME,f,c,o);
    }

    /**
     * 创建新章节 (预发布)
     * @param data 请求数据
     * @return int
     */
    @Transactional
    public boolean createChapter(JSONObject data){
        Long timestamp = data.getLong("timestamp");
        String email = data.getString("email");
        String authorName = data.getString("authorName");
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
                .setStatus(R.STATUS_PRE);
        NovelChapterList chapterList = new NovelChapterList()
                .setEmail(email)
                .setAuthor_name(authorName)
                .setNovel_name(novelName)
                .setChapter_id(chapter_id)
                .setParas_count(paraCurrent)
                .setPara_current(paraCurrent);
        int stat1 = 0;
        int stat2 = 0;
        try {
            stat1 = chapterMapper.insert(chapter);
            stat2 = novelChapterListMapper.insert(chapterList);
            if (stat2 == 1){
                UpdateWrapper<NovelChapterList> wrapper = new UpdateWrapper<>();
                wrapper.set("paras_count",paraCurrent)
                        .eq("email",email)
                        .eq("novel_name",novelName);
                novelChapterListMapper.update(null,wrapper);
            }
        }catch (Exception e){
            RedisUtil.set(email+"_"+novelName+"_"+paraCurrent,
                    JSON.toJSONString(chapter),
                    24,
                    TimeUnit.HOURS);
            RedisUtil.set(email+"_"+novelName+"_"+paraCurrent+"list",
                    JSON.toJSONString(chapterList),
                    24,
                    TimeUnit.HOURS);
        }
        return (stat1==stat2 && stat1==1);
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
                .apply("a.chapter_id = b.chapter_id");
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
                    .apply("a.chapter_id = b.chapter_id")
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
     * 发布并审核章节
     * @param data 请求数据
     * @return boolean
     */
    public boolean checkPublishChapter(JSONObject data){
        String is_admin = data.getString("is_admin");
        String chapter_id = data.getString("chapter_id");
        boolean flag = is_admin.equals("1");
        //管理员手动审核
        if (flag){
            UpdateWrapper<Chapter> wrapper = new UpdateWrapper<>();
            wrapper.set("status",R.STATUS_PUB)
                    .eq("chapter_id",chapter_id)
                    .eq("status",R.STATUS_EXA);
            int update = chapterMapper.update(null, wrapper);
            return update==1;
        }
        //非管理员自动审核
        QueryWrapper<Chapter> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id",chapter_id)
                .eq("status",R.STATUS_EXA);
        Chapter chapter = chapterMapper.selectOne(wrapper);
        List<Chapter.ChapterPara> detail = chapter.getDetail();
        List<String> list = new ArrayList<>();
        for (Chapter.ChapterPara c : detail){
            list.add(c.getPara());
        }
        return Tools.checkIfChapterLegal(list);
    }
}
