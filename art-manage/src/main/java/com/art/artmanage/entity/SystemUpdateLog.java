package com.art.artmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * description
 * 系统更新日志实体类
 * @author lou
 * @create 2022/9/26
 */
@Data
@TableName("Helix_system_update_log")
@NoArgsConstructor
@Accessors(chain = true)
public class SystemUpdateLog {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String update_time;
    @TableField
    private String update_details;
    @TableField
    private String update_executor;
}
