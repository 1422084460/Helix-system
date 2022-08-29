package com.art.artadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@TableName("Admin_User")
@NoArgsConstructor
@Accessors(chain = true)
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String username;
    @TableField
    private String email;
    @TableField
    private String password;
    @TableField
    private String avatar;
    @TableField
    private BigDecimal money;
    @TableField
    private String is_avatar_prepare;
    @TableField
    private String status;
    @TableField
    private String is_admin;
    @TableField
    private int score;
    @TableField
    private String phone_num;
    @TableField
    private String create_time;
    @TableField
    private String role_key;
}
