package com.art.artadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@TableName("Admin_User")
@NoArgsConstructor
@Accessors(chain = true)
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    @NotBlank(message = "用户昵称不能为空")
    private String username;
    @TableField
    @NotBlank(message = "用户邮箱不能为空")
    private String email;
    @TableField
    @NotBlank(message = "用户密码不能为空")
    private String password;
    @TableField
    @NotBlank(message = "用户头像不能为空")
    private String avatar;
    @TableField
    private BigDecimal money;
    @TableField
    private String is_avatar_prepare;
    @TableField
    private String status;
    @TableField
    private int score;
    @TableField
    private int sign_in_status;
    @TableField
    private int last_sign_in;
    @TableField
    private int sign_in_count;
    @TableField
    private String phone_num;
    @TableField
    private String create_time;
    @TableField
    private String role_key;
}
