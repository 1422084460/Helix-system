package com.art.artadmin.dto;

import lombok.Data;

/**
 * description
 * 用户基本信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@Data
public class UserBaseInfo {

    private String email;
    private String newPassWord;
    private Long timestamp;
    private int score;
    private int signInCount;
}
