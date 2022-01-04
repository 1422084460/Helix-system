package com.art.artweb.interceptor;

import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.JWTUtils;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JWTInterceptor extends HandlerInterceptorAdapter {
    //拦截器于过滤器后执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader("token");
        IResult map;
        String name = Thread.currentThread().getName();
        try {
            JWTUtils.verify(token);
            map = IResult.success(null);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证", map));
        }catch (SignatureVerificationException e){
            e.printStackTrace();
            map = IResult.fail(null,R.MSG_TOKEN_SIGNATURE_INVALID,R.CODE_TOKEN_SIGNATURE_INVALID);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证",map));
        }catch (TokenExpiredException e){
            //e.printStackTrace();
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            map = IResult.fail(null,R.MSG_TOKEN_EXPIRE,R.CODE_TOKEN_EXPIRE);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证",map));
        }catch (AlgorithmMismatchException e){
            e.printStackTrace();
            map = IResult.fail(null,R.MSG_TOKEN_ALGORITHM_MISMATCH,R.CODE_TOKEN_ALGORITHM_MISMATCH);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证",map));
        }catch (Exception e) {
            e.printStackTrace();
            map = IResult.fail(null,R.MSG_TOKEN_INVALID,R.CODE_TOKEN_INVALID);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证",map));
        }
        System.out.println("进入拦截器"+token);
        return true;
    }
}
