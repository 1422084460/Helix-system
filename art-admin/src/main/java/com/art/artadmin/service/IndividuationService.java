package com.art.artadmin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artadmin.entity.Character;
import com.art.artadmin.entity.PersonalizedDress;
import com.art.artadmin.mapper.PersonalizedDressMapper;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * description
 * 后台管理服务类
 * @author lou
 * @create 2022/9/9
 */
@Service
public class IndividuationService {

    @Autowired
    private PersonalizedDressMapper personalizedDressMapper;

    /**
     * 修改用户角色权限
     * @param data 请求数据
     * @return boolean
     */
    public boolean updateModule(JSONObject data){
        UpdateWrapper<Character> wrapper = new UpdateWrapper<>();
        return true;
    }

    /**
     * 添加个性装扮
     * @param data 请求数据
     * @return int
     */
    public int addPersonalizedDress(JSONObject data){
        long timestamp = data.getLongValue("timestamp");
        String itemName = data.getString("itemName");
        String itemUrl = data.getString("itemUrl");
        int itemPrice = data.getIntValue("itemPrice");
        String itemCategory = data.getString("itemCategory");
        int overdue = data.getIntValue("overdue");
        String itemId = Tools.getCode() + Tools.dateToStr(timestamp).substring(0,8);
        PersonalizedDress entity = new PersonalizedDress()
                .setItem_id(itemId)
                .setItem_name(itemName)
                .setItem_url(itemUrl)
                .setItem_category(itemCategory)
                .setItem_price(itemPrice)
                .setOverdue(overdue)
                .setIs_available(1);
        return personalizedDressMapper.insert(entity);
    }

    /**
     * 展示个性装扮
     * @param data 请求数据
     * @return List<PersonalizedDress>
     */
    public List<PersonalizedDress> showPersonalizedDress(JSONObject data){
        List<PersonalizedDress> list = null;
        if (!RedisUtil.hasKey("PersonalizedDressList")) {
            list = personalizedDressMapper.selectList(null);
            RedisUtil.set("PersonalizedDressList", JSON.toJSONString(list),12, TimeUnit.HOURS);
        }
        String category = data.getString("category");
        int overdue = data.getIntValue("overdue");
        int available = data.getIntValue("available");
        if (list == null){
            String s = RedisUtil.get("PersonalizedDressList");
            list = JSON.parseObject(s,new TypeReference<List<PersonalizedDress>>(){});
        }
        return list.stream()
                .filter(c -> category.equals("") || c.getItem_category().equals(category))
                .filter(o -> overdue == 0 || o.getOverdue() == overdue)
                .filter(a -> available == 0 || a.getIs_available() == available)
                .collect(Collectors.toList());
    }
}
