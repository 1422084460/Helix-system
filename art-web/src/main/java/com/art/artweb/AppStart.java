package com.art.artweb;

import com.art.artcommon.entity.Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppStart implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initStore();
    }

    /**
     * 初始化全局变量 Store
     */
    private void initStore(){
        Store.getInstance();
        log.info("store初始化完成......");
    }
}
