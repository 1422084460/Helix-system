package com.art.artweb.async;

import com.art.artcommon.entity.Error_log;
import com.art.artcommon.mapper.Error_logMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * description
 * 异步任务处理
 * @author lou
 * @create 2022/3/22
 */
@Component
@Slf4j
public class AsyncTaskMain {

    @Autowired
    private Error_logMapper errorLogMapper;

    @Value("${dev-pattern.devInfo}")
    private String devInfo;

    @Async
    public void doAsync(){
        try {
            TimeUnit.SECONDS.sleep(10);
            System.out.println("======123Async======");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void asyncError(String msg,String code,String path,long timestamp){
        Error_log log = new Error_log();
        log.setError_msg(msg)
                .setError_code(code)
                .setInterface_path(path)
                .setCaller(Thread.currentThread().getName())
                .setTimestamp(timestamp)
                .setDevInfo(devInfo);
        try {
            errorLogMapper.insert(log);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
