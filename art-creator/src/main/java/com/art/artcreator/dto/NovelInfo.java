package com.art.artcreator.dto;

import com.art.artcreator.novel.NovelChapterList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * description
 * 小说信息前端映射
 * @author lou
 * @create 2022/11/1
 */
@NoArgsConstructor
@AllArgsConstructor
public class NovelInfo extends NovelChapterList {

    private String fuzzyWord;

    public String getFuzzyWord() {
        return fuzzyWord;
    }
}
