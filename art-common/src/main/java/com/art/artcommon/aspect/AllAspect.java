package com.art.artcommon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author lou
 * @create 2022/4/11
 */
@Component
@Aspect
public class AllAspect {

    @Pointcut("@annotation(com.art.artcommon.custominterface.Error)")
    public void cutError(){}

    @Around("cutError()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before");
        joinPoint.proceed();
        System.out.println("after");
    }
}
