package com.art.artcommon.utils;

import com.art.artcommon.factory.SftpPool;
import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

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

    public void download(HttpServletResponse response, String fileName){
        try {
            response.setHeader("content-disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8"));
            InputStream in = getResource(fileName);
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer,0,len);//将缓冲区的数据输出到客户端浏览器
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream getResource(String fileName){
        ChannelSftp sftp = null;
        try {
            sftp = pool.borrowObject();
            sftp.cd(filepath);
            return sftp.get(fileName);
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
