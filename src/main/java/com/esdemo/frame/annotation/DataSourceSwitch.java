package com.esdemo.frame.annotation;

import com.esdemo.frame.db.DataSourceType;
import org.springframework.stereotype.Repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法上没有写这个注解的话,都是默认走读库
 * 如果有写这个注解的话,根据配置走相应的数据库
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repository
public @interface DataSourceSwitch {
    /**
     * 数据源配置
     * 默认走读库
     */
    DataSourceType value() default DataSourceType.WRITE;
}
