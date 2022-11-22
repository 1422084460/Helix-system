package com.art.artcreator.mapper;

import com.alibaba.fastjson.JSONObject;
import com.art.artcreator.novel.Chapter;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * description
 * 章节信息db操作映射
 * @author lou
 * @create 2022/8/29
 */
public interface ChapterMapper extends BaseMapper<Chapter> {

    @Select("select " +
            "a.id," +
            "a.email," +
            "a.chapter_id," +
            "a.chapterName," +
            "a.detail," +
            "a.count," +
            "a.create_time," +
            "b.author_name," +
            "b.novel_name," +
            "b.para_current " +
            "from " +
            "Story_NovelChapter a," +
            "Story_NovelChapterList b" +
            " ${ew.customSqlSegment}")
    JSONObject queryOneChapter(@Param("ew") Wrapper wrapper);

    @Select("select " +
            "a.chapterName," +
            "b.para_current " +
            "from " +
            "Story_NovelChapter a," +
            "Story_NovelChapterList b" +
            " ${ew.customSqlSegment}")
    List<JSONObject> queryChapters(@Param("ew") Wrapper wrapper);
}
