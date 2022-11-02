package com.art.artcreator.dto;

import com.art.artcreator.novel.Chapter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * description
 * 章节信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@NoArgsConstructor
@AllArgsConstructor
public class ChapterInfo extends Chapter {

    @NotNull(message = "时间戳不能为空")
    private Long timestamp;
    @NotBlank(message = "小说名不能为空")
    private String novelName;
    @NotNull(message = "当前章节位置不能为空")
    private Integer paraCurrent;

    public Long getTimestamp() {
        return timestamp;
    }

    public String getNovelName() {
        return novelName;
    }

    public Integer getParaCurrent() {
        return paraCurrent;
    }
}
