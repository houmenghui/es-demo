package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/29 11:26
 */
@Mapper
public interface BannerDao {
    @Select(
            "SELECT * from banner_info where app_no = #{app_no} and banner_position=4 and banner_status ='1' and  now() between online_time and offline_time ORDER BY weight "
    )
    List<Map<String,Object>> findBanner(@Param("app_no")String app_no);
}
