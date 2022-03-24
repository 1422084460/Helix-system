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
 * @create 2022/3/24
 */
@Data
@TableName("Error_Log")
@Accessors(chain = true)
public class Error_log {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String error_msg;
    @TableField
    private String error_code;
    @TableField
    private String interface_path;
    @TableField
    private String caller;
    @TableField
    private long timestamp;
}
