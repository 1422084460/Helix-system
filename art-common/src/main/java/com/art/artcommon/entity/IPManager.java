package com.art.artcommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description
 *
 * @author lou
 * @create 2022/6/15
 */
@Data
@TableName("IPM")
@Accessors(chain = true)
public class IPManager {

    @TableId(type = IdType.AUTO)
    private int id;
    @TableField
    private String ip;
    @TableField
    private int count;
    @TableField
    private String blacklist;
}
