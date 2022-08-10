package com.art.artcommon.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author lou
 * @create 2022/8/9
 */
@Component
public class DBUtils {

    @Autowired
    private JdbcTemplate template;
    @Autowired
    private DataSource dataSource;

    public String executeSql(String sql){
        template.setDataSource(dataSource);
        List<Map<String, Object>> maps = template.queryForList(sql);
        return JSON.toJSONString(maps);
    }
}
