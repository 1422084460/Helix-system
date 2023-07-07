package com.art.artweb;

import com.art.artweb.config.CacheThreadPoolConfig;
import com.art.artweb.config.TaskThreadPoolConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({TaskThreadPoolConfig.class, CacheThreadPoolConfig.class})
@ComponentScan(basePackages = {"com.art"})
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = {"com.art.**.mapper"})
public class HelixApplication {
//
    public static void main(String[] args) {
        SpringApplication.run(HelixApplication.class, args);
    }

}
