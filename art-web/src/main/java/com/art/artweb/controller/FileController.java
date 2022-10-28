package com.art.artweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.CustomException;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.utils.FileUtils;
import com.art.artcommon.utils.Tools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * description
 * 文件管理控制器
 * @author lou
 * @create 2022/9/9
 */
@Api("文件管理接口")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileUtils fileUtils;

    /**
     * 文件上传
     * @param file 源文件
     * @return IResult
     */
    @ApiOperation("文件上传")
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

    /**
     * 文件下载
     * @param data 请求数据
     * @param response HttpServletResponse
     * @return IResult
     */
    @ApiOperation("文件下载")
    @RequestMapping("/downloadFile")
    public IResult downloadFile(@RequestBody JSONObject data, HttpServletResponse response) {
        try {
            fileUtils.download(response,data.getString("fileName"));
        }catch (Exception e){
            throw new CustomException("下载失败");
        }
        return IResult.success();
    }

    /**
     * 获取文件池信息
     * @return String
     */
    @ApiOperation("获取文件池信息")
    @PostMapping("/getFileConfigInfo")
    public String getInfo(){
        return fileUtils.getConfigInfo();
    }
}

