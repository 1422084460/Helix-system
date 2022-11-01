package com.art.artadmin.dto;

import com.art.artadmin.entity.User;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * description
 * 用户注册信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@AllArgsConstructor
public class RegisterUserInfo extends User {

    @NotNull(message = "时间戳不能为空")
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }
}
