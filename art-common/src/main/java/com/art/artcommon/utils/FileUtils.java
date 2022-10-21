package com.art.artcommon.utils;

import com.art.artcommon.factory.SftpPool;
import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * description
 * 文件工具类
 * @author lou
 * @create 2022/10/17
 */

@Slf4j
@Component
public class FileUtils {

    @Autowired
    private SftpPool pool;

    @Value("${upload-file.data.avatarpath}")
    private String filepath;

    public void upload(byte[] bytes, String fileName) throws Exception{
        OutputStream outstream = null;
        ChannelSftp sftp = pool.borrowObject();
        try {
            sftp.cd(filepath);
            outstream = sftp.put(fileName);
            outstream.write(bytes);
        }
        finally {
            if (outstream != null) {
                outstream.flush();
                outstream.close();
            }
            pool.returnObject(sftp);
        }
    }

    public void download(String localPath, String localName, String name) throws Exception{
        InputStream in = getResource(name);
        OutputStream out = new FileOutputStream(localPath+localName);
        byte[] buff = new byte[1024 * 2];
        int read;
        if (in != null) {
            do {
                read = in.read(buff, 0, buff.length);
                if (read > 0) {
                    out.write(buff, 0, read);
                }
                out.flush();
            } while (read >= 0);
        }
        if (in != null){
            in.close();
            out.close();
        }
    }

    private InputStream getResource(String name){
        ChannelSftp sftp = null;
        try {
            sftp = pool.borrowObject();
            sftp.cd(filepath);
            return sftp.get(name);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            pool.returnObject(sftp);
        }
        return null;
    }

    public String getConfigInfo(){
        return "MaxTotal:"+pool.myConfig.getMaxTotal()+";MaxIdle:"+pool.myConfig.getMaxIdle()+";MinIdle:"+pool.myConfig.getMinIdle();
    }
}
