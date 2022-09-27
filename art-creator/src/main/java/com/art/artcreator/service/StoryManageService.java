package com.art.artcreator.service;

import com.alibaba.fastjson.JSONObject;
import com.art.artcreator.entity.FirstName;
import com.art.artcreator.entity.LastName;
import com.art.artcreator.mapper.FirstNameMapper;
import com.art.artcreator.mapper.LastNameMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description
 * 管理服务类
 * @author lou
 * @create 2022/9/9
 */
@Service
@Slf4j
public class StoryManageService {

    @Autowired
    private FirstNameMapper first;
    @Autowired
    private LastNameMapper last;

    /**
     * 帮助开发者（查询姓、名）
     * @param data 请求数据
     * @return List
     */
    public List helpQuery(JSONObject data){
        String flag = data.getString("flag");
        List list;
        if (flag.equals("first")){
            QueryWrapper<FirstName> wrapper = new QueryWrapper<>();
            list = first.selectList(wrapper);
        }else {
            QueryWrapper<LastName> wrapper = new QueryWrapper<>();
            list = last.selectList(wrapper);
        }
        return list;
    }

    /**
     * 帮助开发者（修改姓、名）
     * @param data 请求数据
     * @return int
     */
    public int helpUpdate(JSONObject data){
        String flag = data.getString("flag");
        int res = 0;
        if (flag.equals("first")){
            UpdateWrapper<FirstName> wrapper = new UpdateWrapper<>();
            wrapper.set("category",data.getString("newValueFC"))
                    .set("style",data.getString("newValueFS"))
                    .eq("first_name",data.getString("nameF"));
            res = first.update(null,wrapper);
        }else {
            UpdateWrapper<LastName> wrapper = new UpdateWrapper<>();
            wrapper.set("category",data.getString("newValueLC"))
                    .set("style",data.getString("newValueLS"))
                    .eq("first_name",data.getString("nameL"));
            res = last.update(null,wrapper);
        }
        return res;
    }
}
