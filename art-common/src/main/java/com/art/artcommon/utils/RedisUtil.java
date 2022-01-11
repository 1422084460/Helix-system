package com.art.artcommon.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisUtil {

    private static StringRedisTemplate redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);

    private final static long NOT_EXPIRE = 60 * 60 * 48;

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
            setExpire(key2, timeout, unit);
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

    public static Long inc(String key,boolean ifGetFirst){
        RedisAtomicLong atomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        if (ifGetFirst){
            return atomicLong.getAndIncrement();
        }else {
            return atomicLong.incrementAndGet();
        }
    }
}
