package com.art.artweb.exception;

import com.art.artcommon.constant.CustomException;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artweb.async.AsyncTaskMain;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private AsyncTaskMain task;

    @ExceptionHandler(Exception.class)
    public IResult handlerException(Exception e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"系统异常:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("系统异常",R.CODE_FAIL,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,"系统异常", R.CODE_FAIL);
    }

    @ExceptionHandler(NullPointerException.class)
    public IResult handlerNullPointerException(NullPointerException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"空指针异常:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("空指针异常",R.CODE_FAIL,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,"空指针异常", R.CODE_FAIL);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public IResult handlerTokenExpiredException(TokenExpiredException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"token过期:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("token过期",R.CODE_TOKEN_EXPIRE,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,"token过期", R.CODE_TOKEN_EXPIRE);
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public IResult handlerSignatureVerificationException(SignatureVerificationException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"token签名无效:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("token签名无效",R.CODE_TOKEN_SIGNATURE_INVALID,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,"token签名无效", R.CODE_TOKEN_SIGNATURE_INVALID);
    }

    @ExceptionHandler(AlgorithmMismatchException.class)
    public IResult handlerAlgorithmMismatchException(AlgorithmMismatchException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"token算法不一致:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("token算法不一致",R.CODE_TOKEN_ALGORITHM_MISMATCH,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,"token算法不一致", R.CODE_TOKEN_ALGORITHM_MISMATCH);
    }

    @ExceptionHandler(ClassCastException.class)
    public IResult handlerClassCastException(ClassCastException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"类型转换异常:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("类型转换异常",R.CODE_FAIL,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,"类型转换异常", R.CODE_FAIL);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public IResult handlerMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"方法参数校验异常:"+e.getMessage());
        log.error("具体错误位置===>>"+Arrays.toString(e.getStackTrace()));
        task.asyncError("方法参数校验异常",R.CODE_FAIL,request.getRequestURI(),new Date().getTime());
        return IResult.fail(null,e.getBindingResult().getFieldError().getDefaultMessage(), R.CODE_FAIL);
    }

    @ExceptionHandler(CustomException.class)
    public IResult handlerCustomException(CustomException e, HttpServletRequest request){
        log.error("访问接口:"+request.getRequestURI()+"失败===>>"+"CustomExp:"+e.getMessage());
        return IResult.fail(null,e.getMessage(),e.getCode());
    }
}
