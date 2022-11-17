package com.art.artcommon.aspect;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.annotations.Cached;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.AopTargetUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * description
 * 数据缓存切面类
 * @author lou
 * @create 2022/7/29
 */
@Aspect
@Component
public class CustomCachedAspect {

    @Pointcut("@annotation(com.art.artcommon.annotations.Cached)")
    public void cached(){}

    /**
     * 自定义缓存数据
     * @param point 连接点
     * @throws Exception 异常
     */
    @Before("cached()")
    public void customCached(JoinPoint point) throws Exception {
        System.out.println("开始Before");
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Cached cached = AopTargetUtils.getTarget(target).getClass()
                .getDeclaredMethod(methodName, JSONObject.class)
                .getAnnotation(Cached.class);
        String prefix = cached.prefix();
        String key = cached.key();
        long timeout = cached.timeout();
        TimeUnit timeunit = cached.timeunit();
        String cache_key = prefix + key;
        JSONObject object = new JSONObject();
        object.put("cache_key",cache_key);
        object.put("timeout",timeout);
        object.put("timeunit",timeunit);
        String name = Thread.currentThread().getName();//name换成email
        Store.Instance().safePut(name, "cache_args", object);
        System.out.println("aop线程:"+name);
        System.out.println("结束Before");
    }

    /**
     * 查询并缓存整个表数据
     * @param point 连接点
     * @throws Exception 异常
     */
    @After("cached()")
    public void cachedAll(JoinPoint point) throws Exception {}
}
