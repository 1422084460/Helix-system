package com.art.artweb.render;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.entity.IPManager;
import com.art.artcommon.utils.DBUtils;
import com.art.artcommon.utils.MongoClient;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcreator.mongo.IllegalWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * description
 * 项目启动时初始化redis数据
 * @author lou
 * @create 2022/5/10
 */
@Slf4j
@Component
public class DataRender {

    @Autowired
    private DBUtils dbUtils;

    /**
     * 待执行的任务链表
     */
    private volatile LinkedBlockingQueue<String> task = new LinkedBlockingQueue<>();

    /**
     * 主方法
     */
    public void start(){
        render();
    }

    /**
     * 需要缓存的所有数据，主渲染类
     */
    private void render(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            try {
                importBlackList();
            } catch (Exception e) {
                log.error("importBlackList ===>>> 发生错误，请查看后台任务！");
                task.add("importBlackList");
            }
            return "ok";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            try {
                importIllegalWords();
            } catch (Exception e) {
                log.error("doSomething ===>>> 发生错误，请查看后台任务！");
                task.add("importIllegalWords");
            }
            return "ok";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(()->{
            try {
                doSomething();
            } catch (Exception e) {
                log.error("doSomething ===>>> 发生错误，请查看后台任务！");
                task.add("doSomething");
            }
            return "ok";
        });
        CompletableFuture.allOf(future,future2,future3).whenComplete((v,e)->{
            if (e!=null){
                e.printStackTrace();
            }
            log.info("success");
        });
    }

    /**
     * 导入黑名单数据
     */
    private void importBlackList(){
        String sql = "select * from Admin_IPM where blacklist is true";
        String result = dbUtils.executeSql(sql);
        List<IPManager> list = JSON.parseObject(result, new TypeReference<List<IPManager>>(){});
        Map<String, Map<String,String>> hashCmd = new HashMap<>();
        Map<String,String> map = new HashMap<>();
        for (IPManager i : list){
            if (i.isBlacklist()){
                map.put(i.getIp(),"true");
            }
        }
        hashCmd.put("blacklist",map);
        RedisUtil.deleteKey("blacklist");
        RedisUtil.pipLine(null,hashCmd);
    }

    /**
     * 导入非法字符数据
     */
    private void importIllegalWords(){
        MongoClient<IllegalWords> client = new MongoClient<>(IllegalWords.class);
        List<IllegalWords> list = client.queryAll();
        List<String> collect = list.stream().map(IllegalWords::getWord).collect(Collectors.toList());
        RedisUtil.deleteKey("Illegal_word_list");
        RedisUtil.set("Illegal_word_list",JSON.toJSONString(collect));
    }

    public void doSomething(){}

    /**
     * 获取当前剩余任务
     * @return JSONObject
     */
    public JSONObject getTask(){
        if (task.size()==0){
            return null;
        }else {
            JSONObject res = new JSONObject();
            List<String> list = new ArrayList<>();
            if (task.contains("importBlackList")) {
                list.add("importBlackList");
            }
            if (task.contains("importIllegalWords")) {
                list.add("importIllegalWords");
            }
            if (task.contains("doSomething")) {
                list.add("doSomething");
            }
            res.put("taskList",list);
            res.put("taskSize",list.size());
            return res;
        }
    }

    /**
     * 执行剩余任务
     * @return boolean
     */
    public boolean executorTask(){
        while (true){
            String element = task.poll();
            if (element == null){
                break;
            }else {
                switch (element){
                    case "importBlackList":
                        importBlackList();
                        break;
                    case "importIllegalWords":
                        importIllegalWords();
                        break;
                    case "doSomething":
                        break;
                }
            }
        }
        return true;
    }
}
