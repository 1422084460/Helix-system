package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.utils.FileUpLodeUtil;
import com.art.artcommon.utils.Tools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/upload")
public class FileController {

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
            FileUpLodeUtil.upload(file.getBytes(),newFileName);
        }catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject object = new JSONObject();
        object.put("file",newFileName);
        return IResult.success(object);
    }
}

