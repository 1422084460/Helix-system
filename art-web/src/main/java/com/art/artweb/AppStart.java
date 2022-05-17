package com.art.artweb;

import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.Error;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.*;
import com.art.artadmin.handler.DirectHandler;
import com.art.artadmin.handler.Handler;
import com.art.artweb.async.AsyncTaskMain;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AppStart implements ApplicationListener<ApplicationStartedEvent> {

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initStore();
        initRedis();
        //importData();
        //doSomethings();//异步调用redis缓存
    }

    //@TaskExe(name = "syncMsg",order = 1)
    private void doSomething() throws Exception {
        //一部分注解为切面逻辑处理，一部分注解为通过注解变量值定义来进行对应逻辑处理
        Handler handler = SpringContextHolder.getBean("fanoutHandler");
        //Error error = AopTargetUtils.getTarget(handler).getClass().getAnnotation(Error.class);
        //System.out.println(error.name());
        System.out.println("isAopProxy:"+AopUtils.isAopProxy(handler));
        System.out.println("isCglibProxy:"+AopUtils.isCglibProxy(handler));
        System.out.println("isJdkDynamicProxy:"+AopUtils.isJdkDynamicProxy(handler));
        handler.handler("","");
//        System.out.println(AopTargetUtils.getTarget(handler).getClass()
//                .getDeclaredMethod("handler", String.class, String.class).getAnnotation(Error.class).name());
//        System.out.println(handler.getClass()
//                .getDeclaredMethod("handler", String.class, String.class).getAnnotation(Error.class).name());
    }

    @Order(2)
    private void initRedis() {
        if (!RedisUtil.hasKey("isInitAlready")){
            Map<String,String> map = new HashMap<>();
            String email = "getEmail";
            map.put("test",email);
            RedisUtil.pipLine(map,null);
        }
        if (!RedisUtil.hasKey("user_log_queue_sync_finished")){
            RedisUtil.set("user_log_queue_sync_finished","false");
        }
        log.info("redis data initializes already...");
    }

    /**
     * 初始化全局变量 Store
     */
    @Order(1)
    private void initStore(){
        //Store.getInstance();
        log.info("store initializes already...");
    }

    private void importData(){
        InitDataUtils.importDataToRedis();
        log.info("import data to redis already...");
    }

    @Autowired
    private AsyncTaskMain task;
    private void doSomethings(){
        //task.ddd();
        System.out.println("start+++++++++++++++++");
        Tools.getAnnotationField();
    }
}
