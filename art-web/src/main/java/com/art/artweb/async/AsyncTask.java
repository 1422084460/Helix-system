package com.art.artweb.async;

import lombok.extern.slf4j.Slf4j;
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
public class AsyncTask {

    @Async
    public void doAsync(){
        try {
            TimeUnit.SECONDS.sleep(10);
            System.out.println("======123Async======");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
