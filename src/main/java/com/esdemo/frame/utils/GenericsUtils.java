package com.esdemo.frame.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Title：agentApi2
 * @Description：反射工具类
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
public class GenericsUtils {
    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
     * GenricManager<Book>
     *
     * @param clazz
     * @return
     */
    public static Class getSuperClassGenricType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager<Book>
     *
     * @param clazz
     * @param index
     */
    public static Class getSuperClassGenricType(Class clazz, int index)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
}