package com.esdemo.modules.service;

import java.util.Map;
import java.util.function.Function;

/**
 * @Title：agentApi2
 * @Description：系统参数业务层
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface SysConfigService {

    /**
     * 根据参数名获取系统参数对象
     *
     * @param paramKey
     * @return
     */
    Map<String, Object> getSysConfigByKey(String paramKey);

    /**
     * 根据参数名获取对应值
     *
     * @param paramKey
     * @return
     */
    String getSysConfigValueByKey(String paramKey);

    /**
     * 根据参数名获取对应值
     *
     * @param paramKey     参数值
     * @param defaultValue 如果找不到,取默认值
     * @param function     讲字符串转化成想要的格式
     */
    <T> T getSysConfigValueByKey(String paramKey, T defaultValue, Function<String, T> function);
}
