package com.art.artcreator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * description
 * 作者发布章节日志信息记录
 * @author lou
 * @create 2022/11/3
 */
@Data
@NoArgsConstructor
@TableName("Story_Publish_Log")
@Accessors(chain = true)
public class PublishLog {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String email;
    @TableField
    private String novel;
    @TableField
    private Integer current;
    @TableField
    private String name;
    @TableField
    private String status;
}
