package com.art.artadmin.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * description
 * 用户私人信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@NoArgsConstructor
@AllArgsConstructor
public class SecretUserInfo extends BaseUserInfo{

    private String code;
    private String newPassWord;

    public String getCode() {
        return code;
    }

    public String getNewPassWord() {
        return newPassWord;
    }
}
