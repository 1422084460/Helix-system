package com.art.artmanage.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

/**
 * description
 * 后台用户管理服务
 * @author lou
 * @create 2022/9/15
 */
@Service
public class ManageUserService {

    /**
     * 修改用户角色权限
     * @param data 请求数据
     * @return boolean
     */
    public boolean updateModule(JSONObject data){
        UpdateWrapper<Character> wrapper = new UpdateWrapper<>();
        return true;
    }
}
