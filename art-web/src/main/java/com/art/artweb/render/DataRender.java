package com.art.artweb.render;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IPManager;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.DBUtils;
import com.art.artcommon.utils.MongoClient;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcreator.mongo.IllegalWords;
import com.art.artcreator.novel.Novel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${dataRender.IllegalWords}")
    private boolean IF_NEED_UPDATE_ILLEGAL_WORDS;

    private static final String RANK_KEY_1 = "Novel_Score_Rank";
    private static final String RANK_KEY_2 = "Novel_Popularity_Rank";
    private static final String RANK_KEY_3 = "Novel_Counts_Rank";

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
                log.info("importBlackList ok");
            } catch (Exception e) {
                log.error("importBlackList ===>>> 发生错误，请查看后台任务！");
                task.add("importBlackList");
            }
            return "ok";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            try {
                importIllegalWords();
                log.info("importIllegalWords ok");
            } catch (Exception e) {
                log.error("importIllegalWords ===>>> 发生错误，请查看后台任务！");
                task.add("importIllegalWords");
            }
            return "ok";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(()->{
            try {
                importNovelRank();
                log.info("importNovelRank ok");
            } catch (Exception e) {
                log.error("importNovelRank ===>>> 发生错误，请查看后台任务！");
                task.add("importNovelRank");
            }
            return "ok";
        });
        CompletableFuture.allOf(future,future2,future3).whenComplete((v,e)->{
            if (e!=null){
                e.printStackTrace();
            }
            //此处解锁项目访问权限
            Store.Instance().remove(R.RENDER_LOCK);
            log.info("unlock... and import: success!");
        });
    }

    /**
     * 导入黑名单数据
     */
    private void importBlackList(){
        String sql = "select * from Admin_IPM where blacklist is true";
        String result = dbUtils.execute(sql);
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
        RedisUtil.pipLine(null,hashCmd,null);
    }

    /**
     * 导入非法字符数据
     */
    private void importIllegalWords(){
        if (IF_NEED_UPDATE_ILLEGAL_WORDS){
            MongoClient<IllegalWords> client = new MongoClient<>(IllegalWords.class);
            List<IllegalWords> list = client.queryAll();
            List<String> collect = list.stream().map(IllegalWords::getWord).collect(Collectors.toList());
            RedisUtil.deleteKey("Illegal_word_list");
            RedisUtil.set("Illegal_word_list",JSON.toJSONString(collect));
        }
    }

    /**
     * 导入小说排行榜
     */
    public void importNovelRank(){
        cachedRank("novel_score",RANK_KEY_1);
        cachedRank("novel_popularity",RANK_KEY_2);
        cachedRank("paras_count",RANK_KEY_3);
    }

    private void cachedRank(String column,String key){
        RedisUtil.deleteKey(key);
        String sql = String.format("select * from Story_NovelChapterList order by %s limit 20",column);
        String result = dbUtils.execute(sql);
        List<Novel> list = JSON.parseObject(result, new TypeReference<List<Novel>>(){});
        Map<String,Double> innerMap = new HashMap<>();
        Map<String,Map<String,Double>> zSetCmd = new HashMap<>();
        for (Novel n : list){
            if (column.equals("novel_score")){
                innerMap.put(JSON.toJSONString(n),Double.parseDouble(n.getNovel_score()));
            }else if (column.equals("novel_popularity")){
                innerMap.put(JSON.toJSONString(n),Double.parseDouble(n.getNovel_popularity()));
            }else {
                innerMap.put(JSON.toJSONString(n),Double.parseDouble(""+n.getParas_count()));
            }
        }
        zSetCmd.put(key,innerMap);
        RedisUtil.pipLine(null,null,zSetCmd);
    }

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
