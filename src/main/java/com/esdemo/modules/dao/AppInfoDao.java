package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：APP信息数据层
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Mapper
public interface AppInfoDao {

    @Select(" SELECT version, app_url, down_flag, lowest_version, ver_desc FROM mobile_ver_info WHERE " +
            " PLATFORM = #{platForm} AND APP_TYPE =#{appType} ORDER BY id DESC LIMIT 1")
    Map<String, Object> getAppVersion(@Param("platForm") String platForm, @Param("appType") String appType);
}
