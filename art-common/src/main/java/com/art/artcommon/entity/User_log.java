package com.art.artcommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@TableName("User_log")
@ToString
@Accessors(chain = true)
public class User_log {

    private Integer id;
    @TableField
    private String username;
    @TableField
    private String email;
    @TableField
    private String login_time;
}
