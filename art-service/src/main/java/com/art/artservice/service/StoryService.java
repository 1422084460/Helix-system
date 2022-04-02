package com.art.artservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.entity.FirstName;
import com.art.artcommon.entity.LastName;
import com.art.artcommon.entity.NamePackage;
import com.art.artcommon.mapper.FirstNameMapper;
import com.art.artcommon.mapper.LastNameMapper;
import com.art.artcommon.mongo.NameAdopted;
import com.art.artcommon.utils.MongoUtils;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private static final String FULL_CLASS_NAME = "com.art.artcommon.mongo.NameAdopted";

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
}
