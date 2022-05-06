package com.art.artcommon.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * description
 * 日志记录
 * @author lou
 * @create 2022/5/6
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(com.art.artcommon.custominterface.Logs)")
    public void logCut(){}

    @Around("logCut()")
    public Object logExecutor(ProceedingJoinPoint pjp) throws Throwable {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        Object[] array = pjp.getArgs();
        if (array.length==0){
            log.info("调用方法:{}:{},无请求参数", className, methodName);
        }else {
            log.info("调用方法:{}:{},请求参数为:{}", className, methodName, Arrays.toString(array));
        }
        log.info("URL:{}",request.getRequestURL().toString());
        log.info("ip:{}",request.getRemoteAddr());
        Object proceed = pjp.proceed();
        log.info("调用方法:{}:{},返回值为:{}",className,methodName,proceed.toString());
        log.info("耗时:{}ms",System.currentTimeMillis()-startTime.get());
        return proceed;
    }
}
