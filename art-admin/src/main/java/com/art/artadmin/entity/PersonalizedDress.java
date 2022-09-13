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
 * 用户个性装扮类
 * @author lou
 * @create 2022/9/9
 */
@Data
@TableName("Admin_Personalized_dress")
@NoArgsConstructor
@Accessors(chain = true)
public class PersonalizedDress {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String item_name;
    @TableField
    private String item_id;
    @TableField
    private String item_url;
    @TableField
    private String item_category;
    @TableField
    private int item_price;
    @TableField
    private int overdue;
    @TableField
    private int is_available;
}
