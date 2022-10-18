package com.art.artcommon.factory;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * description
 * 文件操作类连接池
 * @author lou
 * @create 2022/10/17
 */
public class SftpPool {

    private final GenericObjectPool<ChannelSftp> genericObjectPool;

    public GenericObjectPoolConfig<ChannelSftp> myConfig;

    /**
     * 构造方法
     * @param sftpFactory 对象工厂类
     * @param config 连接池配置
     */
    public SftpPool(FileManageFactory sftpFactory, GenericObjectPoolConfig<ChannelSftp> config) {
        this.myConfig = config;
        this.genericObjectPool = new GenericObjectPool<>(sftpFactory, config);
    }

    /**
     * 从连接池中获取对象
     * @return ChannelSftp
     * @throws Exception 异常
     */
    public ChannelSftp borrowObject() throws Exception {
        return genericObjectPool.borrowObject();
    }

    /**
     * 向连接池中归还对象
     * @param obj 通道
     */
    public void returnObject(ChannelSftp obj) {
        genericObjectPool.returnObject(obj);
    }
}
