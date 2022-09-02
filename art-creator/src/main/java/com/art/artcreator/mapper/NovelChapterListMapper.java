package com.art.artcreator.mapper;

import com.alibaba.fastjson.JSONObject;
import com.art.artcreator.novel.NovelChapterList;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * description
 *
 * @author lou
 * @create 2022/8/29
 */
public interface NovelChapterListMapper extends BaseMapper<NovelChapterList> {

    @Select("select " +
            "author_name," +
            "novel_name," +
            "cover," +
            "novel_type," +
            "novel_score," +
            "introduction," +
            "paras_count " +
            "from " +
            "Story_NovelChapterList" +
            " ${ew.customSqlSegment}")
    List<JSONObject> queryUniqueChaptersForShow(@Param("ew") Wrapper wrapper);
}
