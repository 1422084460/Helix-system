package com.art.artcommon.entity;

import com.art.artcommon.constant.R;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * description
 * 全局缓存类
 * @author lou
 * @create 2022/11/9
 */
@Slf4j
public class Store extends HashMap<String,HashMap<String,Object>> {

    /**
     * 构造方法
     */
    private Store(){}

    /**
     * 静态内部类
     */
    private static class createStore{
        private static final Store store = new Store();
    }

    /**
     * 获取 Store
     */
    public static Store getInstance(){
        return createStore.store;
    }

    /**
     * 初始化 Store
     */
    public static void init(){
        createStore.store.put(R.RENDER_LOCK,null);
    }

    public HashMap<String, Object> MainDataPut(String k,Object v){
        HashMap<String, Object> map = new HashMap<>();
        map.put(k,v);
        return map;
    }
}
