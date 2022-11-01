package com.art.artadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * description
 * 用户基本信息
 * @author lou
 * @create 2022/11/1
 */
@Data
public class BaseUserInfo {

    @NotBlank(message = "邮箱不能为空")
    private String email;
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;
    private String username;
    private String password;
}
