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
 * 章节类和段落内部类
 * @author lou
 * @create 2022/8/29
 */
@Data
@TableName("Story_NovelChapter")
@Accessors(chain = true)
public class Chapter {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String email;
    @TableField
    private String chapter_id;
    @TableField
    private String chapterName;
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<ChapterPara> detail;
    @TableField
    private Integer count;
    @TableField
    private String create_time;

    @Data
    public class ChapterPara {

        private String para;
    }

    public ChapterPara generatePara(String detail){
        return new ChapterPara().setPara(detail);
    }
}
