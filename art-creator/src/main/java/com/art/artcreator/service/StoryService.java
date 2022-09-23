package com.art.artcreator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcreator.entity.FirstName;
import com.art.artcreator.entity.LastName;
//import com.art.artcreator.entity.NamePackage;
import com.art.artcreator.mapper.ChapterMapper;
import com.art.artcreator.mapper.FirstNameMapper;
import com.art.artcreator.mapper.LastNameMapper;
import com.art.artcreator.mapper.NovelChapterListMapper;
import com.art.artcreator.mongo.NameAdopted;
import com.art.artcommon.utils.MongoClient;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artcreator.mongo.NamePublished;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    /**
     * 创建名字总方法，默认生成30个
     * @param area 地域
     * @param has_inner_name 是否包含中间名
     * @param category 性别分类
     * @param style 风格
     * @param first_has_num 姓包含字数
     * @param last_has_num 名包含字数
     * @return List<NamePublished>
     */
    public List<NamePublished> createName(String area,String category,String style,int first_has_num,int last_has_num,boolean has_inner_name,String email){
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
        int nid = 1;
        List<NamePublished> lists = new ArrayList<>();
        for (String nameStr : finalNameList) {
            NamePublished namePublished = new NamePublished();
            namePublished.setNid(nid)
                    .setName(nameStr)
                    .setStyle(style)
                    .setCategory(category)
                    .setArea(area)
                    .setEmail(email)
                    .setIsAdopted(false)
                    .setScore(0)
                    .setNameId(Tools.getNameId(nameStr));
            lists.add(namePublished);
            nid++;
        }
        watch.stop();
        log.info("创建该组名字共花费时间"+watch.getTotalTimeMillis()+"毫秒");
        return lists;
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
     * 批量更新所有采用状态
     * @param locals 本地生成姓名集合
     * @param cloudMap 云数据姓名map集合
     * @return List<NamePublished>
     */
    public List<NamePublished> batchUpdateAdoptedStatus(List<NamePublished> locals,Map<String,NamePublished> cloudMap){
        for (NamePublished local : locals){
            if (local.getIsAdopted() != cloudMap.get(local.getNameId()).getIsAdopted()){
                local.setIsAdopted(cloudMap.get(local.getNameId()).getIsAdopted());
            }
        }
        return locals;
    }

    /**
     * 以下标批量更新部分采用状态
     * @param locals 本地生成姓名集合
     * @param indexList 下标集合
     * @param cloudMap 云数据姓名map集合
     * @return List<NamePublished>
     */
    public List<NamePublished> batchUpdateElement(List<NamePublished> locals,List<Integer> indexList,Map<String,NamePublished> cloudMap){
        for (int index : indexList){
            String nameId = locals.get(index).getNameId();
            locals.get(index).setIsAdopted(cloudMap.get(nameId).getIsAdopted());
        }
        return locals;
    }

    /**
     * 生成最终的姓名集合
     * @param namePackageList 生成的姓名打包集合
     * @return List<?>
     */
    public List getFinalNameList(List<NamePublished> namePackageList){
        List<String> justNameList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        for (NamePublished namePackage : namePackageList) {
            justNameList.add(namePackage.getName());
        }
        MongoClient<NamePublished> client = new MongoClient<>(NamePublished.class);
        List<NamePublished> publishedList = client.queryTogether("name", justNameList);
        Map<String,NamePublished> cloudMap = new HashMap<>();
        for (NamePublished cloud : publishedList){
            cloudMap.put(cloud.getNameId(),cloud);
        }
        if (namePackageList.size() == publishedList.size()){
            return batchUpdateAdoptedStatus(namePackageList,cloudMap);
        }else {
            //此时namePackageList.size() > publishedList.size(),重复部分更新采用状态,非重复部分push至云端
            List<NamePublished> pushList = new ArrayList<>();
            for (int i=0;i<namePackageList.size();i++){
                if (cloudMap.get(namePackageList.get(i).getNameId()) == null){
                    pushList.add(namePackageList.get(i));
                }else {
                    indexList.add(i);
                }
            }
            client.saveBatch(pushList);
            return batchUpdateElement(namePackageList,indexList,cloudMap);
        }
    }

    /**
     * 添加采用名字
     * @param nameId 添加名唯一id
     */
    public void addAdoptedName(String nameId,String email){
        MongoClient<NameAdopted> client = new MongoClient<>(NameAdopted.class);
        NameAdopted one = (NameAdopted) client.queryOne("email", email);
        if (one == null){
            List<String> list = new ArrayList<String>(){{
                add(nameId);
            }};
            NameAdopted newOne = new NameAdopted().setEmail(email).setNameList(list);
            client.saveOne(newOne);
        }else {
            List<String> list = one.getNameList();
            if (list == null){
                list = new ArrayList<>();
            }
            list.add(nameId);
            client.updateOne("email",email,"nameList",list);
        }
    }

    public void markForName(){}

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
}
