package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:10
 */
@Mapper
public interface TestDao {
    Map<String, Object> getAgentByAgentNo(@Param("agentNo") String agentNo);
}
