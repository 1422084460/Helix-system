package com.art.artcommon.utils;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.entity.Store;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * description
 * 异步缓存数据工具方法，配合Cached注解使用
 * @author lou
 * @create 2022/8/2
 */
@Component
public class AsyncDataUtils {

    @Async("cachePool")
    public void dataCached(JSONObject data){
        //Object args = Store.getInstance().get(Thread.currentThread().getName()).get("cache_args");
        //System.out.println("args.toString() = " + args.toString());
        System.out.println("--------------------");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(data.toJSONString());
        System.out.println(Thread.currentThread().getName());
        System.out.println("--------------------");
    }
}
