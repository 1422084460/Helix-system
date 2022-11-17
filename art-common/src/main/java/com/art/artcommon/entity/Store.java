package com.art.artcommon.entity;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;

import java.util.HashMap;

/**
 * description
 * 全局缓存类
 * @author lou
 * @create 2022/11/16
 */
public class Store extends JSONObject {

    /**
     * 构造方法
     */
    private Store(){}

    /**
     * 静态内部类
     */
    private static class createStore{
        private static final Store STORE = new Store();
    }

    /**
     * 获取 Store
     */
    public static Store Instance(){
        return createStore.STORE;
    }

    /**
     * 初始化 Store
     */
    public static void init(){
        createStore.STORE.put(R.RENDER_LOCK,null);
    }

    /**
     * 是否包含某个 key
     * @param key 键
     * @return boolean
     */
    public boolean hasKey(Object key) {
        return super.containsKey(key);
    }

    /**
     * 生成 map 值
     * @param key 键
     * @param value 值
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> MapValue(String key, Object value){
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 生成 JSONObject 值
     * @param key 键
     * @param value 值
     * @return JSONObject
     */
    public JSONObject JSONObjectValue(String key, Object value){
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object;
    }

    /**
     * 存入数据
     * @param key 键
     * @param value 值
     * @return Object
     */
    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    /**
     * 存入并发数据
     * @param KEY 外键
     * @param key 内键
     * @param value 值
     */
    public void safePut(String KEY, String key, Object value){
        JSONObject object = JSONObjectValue(key, value);
        put(KEY, object);
    }

    /**
     * 获取并发数据
     * @param KEY 外键
     * @param key 内键
     * @return Object
     */
    public Object safeGet(String KEY, String key){
        JSONObject object = getJSONObject(KEY);
        return object.get(key);
    }

    /**
     * 移除内部数据
     * @param KEY 外键
     * @param key 内键
     */
    public void subRemove(String KEY, String key){
        getJSONObject(KEY).remove(key);
    }
}
