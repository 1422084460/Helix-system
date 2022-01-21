package com.art.artweb.interceptor;

import com.alibaba.fastjson.JSONObject;
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
import java.io.IOException;

@Slf4j
public class JWTInterceptor extends HandlerInterceptorAdapter {
    //拦截器于过滤器后执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String token = request.getHeader("token");
        IResult res;
        String name = Thread.currentThread().getName();
        try {
            JWTUtils.verify(token);
            res = IResult.success(null);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证", res));
            return true;
        }catch (SignatureVerificationException e){
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            res = IResult.fail(null,R.MSG_TOKEN_SIGNATURE_INVALID,R.CODE_TOKEN_SIGNATURE_INVALID);
        }catch (TokenExpiredException e){
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            res = IResult.fail(null,R.MSG_TOKEN_EXPIRE,R.CODE_TOKEN_EXPIRE);
        }catch (AlgorithmMismatchException e){
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            res = IResult.fail(null,R.MSG_TOKEN_ALGORITHM_MISMATCH,R.CODE_TOKEN_ALGORITHM_MISMATCH);
        }catch (Exception e) {
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            res = IResult.fail(null,R.MSG_TOKEN_INVALID,R.CODE_TOKEN_INVALID);
        }
        System.out.println("进入拦截器"+token);
        falseResult(response,res);
        return false;
    }

    public void falseResult(HttpServletResponse response,IResult res) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Object json = JSONObject.toJSON(res);
        response.getWriter().println(json);
        return;
    }
}
