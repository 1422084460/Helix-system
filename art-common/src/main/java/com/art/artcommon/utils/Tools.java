package com.art.artcommon.utils;

import org.apache.commons.mail.HtmlEmail;
import org.springframework.util.DigestUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param time 时间戳
     * @return String
     */
    public static String dateToStr(long time){
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    /**
     * 时间戳转字符串,格式 yyyy-MM-dd HH:mm:ss
     * @param time 时间戳
     * @return String
     */
    public static String date_To_Str(long time){
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 生成一个存储对应 size 大小的随机无序集合
     * @param size 存储大小
     * @return List<Integer>
     */
    public static List<Integer> getRandom(int size){
        Random random = new Random();
        List<Integer> list = new ArrayList<>(size);
        for (int i=0;i<size;i++){
            Integer value = random.nextInt(size);
            if (!list.contains(value)){
                list.add(value);
                continue;
            }
            i--;
        }
        return list;
    }

    /**
     * 重新排列无序集合
     * @param size 存储大小
     * @return List<Integer>
     */
    public static List<Integer> reSort(int size){
        return getRandom(size);
    }

    /**
     * 为保证随机，当所需数据集小于筛选数据集时进行数据集的指针移动
     * @param list 所需改变指针的数据集合
     * @param sortedListSize 筛选出的数据集大小
     * @return List<Integer>
     */
    public static List<Integer> removeCursor(List<Integer> list,int sortedListSize){
        int distance = sortedListSize - list.size();
        if (distance!=0){
            Random random = new Random();
            int cursor = random.nextInt(distance);
            return list.stream().map(i -> i+cursor).collect(Collectors.toList());
        }
        return list;
    }

    public static String toMd5(String pwd){
        return DigestUtils.md5DigestAsHex(pwd.getBytes());
    }

    public static void sendEmail(String receiver,String code){
        try {
            HtmlEmail html = new HtmlEmail();
            html.setHostName("smtp.qq.com");
            html.setCharset("utf-8");
            html.addTo(receiver);
            html.setFrom("1544096285@qq.com","Creator服务");
            html.setAuthentication("1544096285@qq.com","rxgauhezhrysihjj");
            html.setSubject("验证码");
            html.setMsg("验证码:<"+code+">");
            html.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
