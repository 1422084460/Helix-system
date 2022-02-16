package com.art.artweb.interceptor;

import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.JWTUtils;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JWTInterceptor extends HandlerInterceptorAdapter {
    //拦截器于过滤器后执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        String token = request.getHeader("token");
        IResult res;
        String name = Thread.currentThread().getName();
        try {
            JWTUtils.verify(token);
            res = IResult.success(null);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证", res));
            return true;
        }catch (TokenExpiredException e){
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            res = IResult.fail(null,R.MSG_TOKEN_EXPIRE,R.CODE_TOKEN_EXPIRE);
            if (request.getRequestURI().equals("/api/user/login")){
                Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证", res));
                return true;
            }
            throw new TokenExpiredException(R.MSG_TOKEN_EXPIRE);
        }
    }
}
