package com.art.artweb.controller;

//import com.art.artcommon.mapper.WatcherMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.mapper.UserMapper;
import com.art.artcommon.annotations.CachedTable;
import com.art.artcommon.annotations.Error;
import com.art.artcommon.entity.IPManager;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.*;
//import com.art.artcommon.utils.MongoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @RequestMapping("/justTest")
    public IResult justTest(@RequestBody @Valid IPManager data, HttpServletRequest request){
        //new DataRender().start();
        //String token = request.getHeader("token");
        System.out.println(data.getIp());
        return IResult.success();
    }

    @RequestMapping("/getToken")
    public void getTokenTest(@RequestBody String data){
        System.out.println(data);
        Map map = JSON.parseObject(data);
        String token = JWTUtils.getToken(map);
        System.out.println(token);
    }

    @RequestMapping("/token")
    public String verityTokenTest(){
        System.out.println("测试进来了...");
        //DecodedJWT verify = JWTUtils.verify("");
        //System.out.println(verify.getClaim("email").asString());
        //System.out.println(Store.getInstance().get(Thread.currentThread().getName()).get("token验证"));
        IResult result = (IResult) Store.Instance().safeGet(Thread.currentThread().getName(), "token验证");
        String code = result.getCode();
        try {
            TimeUnit.SECONDS.sleep(3);
            System.out.println("结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("0000".equals(code)){
            System.out.println("业务正常进行>>>...");
            Store.Instance().remove(Thread.currentThread().getName());
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

//    @RequestMapping("/testPage")
//    public IResult testPage(){
//        JSONObject o1 = new JSONObject();
//        JSONObject o2 = new JSONObject();
//        JSONObject o3 = new JSONObject();
//        JSONObject o4 = new JSONObject();
//        JSONObject o5 = new JSONObject();
//        o1.put("name","1");
//        o1.put("pwd","1");
//        o2.put("name","2");
//        o2.put("pwd","2");
//        o3.put("name","3");
//        o3.put("pwd","3");
//        o4.put("name","4");
//        o4.put("pwd","4");
//        o5.put("name","5");
//        o5.put("pwd","5");
//        List<JSONObject> list = new ArrayList<JSONObject>(){{
//           add(o1);
//           add(o2);
//           add(o3);
//           add(o4);
//           add(o5);
//        }};
//        PageMaster res = PageMaster.create(list,5);
//        return IResult.success(res);
//    }

    @RequestMapping("/testAsync")
    public IResult testAsync(@RequestBody JSONObject data){
        String msg = data.getString("msg");
        CompletableFuture.supplyAsync(()->{
            try {
                log.info("go in CompletableFuture");
                TimeUnit.SECONDS.sleep(6);
                log.info("finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }).thenAccept(a->{
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("=="+a);
        });
        return IResult.success("ok!!!",null);
    }

    public List ddd(List list){
        list.remove(0);
        return list;
    }

    @RequestMapping("/testMongo")
    public IResult testMongo(@RequestBody JSONObject data){
//        MongoClient<NamePublished> client = new MongoClient<>(NamePublished.class);
//        List<NamePublished> onlinePublishedList = client.queryByFilter("email", "123@4.com", "", true);
//        System.out.println(onlinePublishedList.size()==0);
        Map map = new HashMap();
        map.put("1",1);
        map.put("2",2);
        map.put("3",3);
        List list = new ArrayList();
        List index = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        index.add(0);
        System.out.println(list.toString());
        List ddd = ddd(list);
        System.out.println(ddd.toString());
        System.out.println(list.toString());
        return IResult.success("ok",null);
    }

    @RequestMapping("/testRandom")
    public IResult testRandom(@RequestBody JSONObject data){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Integer> list = Tools.getRandom(data.getIntValue("a"));
        stopWatch.stop();
        String str = list.toString();
        JSONObject object = new JSONObject();
        object.put("list",str);
        object.put("cost",""+stopWatch.getTotalTimeMillis());
        return IResult.success(object);
    }

    //@Autowired
    //private TestService service;

    @RequestMapping("/testScheduled")
    public IResult testScheduled(@RequestBody JSONObject data){
        return IResult.success();
    }

    LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

    public String addQ(String msg){
        queue.add(1);
        queue.add(2);
        queue.add(3);
        submit();
        return "123";
    }

    //@Scheduled(fixedDelay = 1000)
    public void submit(){
        //Integer i = queue.poll();
        //log.info("current_>>>>>>>>: "+i);
        //log.info("size_>>>>>>>>: "+queue.size());
        log.info("submit");
    }

    @PostMapping("/testIllegalWord")
    public IResult testIllegalWord(@RequestBody JSONObject data){
        JSONObject o = new JSONObject();
        o.put("info","info");
        return IResult.success(o);
    }
}
