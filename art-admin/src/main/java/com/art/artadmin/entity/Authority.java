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
 * 权限管理实体类
 * @author lou
 * @create 2022/8/26
 */
@Data
@TableName("Helix_authority")
@NoArgsConstructor
@Accessors(chain = true)
public class Authority {

    @TableId(type = IdType.AUTO)
    private Integer id;
}
