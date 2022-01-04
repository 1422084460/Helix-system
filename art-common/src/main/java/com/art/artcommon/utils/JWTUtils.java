package com.art.artcommon.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JWTUtils {

    //设置密钥
    private static final String signature = "!@#qdh%f(sj&";

    //生成token
    public static String getToken(Map<String,String> map){
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND,60);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(signature)).toString();
    }

    //验证token
    public static DecodedJWT verify(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(signature)).build().verify(token);
        return verify;
    }
}
