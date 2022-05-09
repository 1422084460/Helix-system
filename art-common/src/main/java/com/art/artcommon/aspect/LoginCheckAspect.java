package com.art.artcommon.aspect;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.CustomExp;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.AuthL;
import com.art.artcommon.utils.AopTargetUtils;
import com.art.artcommon.utils.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * description
 * 单点登录切面类
 * @author lou
 * @create 2022/5/7
 */
@Aspect
@Component
public class LoginCheckAspect {

    @Pointcut("@annotation(com.art.artcommon.custominterface.AuthL)")
    public void auth(){}

    @Before("auth()")
    public void authLogin(JoinPoint point) throws Exception {
        String key1 = "user_auth_login";
        Object[] args = point.getArgs();
        JSONObject object = (JSONObject) args[0];
        String key2 = object.getString("email");
        if (RedisUtil.hasHashKey(key1,key2)){
            RedisUtil.reFresh(key1,key2);
        }else {
            Object target = point.getTarget();
            String methodName = point.getSignature().getName();
            AuthL authL = AopTargetUtils.getTarget(target).getClass()
                    .getDeclaredMethod(methodName, JSONObject.class)
                    .getAnnotation(AuthL.class);
            throw new CustomExp(R.CODE_LOGIN_INVALID,authL.message());
        }
    }
}
