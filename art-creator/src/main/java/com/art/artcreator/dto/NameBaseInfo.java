package com.art.artcreator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * description
 * 姓名前端映射
 * @author lou
 * @create 2022/11/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameBaseInfo {

    @NotBlank(message = "所属地域不能为空")
    private String area;
    @NotBlank(message = "性别偏向不能为空")
    private String category;
    @NotBlank(message = "风格不能为空")
    private String style;
    @NotNull(message = "姓字数不能为空")
    private Integer first_has_num;
    @NotNull(message = "名字数不能为空")
    private Integer last_has_num;
    @NotNull(message = "必须判断是否包含中间名")
    private Boolean has_inner_name;
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
