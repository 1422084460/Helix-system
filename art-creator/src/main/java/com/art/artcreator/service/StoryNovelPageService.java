package com.art.artcreator.service;

import com.art.artcommon.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * description
 * 小说主页面服务类
 * @author lou
 * @create 2022/11/2
 */
@Slf4j
@Service
public class StoryNovelPageService {

    private static final String RANK_KEY_1 = "Novel_Score_Rank";
    private static final String RANK_KEY_2 = "Novel_Popularity_Rank";

    /**
     * 展示首页小说各类排行榜
     * @param sortMode 以何种目标进行排序
     * @param asc 排序方式是否正序
     * @return Set<String>
     */
    public Set<String> showNovelsRank(String sortMode, boolean asc){
        Set<String> source = null;
        if (sortMode.equals("score")){
            source = RedisUtil.getZSetValues(RANK_KEY_1,0,-1,asc);
        }
        if (sortMode.equals("popularity")){
            source = RedisUtil.getZSetValues(RANK_KEY_2,0,-1,asc);
        }
        return source;
    }
}
