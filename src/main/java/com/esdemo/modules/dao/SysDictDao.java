package com.esdemo.modules.dao;

import com.esdemo.modules.bean.SysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据字典
 * 
 * by zouruijin
 * email rjzou@qq.com zrj@eeepay.cn
 * 2016年4月12日13:45:54
 *
 */
@Mapper
public interface SysDictDao {

	@Select("SELECT sys_value from sys_dict where sys_key=#{sysKey} limit 1")
	SysDict getByKey(@Param("sysKey") String sysKey);

	/*
	获取字典值
	 */
	@Select("SELECT sys_value AS sysValue FROM sys_dict WHERE sys_key=#{key} AND status=1 limit 1")
	Map<String, Object> getDictValue(@Param("key") String key);

	/*
	获取字典集合
	 */
	@Select("select * from sys_dict where status=1 and sys_value != 'STRING' and sys_key=#{key}")
	List<Map<String, Object>> getDictValues(@Param("key") String key);

	/*
	获取字典值对应的名称
	 */
	@Select("select sys_name from sys_dict where status=1 and sys_key=#{key} and sys_value=#{sys_value}")
	String getDictSysName(@Param("key") String key, @Param("sys_value") String sys_value);

	/*
	获取字典值对应的名称
	 */
	@Select("select sys_value from sys_dict where status=1 and sys_key=#{key} limit 1")
	String getDictSysValue(@Param("key") String key);

	@Select(" select * from sys_dict where sys_key = #{key} and sys_value = #{value} ")
	@ResultType(SysDict.class)
	SysDict sysDictByKeyAndValue(@Param("key") String key, @Param("value") String value);
}
