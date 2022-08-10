package com.art.artcommon.aspect;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.custominterface.CachedTable;
import com.art.artcommon.utils.AopTargetUtils;
import com.art.artcommon.utils.DBUtils;
import com.art.artcommon.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class AllCachedAspect {

    @Autowired
    private DBUtils dbUtils;

    @Pointcut("@annotation(com.art.artcommon.custominterface.CachedTable)")
    public void cached(){}

    /**
     * 查询并缓存整个表数据
     * @param point 连接点
     * @throws Exception 异常
     */
    @Before("cached()")
    public void cachedAll(JoinPoint point) throws Exception {
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        CachedTable cached = AopTargetUtils.getTarget(target).getClass()
                .getDeclaredMethod(methodName, JSONObject.class)
                .getAnnotation(CachedTable.class);
        String prefix = cached.prefix();
        String key = cached.key();
        String[] args = cached.args();
        String tableName = cached.tableName();
        long timeout = cached.timeout();
        TimeUnit timeunit = cached.timeunit();
        String cache_key = prefix + key;
        if (!RedisUtil.hasKey(cache_key)){
            String sql = null;
            if (args.length==1 && args[0].equals("*")){
                sql = String.format("select * from %s",tableName);
            }else {
                StringBuilder s = new StringBuilder();
                for (int i=0;i<args.length;i++){
                    s.append(args[i]);
                    if (i != args.length-1){
                        s.append(",");
                    }
                }
                sql = String.format("select %s from %s",s,tableName);
            }
            String result = dbUtils.executeSql(sql);
            RedisUtil.set(cache_key,result,timeout,timeunit);
            log.info("缓存"+tableName+"表数据完成...");
        }
    }
}
