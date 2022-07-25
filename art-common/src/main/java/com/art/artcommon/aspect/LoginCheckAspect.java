package com.art.artcommon.aspect;

import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.CustomException;
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
        String prefix = "user_auth_";
        Object[] args = point.getArgs();
        JSONObject object = (JSONObject) args[0];
        boolean auth = object.getBooleanValue("is_admin");
        if (!auth){
            String suffix = object.getString("email");
            String key = prefix + suffix;
            if (RedisUtil.hasKey(key)){
                RedisUtil.reFresh(key);
            }else {
                Object target = point.getTarget();
                String methodName = point.getSignature().getName();
                AuthL authL = AopTargetUtils.getTarget(target).getClass()
                        .getDeclaredMethod(methodName, JSONObject.class)
                        .getAnnotation(AuthL.class);
                throw new CustomException(R.CODE_LOGIN_INVALID,authL.message());
            }
        }
    }
}
