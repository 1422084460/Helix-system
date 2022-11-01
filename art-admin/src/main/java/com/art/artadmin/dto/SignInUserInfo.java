package com.art.artadmin.dto;

import lombok.AllArgsConstructor;

/**
 * description
 * 用户签到信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@AllArgsConstructor
public class SignInUserInfo extends BaseUserInfo{

    private int score;
    private int signInCount;

    public int getScore() {
        return score;
    }

    public int getSignInCount() {
        return signInCount;
    }
}
