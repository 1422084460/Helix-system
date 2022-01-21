package com.art.artcommon.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

    /**
     * 生成订单号
     * @param userId
     * @param time
     * @return
     */
    public static String createOrderId(String userId,long time){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateToStr(time));
        stringBuilder.append(userId);
        return stringBuilder.toString();
    }

    /**
     * 时间戳转字符串,格式 yyyyMMddhhmmss
     * @param time
     * @return
     */
    public static String dateToStr(long time){
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    /**
     * 时间戳转字符串,格式 yyyy-MM-dd hh:mm:ss
     * @param time
     * @return
     */
    public static String date_To_Str(long time){
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
