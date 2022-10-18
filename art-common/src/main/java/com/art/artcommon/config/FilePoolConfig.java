package com.art.artcommon.config;

import com.art.artcommon.factory.FileManageFactory;
import com.art.artcommon.factory.SftpPool;
import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description
 * 文件操作类连接池配置
 * @author lou
 * @create 2022/10/17
 */
@Configuration
public class FilePoolConfig {

    @Bean
    @ConfigurationProperties(prefix = "upload-file.pool")
    public GenericObjectPoolConfig<ChannelSftp> config(){
        return new GenericObjectPoolConfig<>();
    }

    @Bean
    public SftpPool sftpPool(FileManageFactory factory,GenericObjectPoolConfig<ChannelSftp> config){
        return new SftpPool(factory,config);
    }
}
