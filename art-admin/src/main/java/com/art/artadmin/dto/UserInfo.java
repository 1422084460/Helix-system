package com.art.artadmin.dto;

import com.art.artadmin.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * description
 * 用户信息前端映射
 * @author lou
 * @create 2022/10/28
 */
public class UserInfo extends User {

    @NotBlank(message = "登录方式不能为空")
    private String login_mode;
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;
    private String code;

    public String getLogin_mode() {
        return login_mode;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }
}
