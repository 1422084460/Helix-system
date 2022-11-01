package com.art.artadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * description
 * 验证码前端映射
 * @author lou
 * @create 2022/10/31
 */
@Data
public class CodeValidation {

    @NotBlank(message = "邮箱不能为空")
    private String email;
    @NotBlank(message = "验证码不能为空")
    private String code;
}
