package com.art.artcreator.novel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * description
 * 小说类
 * @author lou
 * @create 2022/5/19
 */
@Data
@TableName("Story_NovelChapterList")
@Accessors(chain = true)
public class Novel {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @TableField
    @NotBlank(message = "作者不能为空")
    private String author_name;
    @TableField
    @NotBlank(message = "小说名不能为空")
    private String novel_name;
    @TableField
    @NotBlank(message = "小说类型不能为空")
    private String novel_type;
    @TableField
    private String novel_score;
    @TableField
    private String novel_popularity;
    @TableField
    @NotBlank(message = "简介不能为空")
    private String introduction;
    @TableField
    private Integer paras_count;
    @TableField
    private Integer para_current;
    @TableField
    @NotBlank(message = "封面不能为空")
    private String cover;
}
