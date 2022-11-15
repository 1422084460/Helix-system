package com.art.artcommon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * description
 * 缓存数据注解
 * @author lou
 * @create 2022/7/29
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

    String prefix() default "custom_cache_task_";
    String key();
    long timeout() default 0;
    TimeUnit timeunit() default TimeUnit.SECONDS;

}
