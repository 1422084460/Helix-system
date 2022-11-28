package com.art.artcreator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.CustomException;
import com.art.artcreator.entity.FirstName;
import com.art.artcreator.entity.LastName;
import com.art.artcreator.mapper.ChapterMapper;
import com.art.artcreator.mapper.FirstNameMapper;
import com.art.artcreator.mapper.LastNameMapper;
import com.art.artcreator.mapper.NovelChapterListMapper;
import com.art.artcommon.utils.MongoClient;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artcreator.mongo.NamePublished;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * description
 * 姓名服务类
 * @author lou
 * @create 2022/9/9
 */
@Service
@Slf4j
public class StoryNameService {

    @Autowired
    private FirstNameMapper first;
    @Autowired
    private LastNameMapper last;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private NovelChapterListMapper novelChapterListMapper;

    /**
     * 创建名字总方法，默认生成50个
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
        int default_firstNameNum = 50;
        int default_lastNameNum = 50*2;
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
        //筛选出的集合数小于50的情况
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
            //筛选出的集合数大于等于50的情况
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
     * @param nameId 名字唯一id
     */
    public void addAdoptedName(String nameId,String email){
        MongoClient<NamePublished> client = new MongoClient<>(NamePublished.class);
        Map<String,Object> filter = new HashMap<>();
        filter.put("email",email);
        filter.put("nameId",nameId);
        client.update(filter,"isAdopted",true);
    }

    /**
     * 对名字进行评分
     * @param nameId 名字唯一id
     * @param email 邮箱
     */
    public void markForName(String nameId,String email,int score){
        Map<String,Object> filter = new HashMap<>();
        filter.put("email",email);
        filter.put("nameId",nameId);
        MongoClient<NamePublished> client = new MongoClient<>(NamePublished.class);
        client.update(filter,"score",score);
    }

    /**
     * 计算平均评分
     * @param nameId 名字唯一id
     * @return int
     */
    private int computeTotalScore(String nameId){
        MongoClient<NamePublished> client = new MongoClient<>(NamePublished.class);
        List<NamePublished> list = client.queryByFilter("nameId", nameId, "", true);
        int count = 0;
        float sum = 0f;
        for (NamePublished n : list){
            int score = n.getScore();
            if (score != 0){
                count++;
                sum+=score;
            }
        }
        return Math.round(sum/count);
    }

    /**
     * 展示名字详细信息
     * @param nameId 名字唯一id
     * @return JSONObject
     */
    public JSONObject showNameDetails(String nameId) {
        //此处异步执行多任务以提高响应速度
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(()->{
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            });
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "str";
            });
            CompletableFuture<JSONObject> future = future1.thenCombine(future2,(a,b)->{
                stopWatch.stop();
                log.info("名字详情渲染耗时："+stopWatch.getTotalTimeSeconds());
                System.out.println(a);
                System.out.println(b);
                JSONObject o = new JSONObject();
                o.put("1",a);
                o.put("2",b);
                return o;
            });
            return future.get();
        }catch (Exception e){
            throw new CustomException("因数据问题，名字详情渲染失败！");
        }
    }
}
