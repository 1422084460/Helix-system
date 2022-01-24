package com.art.artcommon.entity;

import java.util.HashMap;

public class Store extends HashMap<String,HashMap<String,Object>> {

    private volatile static Store store = null;

    private Store(){}

    /**
     * 不需要单例，但需要考虑保存时的线程安全，即多个用户可能会用同一个key
     * 所以key可以放用户唯一标识即用户id，value放HashMap
     * 多线程并发问题，就是在同一段时间内，多个相同的http请求被创建，一个http请求从开始到结束就是一个线程的生命周期
     * @return
     */
    public static Store getInstance(){
        if (store==null){
            store = new Store();
        }
        return store;
    }

    public HashMap<String, Object> MainDataPut(String k,Object v){
        HashMap<String, Object> map = new HashMap<>();
        map.put(k,v);
        return map;
    }

    public boolean hasKey(String key){
        return store.get(key) != null;
    }

    public boolean hasInnerKey(String key,String innerKey){
        return store.get(key).get(innerKey) != null;
    }
}
