package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 获取系统开关配置Dao
 */
@Mapper
public interface FunctionManageDao {


    /**
     * 查询代理商功能开关
     */
    @Select(
            "select * from function_manage where function_number=#{function_number} "
    )
    Map<String, Object> getFunctionManage(@Param("function_number") String function_number);


    /**
     * 是否黑名单 不包含下级
     * @param agentNo
     * @return
     */
    @Select("SELECT " +
            "    count(*)  " +
            "FROM " +
            "    function_manage fm " +
            "    JOIN agent_function_manage_blacklist afmb ON afmb.function_number = fm.function_number " +
            "    AND fm.function_switch = '1'  " +
            "    AND fm.blacklist = '1'  " +
            "    AND fm.function_number =#{function_number} " +
            "    AND afmb.agent_no = #{agentNo}  " +
            "    AND afmb.blacklist = 1  " +
            "    AND afmb.contains_lower =0")
    long countBlacklistNotContains(String agentNo, @Param("function_number") String function_number);

    /**
     * 是否黑名单 包含下级
     * @param agentNode
     * @return
     */
    @Select("SELECT " +
            "    count(*) " +
            "FROM " +
            "    function_manage fm " +
            "    JOIN agent_function_manage_blacklist afmb ON afmb.function_number = fm.function_number " +
            "    JOIN agent_info ai1 ON ai1.agent_no = afmb.agent_no " +
            "    JOIN agent_info ai2 ON ai2.agent_node LIKE CONCAT( ai1.agent_node, '%' )  " +
            "    AND fm.function_switch = '1'  " +
            "    AND fm.blacklist = '1'  " +
            "    AND fm.function_number = #{function_number}  " +
            "    AND ai2.agent_node = #{agentNode}  " +
            "    AND afmb.blacklist = 1  " +
            "    AND afmb.contains_lower =1 ")
    long countBlacklistContains(String agentNode, @Param("function_number") String function_number);




    /**
     * 获取代理商权限控制信息
     */
    @Select(
            "select id from agent_function_manage where agent_no=#{agent_no} and function_number=#{function_number} "
    )
    String getAgentFunction(@Param("agent_no") String agent_no, @Param("function_number") String function_number);


    /**
     * 获取一级是否开启给二级的绑定权限
     */
    @Select(
            "select terminal_bind_switch from agent_info where agent_no=#{agentNo} "
    )
    String getAgentInfoTerminal(@Param("agentNo") String agentNo);

}
