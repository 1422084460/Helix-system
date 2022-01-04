package com.art.artcommon.utils;

import java.io.File;
import java.io.FileOutputStream;

public class FileUpLodeUtil {

    public static final String ServerPath = "http://118.31.55.121/usr/local/images/";

    public static void fileUpLoad(byte[] file,String filePath,String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
}
