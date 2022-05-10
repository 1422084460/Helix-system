package com.art.artcommon.aspect;

import com.art.artcommon.utils.AopTargetUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * description
 *
 * @author lou
 * @create 2022/4/11
 */
@Component
@Aspect
public class AllAspect {
    //考虑用around环绕建立锁，前置解锁，后置上锁

    @Pointcut("@annotation(com.art.artcommon.custominterface.Error)")
    public void cutError(){}

    @Before("cutError()")
    public void before(){
        System.out.println();
    }

//    @After("cutTaskExe()")
//    public void after(JoinPoint point) throws Exception {
//        Signature s = point.getSignature();
//        MethodSignature m = (MethodSignature)s;
//        Object target = point.getTarget();
//        Method method = target.getClass().getMethod(m.getName(),m.getParameterTypes());
//        TaskExe taskExe = method.getAnnotation(TaskExe.class);
//        String name = taskExe.name();
//        System.out.println("后置通知"+name);
//    }

    private String handlerError(HttpServletRequest req){
        return req.getRequestURI();
    }
}
