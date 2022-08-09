package com.art.artcommon.custominterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedTable {

    String prefix() default "all_cache_task_";
    String key();
    String moduleName();
    String tableName();
    long timeout() default 0;
    TimeUnit timeunit() default TimeUnit.SECONDS;
}
