package com.art.artcommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("User")
public class User {
    @TableField
    private int id;
    @TableField
    private String email;
    @TableField
    private String password;
    @TableField
    private String imgUrl;
}
