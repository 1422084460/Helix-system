package com.art.artcommon.mapper;

import com.art.artcommon.entity.User_log;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface User_logMapper extends BaseMapper<User_log> {

    int insertBatch(List<User_log> list);
}
