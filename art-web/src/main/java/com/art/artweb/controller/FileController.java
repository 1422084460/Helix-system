package com.art.artweb.controller;

import com.art.artcommon.utils.FileUpLodeUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping("/api/upload")
@RestController
public class FileController {

    @RequestMapping("/getUploadUrl")
    public String upLoadAvatar(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString()+fileName;
        //String filePath = "/usr/local/images/";
        String filePath = "D:\\images\\";
        try {
            FileUpLodeUtil.fileUpLoad(file.getBytes(),filePath,newFileName);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return newFileName;
    }
}
