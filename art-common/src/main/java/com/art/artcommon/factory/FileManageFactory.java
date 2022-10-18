package com.art.artcommon.factory;

import com.art.artcommon.constant.CustomException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * description
 * 文件工厂类
 * @author lou
 * @create 2022/10/17
 */
@Component
public class FileManageFactory extends BasePooledObjectFactory<ChannelSftp> {

    @Value("${upload-file.data.port}")
    private int port;
    @Value("${upload-file.data.password}")
    private String password;
    @Value("${upload-file.data.ip}")
    private String ip;
    @Value("${upload-file.data.user}")
    private String user;
    @Value("${upload-file.data.avatarpath}")
    private String filepath;

    @Override
    public ChannelSftp create() throws Exception {
        JSch jSch = new JSch();
        Session session = jSch.getSession(user,ip,port);
        if (session == null) {
            throw new CustomException("session is null");
        }
        session.setPassword(password);
        session.setConfig("userauth.gssapi-with-mic","no");
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(30000);
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }

    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channel) {
        return new DefaultPooledObject<>(channel);
    }
}
