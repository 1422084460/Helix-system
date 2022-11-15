package com.art.artcommon.annotations;

import com.art.artcommon.constant.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthL {

    String message() default R.LOGIN_INVALID;
}
