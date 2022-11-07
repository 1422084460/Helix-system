package com.art.artcommon.entity;

import com.art.artcommon.constant.R;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class Store extends HashMap<String,HashMap<String,Object>> {

    private Store(){}

    private static class makeStore{
        private static final Store store = new Store();
    }

    /**
     * 不需要单例，但需要考虑保存时的线程安全，即多个用户可能会用同一个key
     * 所以key可以放用户唯一标识即用户id，value放HashMap
     * 多线程并发问题，就是在同一段时间内，多个相同的http请求被创建，一个http请求从开始到结束就是一个线程的生命周期
     */
    public static Store getInstance(){
        return makeStore.store;
    }

    public static Store init(){
        makeStore.store.put(R.RENDER_LOCK,null);
        return makeStore.store;
    }

    public HashMap<String, Object> MainDataPut(String k,Object v){
        HashMap<String, Object> map = new HashMap<>();
        map.put(k,v);
        return map;
    }
}
