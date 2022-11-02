package com.art.artcreator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * description
 * 创建者基本信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorBaseInfo {

    @NotBlank(message = "邮箱不能为空")
    private String email;
    @NotBlank(message = "nameId不能为空")
    private String nameId;
    @NotNull(message = "评分不能为空")
    private Integer score;
}
