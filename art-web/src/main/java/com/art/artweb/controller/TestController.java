package com.art.artweb.controller;

//import com.art.artcommon.mapper.WatcherMapper;
import com.alibaba.fastjson.JSON;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.JWTUtils;
import com.art.artservice.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
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
    private TestService service;

    @RequestMapping("/veritySth")
    public String veritySth(){
        System.out.println("进来了");
//        TestService service = new TestService();
        return service.getUser().getEmail();
    }
}
