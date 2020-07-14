package com.esdemo.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title：agentApi2
 * @Description：接口签名注解
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SignValidate {

    /**
     * 是否需要签名
     */
    boolean needSign() default true;
}
