package com.art.artcommon.utils;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisUtil {

    private static StringRedisTemplate redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);

    private final static long NOT_EXPIRE = 60 * 60 * 48;

    public static void set(String key,String data){
        redisTemplate.opsForValue().set(key, data);
    }

    public static void set(String key,String data,long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key, data,NOT_EXPIRE,TimeUnit.SECONDS);
        if (timeout > 0){
            setExpire(key, timeout, unit);
        }
    }

    public static String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public static void setHash(String key1,String key2,String data,long timeout, TimeUnit unit){
        redisTemplate.opsForHash().put(key1,key2,data);
        if (timeout > 0){
            setExpire(key1, timeout, unit);
        }
    }

    public static String getHash(String key1,String key2){
        return (String) redisTemplate.opsForHash().get(key1,key2);
    }

    public static List getMultiHash(String key, Collection list){
        return redisTemplate.opsForHash().multiGet(key,list);
    }

    public static void setZSet(String key,String data,double score){
        redisTemplate.opsForZSet().add(key, data, score);
    }

    public static void setExpire(String key, long timeout, TimeUnit unit){
        redisTemplate.expire(key, timeout, unit);
    }

    public static Long getExpire(String key){
        return redisTemplate.getExpire(key);
    }

    public static void deleteKey(String key){
        redisTemplate.delete(key);
    }

    public static void delHashKey(String key1,String key2){
        redisTemplate.opsForHash().delete(key1,key2);
    }

    public static Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public static Boolean hasHashKey(String key,String hashKey){
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public static void reFresh(String key){
        if (hasKey(key)){
            if (key.toUpperCase().startsWith("USER")){
                set(key,get(key),10,TimeUnit.MINUTES);
            }//else另作判断
        }
    }

    public static void reFresh(String key,String key2){
        if (hasKey(key)){
            if (hasHashKey(key, key2)) {
                if (key.toUpperCase().startsWith("USER")) {
                    setHash(key,key2, getHash(key,key2), 10, TimeUnit.MINUTES);
                }//else另作判断
            }
        }
    }

    public static Long inc(String key,boolean ifGetFirst){
        RedisAtomicLong atomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        if (ifGetFirst){
            return atomicLong.getAndIncrement();
        }else {
            return atomicLong.incrementAndGet();
        }
    }

    public static void pipLine(Map<String,String> setCmd,Map<String,Map<String,String>> hashCmd){
        if (!hasKey("isPipAlready") || "false".equals(get("isInitAlready"))){
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                connection.openPipeline();
                connection.set("isInitAlready".getBytes(),"true".getBytes());
                if (setCmd!=null){
                    setCmd.forEach((key,value)->{
                        connection.set(key.getBytes(),value.getBytes());
                    });
                }
                if (hashCmd!=null){
                    hashCmd.forEach((key,map)->{
                        map.forEach((f,v)->{
                            connection.hashCommands().hSet(key.getBytes(),f.getBytes(),v.getBytes());
                        });
                    });
                }
                return null;
            },redisTemplate.getStringSerializer());
        }
    }
}
