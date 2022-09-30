package com.art.artmanage.service;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.utils.Tools;
import com.art.artmanage.entity.SystemUpdateLog;
import com.art.artmanage.mapper.SystemUpdateLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description
 * 后台系统管理服务
 * @author lou
 * @create 2022/9/27
 */
@Service
public class ManageSystemService {

    @Autowired
    private SystemUpdateLogMapper systemUpdateLogMapper;

    /**
     * 更新系统日志
     * @param data 请求数据
     * @return int
     */
    public int updateSystemLog(JSONObject data){
        long timestamp = data.getLongValue("timestamp");
        String details = data.getString("details");
        String executor = data.getString("executor");
        SystemUpdateLog log = new SystemUpdateLog()
                .setUpdate_time(Tools.date_To_Str(timestamp))
                .setUpdate_details(details)
                .setUpdate_executor(executor);
        return systemUpdateLogMapper.insert(log);
    }

    /**
     * 展示系统更新日志
     * @param data 请求数据
     * @return int
     */
    public JSONObject showSystemLog(JSONObject data){
        QueryWrapper<SystemUpdateLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<SystemUpdateLog> logList = systemUpdateLogMapper.selectList(wrapper);
        JSONObject object = new JSONObject();
        object.put("systemlog",logList);
        return object;
    }
}
