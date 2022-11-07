package com.art.artcommon.utils;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * description
 * redis工具类
 * @author lou
 * @create 2022/11/2
 */
public class RedisUtil {

    /**
     * redisTemplate模板
     */
    private static StringRedisTemplate redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);

    /**
     * 默认过期时间
     */
    private final static long NOT_EXPIRE = 60 * 60 * 48;

    /**
     * 新增普通key-value键值对
     * @param key 键
     * @param data 值
     */
    public static void set(String key, String data){
        redisTemplate.opsForValue().set(key, data);
    }

    /**
     * 新增普通key-value键值对，并设置过期时间
     * @param key 键
     * @param data 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public static void set(String key, String data, long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key, data,NOT_EXPIRE,TimeUnit.SECONDS);
        if (timeout > 0){
            setExpire(key, timeout, unit);
        }
    }

    /**
     * 通过key获取普通key-value键值对的值
     * @param key 键
     * @return String
     */
    public static String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 新增hash类型
     * @param key1 外键
     * @param key2 内键
     * @param data 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public static void setHash(String key1, String key2, String data, long timeout, TimeUnit unit){
        redisTemplate.opsForHash().put(key1,key2,data);
        if (timeout > 0){
            setExpire(key1, timeout, unit);
        }
    }

    /**
     * 获取hash值
     * @param key1 外键
     * @param key2 内键
     * @return String
     */
    public static String getHash(String key1, String key2){
        return (String) redisTemplate.opsForHash().get(key1,key2);
    }

    /**
     * 获取批量hash值
     * @param key 外键
     * @param list 内键集合
     * @return List
     */
    public static List getMultiHash(String key, Collection list){
        return redisTemplate.opsForHash().multiGet(key,list);
    }

    /**
     * 新增ZSet类型
     * @param key 键
     * @param data 值
     * @param score 分数
     */
    public static void setZSet(String key, String data, double score){
        redisTemplate.opsForZSet().add(key, data, score);
    }

    /**
     * 获取ZSet分数
     * @param key 键
     * @param target 目标值
     * @return Double
     */
    public static Double getZSetScore(String key, String target){
        return redisTemplate.opsForZSet().score(key, target);
    }

    /**
     * 获取范围内ZSet数量（包含边界值）
     * @param key 键
     * @param min 范围最小值
     * @param max 范围最大值
     * @return Long
     */
    public static Long getZSetCount(String key, double min, double max){
        return redisTemplate.opsForZSet().count(key,min,max);
    }

    /**
     * 获取指定下标范围内ZSet的值集合
     * @param key 键
     * @param start 下标开始
     * @param end 下标结束（-1表示到最后）
     * @return Set<String>
     */
    public static Set<String> getZSetValues(String key, long start, long end, boolean asc){
        if (asc){
            return redisTemplate.opsForZSet().range(key, start, end);
        }else {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        }
    }

    /**
     * 获取指定分数范围内ZSet的值集合
     * @param key 键
     * @param min 范围最小值
     * @param max 范围最大值
     * @return Set<String>
     */
    public static Set<String> getZSetValuesByScore(String key, double min, double max){
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 删除ZSet的key中的部分值
     * @param key 键
     * @param values 值
     */
    public static void removeZSetKey(String key, Object... values){
        redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 为普通键值对设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public static void setExpire(String key, long timeout, TimeUnit unit){
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取某个键的剩余过期时间
     * @param key 键
     * @return Long
     */
    public static Long getExpire(String key){
        return redisTemplate.getExpire(key);
    }

    /**
     * 删除普通键
     * @param key 键
     */
    public static void deleteKey(String key){
        redisTemplate.delete(key);
    }

    /**
     * 删除hash键
     * @param key1 外键
     * @param key2 内键
     */
    public static void delHashKey(String key1, String key2){
        redisTemplate.opsForHash().delete(key1,key2);
    }

    /**
     * 判断是否存在某个普通键
     * @param key 键
     * @return Boolean
     */
    public static Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断是否存在某个hash键
     * @param key 键
     * @param hashKey hash外键
     * @return Boolean
     */
    public static Boolean hasHashKey(String key, String hashKey){
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 刷新过期时间
     * @param key 键
     */
    public static void reFresh(String key){
        if (hasKey(key)){
            if (key.toUpperCase().startsWith("USER")){
                set(key,get(key),10,TimeUnit.MINUTES);
            }//else另作判断
        }
    }

    /**
     * 刷新过期时间
     * @param key 外键
     * @param key2 内键
     */
    public static void reFresh(String key, String key2){
        if (hasKey(key)){
            if (hasHashKey(key, key2)) {
                if (key.toUpperCase().startsWith("USER")) {
                    setHash(key,key2, getHash(key,key2), 10, TimeUnit.MINUTES);
                }//else另作判断
            }
        }
    }

    /**
     * 自增操作
     * @param key 键
     * @param ifGetFirst 是否先取值再自增
     * @return Long
     */
    public static Long inc(String key, boolean ifGetFirst){
        RedisAtomicLong atomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        if (ifGetFirst){
            return atomicLong.getAndIncrement();
        }else {
            return atomicLong.incrementAndGet();
        }
    }

    /**
     * 用管道操作批量新增键值对
     * @param setCmd 普通键值对集合
     * @param hashCmd hash键值对集合
     */
    public static void pipLine(Map<String,String> setCmd, Map<String,Map<String,String>> hashCmd, Map<String,Map<String,Double>> zSetCmd){
        if (!hasKey("isPipAlready") || "false".equals(get("isInitAlready"))){
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                connection.openPipeline();
                connection.set("isInitAlready".getBytes(),"true".getBytes());
                if (setCmd != null){
                    setCmd.forEach((key,value)->{
                        connection.set(key.getBytes(),value.getBytes());
                    });
                }
                if (hashCmd != null){
                    hashCmd.forEach((key,map)->{
                        map.forEach((f,v)->{
                            connection.hashCommands().hSet(key.getBytes(),f.getBytes(),v.getBytes());
                        });
                    });
                }
                if (zSetCmd != null){
                    zSetCmd.forEach((key,map)->{
                        map.forEach((v,s)->{
                            connection.zSetCommands().zAdd(key.getBytes(),s,v.getBytes());
                        });
                    });
                }
                connection.close();
                return null;
            },redisTemplate.getStringSerializer());
        }
    }
}
