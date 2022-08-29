package com.art.artcreator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@TableName("Story_Last_Name")
@Accessors(chain = true)
public class LastName {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String last_name;
    @TableField
    private String style;
    @TableField
    private String category;
    @TableField
    private int has_num;
    @TableField
    private String area;
}
