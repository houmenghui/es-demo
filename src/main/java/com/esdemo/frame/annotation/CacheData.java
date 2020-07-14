package com.esdemo.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-09-03 08:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheData {
    // 缓存时间(s)
    int ttl() default 120;

    // 缓存时间类型
    CacheType type() default CacheType.TTL;

    enum CacheType{
        TTL,        // 使用TTL缓存时间
        ALL_DAY     // 数据缓存一整天(当日日期 23:59:59 与当前时间的相差的时间)
    }
}
