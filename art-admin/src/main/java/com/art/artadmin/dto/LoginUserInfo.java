package com.art.artadmin.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * description
 * 用户登录信息前端映射
 * @author lou
 * @create 2022/10/28
 */
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserInfo extends BaseUserInfo {

    @NotBlank(message = "登录方式不能为空")
    private String login_mode;
    private String code;

    public String getLogin_mode() {
        return login_mode;
    }

    public String getCode() {
        return code;
    }
}
