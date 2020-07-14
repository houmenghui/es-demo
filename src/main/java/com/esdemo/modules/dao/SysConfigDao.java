package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：系统参数数据层
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Mapper
public interface SysConfigDao {
    Map<String, Object> getSysConfigByKey(@Param("paramKey") String paramKey);

    String getSysConfigValueByKey(@Param("paramKey") String paramKey);

    String getStringValueByKey(@Param("key")String key);
}
