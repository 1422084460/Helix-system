package com.art.artcommon.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.entity.IPManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * description
 * 项目启动时初始化redis数据
 * @author lou
 * @create 2022/5/10
 */
@Slf4j
public class InitDataUtils {

    @Autowired
    private static DBUtils dbUtils;

    /**
     * 主方法
     */
    public static void importDataToRedis(){
        render();
    }

    /**
     * 需要缓存的所有数据，主渲染类
     */
    private static void render(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            try {
                importBlackList();
            } catch (Exception e) {
                log.error("importBlackList ===>>> 发生错误，请重试！");
            }
            return "ok";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                return "import 2";
            }
            return "ok";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(6);
                int i = 1/0;
            } catch (InterruptedException e) {
                return "import 3";
            }
            return "ok";
        });
        CompletableFuture.allOf(future,future2,future3).whenComplete((v,e)->{});
    }

    private static void importBlackList(){
        String sql = "select * from Admin_IPM where blacklist is true";
        String result = dbUtils.executeSql(sql);
        IPManager ipManager = JSON.parseObject(result, new TypeReference<IPManager>() {});
        RedisUtil.setHash("blacklist");
    }
}
