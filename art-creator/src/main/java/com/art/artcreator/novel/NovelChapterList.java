package com.art.artcreator.novel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * description
 * 小说类
 * @author lou
 * @create 2022/5/19
 */
@Data
@TableName("Story_NovelChapterList")
@Accessors(chain = true)
public class NovelChapterList {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String email;
    @TableField
    private String author_name;
    @TableField
    private String novel_name;
    @TableField
    private String novel_type;
    @TableField
    private String novel_score;
    @TableField
    private String introduction;
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> chapter_ids;
    @TableField
    private Integer paras_count;
    @TableField
    private Integer para_current;
    @TableField
    private String cover;
}
