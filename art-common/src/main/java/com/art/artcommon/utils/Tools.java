package com.art.artcommon.utils;

import com.art.artcommon.config.EmailConfig;
import com.art.artcommon.custominterface.AuthL;
import org.apache.commons.mail.HtmlEmail;
import org.reflections.Reflections;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    /**
     * md5加密
     * @param pwd 需加密密码
     * @return String
     */
    public static String toMd5(String pwd){
        String $ = "$%^_5";
        String newPwd = pwd + $;
        return DigestUtils.md5DigestAsHex(newPwd.getBytes());
    }

    /**
     * 随机生成6位验证码
     * @return String
     */
    private static String getCode(){
        String $ = "$%^_6";
        Date date = new Date();
        long time = date.getTime();
        String pre = time + $;
        String s = toMd5(pre);
        Random random = new Random();
        int i = random.nextInt(s.length() - 6);
        return s.substring(i,i+6).toUpperCase();
    }

    /**
     * 发送验证码邮件
     * @param receiver 邮件接收者
     */
    public static void sendEmail(String receiver){
        try {
            HtmlEmail html = new HtmlEmail();
            String code = getCode();
            html.setHostName(EmailConfig.getHostName());
            html.setCharset(EmailConfig.getCharset());
            html.addTo(receiver);
            html.setFrom(EmailConfig.getEmail(),EmailConfig.getName());
            html.setAuthentication(EmailConfig.getUserName(),EmailConfig.getPassword());
            html.setSubject(EmailConfig.getSubject());
            html.setMsg("验证码:<"+code+">");
            RedisUtil.set("verifyCode",code,5, TimeUnit.MINUTES);
            html.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取注解中的字段信息
     */
    public static void getAnnotationField(){
        String prefix = "com.art.artweb.controller";
        Reflections reflections = new Reflections(prefix);
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(RestController.class);
        for(Class<?> m : set){
            Method[] methods = m.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations){
                    if (annotation.annotationType() == AuthL.class){
                        System.out.println(method.getName());
                    }
                }
            }
        }
    }

    /**
     * 检查 ip访问次数
     * @param request
     * @return
     */
    public static String checkIpVisitCount(HttpServletRequest request){
        String ip = getIpAddr(request);
        if (!RedisUtil.hasHashKey("ip",ip)){
            RedisUtil.setHash("ip",ip,"0",30,TimeUnit.SECONDS);
        }
        Long nums = RedisUtil.inc(ip,false);
        if (nums<=10){
            return "请求正常...";
        }else {
            return "60秒内请求次数过多...";
        }
    }

    /**
     * 获取客户端 ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request){
        if (request == null){
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获取第一个非unknown的ip地址
     * @param ip
     * @return
     */
    private static String getMultistageReverseProxyIp(String ip){
        //多级反向代理检测
        if (ip != null && ip.indexOf(",")>0){
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips){
                if (!isUnknown(subIp)){
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 判断给定的字符串是否为unknown
     * @param checkString
     * @return
     */
    private static boolean isUnknown(String checkString){
        return isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 判断字符串是否为空
     * @param cs
     * @return
     */
    public static boolean isBlank(CharSequence cs){
        int strLen;
        if (cs != null && (strLen = cs.length())!=0){
            for (int i=0;i<strLen;++i){
                if (!Character.isWhitespace(cs.charAt(i))){
                    return false;
                }
            }
        }
        return true;
    }
}
