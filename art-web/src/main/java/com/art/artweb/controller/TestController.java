package com.art.artweb.controller;

//import com.art.artcommon.mapper.WatcherMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.entity.User;
import com.art.artadmin.mapper.UserMapper;
import com.art.artcommon.custominterface.CachedTable;
import com.art.artcommon.custominterface.Error;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.DBUtils;
import com.art.artcommon.utils.JWTUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @RequestMapping("/getToken")
    public void getTokenTest(@RequestBody String data){
        System.out.println(data);
        Map map = JSON.parseObject(data);
        String token = JWTUtils.getToken(map);
        System.out.println(token);
    }

    @RequestMapping("/token")
    public String verityTokenTest(){
        System.out.println("测试进来了");
        //DecodedJWT verify = JWTUtils.verify("");
        //System.out.println(verify.getClaim("email").asString());
        //System.out.println(Store.getInstance().get(Thread.currentThread().getName()).get("token验证"));
        IResult result = (IResult) Store.getInstance().get(Thread.currentThread().getName()).get("token验证");
        String code = result.getCode();
        try {
            TimeUnit.SECONDS.sleep(3);
            System.out.println("结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("0000".equals(code)){
            System.out.println("业务正常进行>>>...");
            Store.getInstance().remove(Thread.currentThread().getName());
        }
        return result.getMsg();
    }
//
//    @RequestMapping("/thread")
//    public void threadTest(){
//        System.out.println(Thread.currentThread().getName());
//        try {
//            TimeUnit.SECONDS.sleep(10);
//            System.out.println("结束");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

//    @Resource
//    private WatcherMapper watcherMapper;
//
//    @RequestMapping("/goToVideo")
//    public String goToVideo(@RequestBody String data){
//        System.out.println(data);
//        Map map = JSON.parseObject(data);
//        QueryWrapper wrapper = new QueryWrapper();
//        QueryWrapper wrapper2 = new QueryWrapper();
//        wrapper.eq("isWatcher","1");
//        wrapper2.eq("isWatcher","0");
//        Watcher is_watcher = watcherMapper.selectOne(wrapper);
//        Watcher isNot_watcher = watcherMapper.selectOne(wrapper2);
//        if (map.get("email").equals(isNot_watcher.getEmail()) && map.get("password").equals(isNot_watcher.getPassword())){
//            return "Main";
//        }else if (map.get("email").equals(is_watcher.getEmail()) && map.get("password").equals(is_watcher.getPassword())){
//            return "Video";
//        }else {
//            return "error";
//        }
//    }

    @Autowired
    private DBUtils utils;

    @RequestMapping("/testSql")
    @CachedTable(key = "test",tableName = "CacheTables")
    public IResult testSql(@RequestBody JSONObject data){
        log.info("开始测试");
//        try {
//            TimeUnit.SECONDS.sleep(60);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        log.info("结束测试");
        return IResult.success();
    }

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @RequestMapping("/testDruid")
    public void testDruid() throws SQLException {
        System.out.println(dataSource);
        System.out.println(dataSource.getConnection());
        System.out.println(dataSource);
    }

    @RequestMapping("/testAnno")
    @Error
    public IResult testAnno(){
        log.info(Thread.currentThread().getName());
        return IResult.success();
    }

    private volatile Semaphore semaphore = new Semaphore(3);

    @RequestMapping("/testThread")
    public IResult testThread(){
        CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
            while (true){
                try {
                    if (semaphore.tryAcquire()) {
                        log.info("go in...");
                        TimeUnit.SECONDS.sleep(6);
                        semaphore.release();
                        break;
                    }else {
                        log.info("wait...");
                        TimeUnit.SECONDS.sleep(3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.info("go out...");
        });
        return IResult.success();
    }
}
