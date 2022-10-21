package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.CustomException;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.utils.FileUtils;
import com.art.artcommon.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/upload")
public class FileController {

    @Autowired
    private FileUtils fileUtils;

    /**
     * 文件上传
     * @param file 源文件
     * @return IResult
     */
    @RequestMapping("/upLoadFile")
    public IResult upLoadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long timestamp = System.currentTimeMillis();
        String newFileName = Tools.getCode() + Tools.dateToStr(timestamp).substring(0,8) + fileName;
        try {
            fileUtils.upload(file.getBytes(),newFileName);
        }catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject object = new JSONObject();
        object.put("fileName",newFileName);
        return IResult.success(object);
    }

    @RequestMapping("/downloadFile")
    public IResult downloadFile(@RequestBody JSONObject data) {
        String localPath = data.getString("localPath");
        String localName = data.getString("localName");
        String name = data.getString("name");
        try {
            fileUtils.download(localPath,localName,name);
        }catch (Exception e){
            throw new CustomException("下载失败");
        }
        return IResult.success();
    }

    @PostMapping("/getupLoadFileConfigInfo")
    public String getInfo(){
        return fileUtils.getConfigInfo();
    }
}

