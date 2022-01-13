package com.art.artcommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("User")
@Builder
public class User {
    @TableField
    private int id;
    @TableField
    private String email;
    @TableField
    private String password;
    @TableField
    private String avatar;
    @TableField
    private BigDecimal money;
    @TableField
    private boolean is_avatar_prepare;
    @TableField
    private String status;
    @TableField
    private String is_admin;
    @TableField
    private int score;
    @TableField
    private String phone_num;
}
