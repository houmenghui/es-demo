package com.esdemo.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记接口开发完成,可联调
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-16 10:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface KqSwaggerDeveloped {
}
