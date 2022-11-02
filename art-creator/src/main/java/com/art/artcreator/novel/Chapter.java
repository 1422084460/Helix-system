package com.art.artcreator.novel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @TableField
    @NotBlank(message = "章节id不能为空")
    private String chapter_id;
    @TableField
    @NotBlank(message = "章节名不能为空")
    private String chapterName;
    @TableField(typeHandler = FastjsonTypeHandler.class)
    @NotNull(message = "段落内容必填")
    private List<ChapterPara> detail;
    @TableField
    private Integer count;
    @TableField
    private String create_time;
    @TableField
    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChapterPara {

        private Integer location;
        private String para;
    }

    public ChapterPara generatePara(String detail){
        return new ChapterPara().setPara(detail);
    }
}
