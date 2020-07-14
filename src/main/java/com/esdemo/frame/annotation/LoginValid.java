package com.esdemo.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * app接口登陆校验标注
 * 该注解可以用在类声明和方法体上
 * 1. 如果类和方法都没有这个注解,则该方法需要登陆校验
 * 2. 如果类和方法都有这个注解, 则以方法体上的注解优先,即
 * 如果类标注不需要登陆校验,而方法标注需要登陆校验,则该方法需要登陆校验
 * 如果类标注需要登陆校验,而该方法不需要登陆校验,这该方法不需要登陆校验
 * 3. 如果类和方法只有其中一个有该注解,这以该注解为准
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LoginValid {
    /**
     * 是否需要登陆
     */
    boolean needLogin() default true;
}
