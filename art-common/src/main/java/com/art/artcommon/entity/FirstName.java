package com.art.artcommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@TableName("First_Name")
@Accessors(chain = true)
public class FirstName {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String first_name;
    @TableField
    private String style;
    @TableField
    private String category;
    @TableField
    private int has_num;
    @TableField
    private String area;
}
