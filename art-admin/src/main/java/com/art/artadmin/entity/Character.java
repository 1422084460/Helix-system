package com.art.artadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * description
 * 用户角色实体类
 * @author lou
 * @create 2022/8/26
 */
@Data
@TableName("Helix_character")
@NoArgsConstructor
@Accessors(chain = true)
public class Character {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String character;
    @TableField
    private String auth_default;
    @TableField
    private String auth_new;
    @TableField
    private String role_key;
}
