package com.art.artcommon.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author lou
 * @create 2022/8/9
 */
@Component
public class DruidDS {

    @Autowired
    private DruidConfig druidConfig;
    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Bean("DDS")
    public DruidDataSource init(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dataSourceConfig.getUrl());
        dataSource.setUsername(dataSourceConfig.getUsername());
        dataSource.setPassword(dataSourceConfig.getPassword());
        dataSource.setInitialSize(druidConfig.getInitialSize());
        dataSource.setMinIdle(druidConfig.getMinIdle());
        dataSource.setMaxActive(druidConfig.getMaxActive());
        dataSource.setMaxWait(druidConfig.getMaxWait());
        dataSource.setMinEvictableIdleTimeMillis(druidConfig.getMinEvictableIdleTimeMillis());
        return dataSource;
    }
}
