package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/28 11:44
 */
@Mapper
public interface PublicDataDao {

    /*
   获取新消息
    */
    @Select(
            "select nt_id from notice_info where sys_type='2' and ((receive_type='1') or (receive_type='2' and 'true'=#{one_level_id_flag})) and status='2' and find_in_set(#{oem_type},oem_type) and issued_time >=#{time_str} "
    )
    List<Map<String, Object>> getMsgList(@Param("one_level_id_flag") String one_level_id, @Param("oem_type") String oem_type, @Param("time_str") String time_str);

    /*
      获取阅读时间戳
       */
    @Select(
            "select DATE_FORMAT(last_notice_time, '%Y-%m-%d %H:%i:%S') as last_notice_time from user_entity_info where user_id=#{user_id} "
    )
    String getTimeStr(String user_id);

    @Select("select * from survey_order_info where agent_node like concat(#{agentNode},'%') and order_status = '1' " +
            "and (reply_status in ('0','1','3','4') " +
            "or (deal_status in ('2','3','6') and final_have_look_no not like concat('%',#{agentNo},'%' )))")
    @ResultType(List.class)
    List<Map<String, Object>> selectSurveyOrderInfoByOneAgent(@Param("agentNo") String agentNo, @Param("agentNode") String agentNode);

    @Select("select * from survey_order_info where agent_node = #{agentNode} and order_status = '1' " +
            "and (reply_status in ('0','3') " +
            "or (deal_status in ('2','3','6') and final_have_look_no not like concat('%',#{agentNo},'%' )))")
    @ResultType(List.class)
    List<Map<String, Object>> selectSurveyOrderInfo(@Param("agentNo") String agentNo, @Param("agentNode") String agentNode);

    @Select("select app_no,app_name,team_id,team_name,last_version,status,ifnull(apply,0) as apply,code_url from app_info where parent_id IN (SELECT id FROM  app_info  WHERE app_no=#{appNo} )")
    @ResultType(List.class)
    List<Map<String, Object>> getAppInfo(@Param("appNo") String appNo, @Param("teamId") String teamId);

    @Select("select count(1) from agent_business_product where agent_no = #{agentNo} " +
            "and bp_id = (select sys_value from sys_dict where sys_key = #{key})")
    @ResultType(Integer.class)
    Integer selectBySuperPushBpId(@Param("agentNo") String entityId, @Param("key") String key);

    @Select("SELECT protocol_ver FROM app_info WHERE app_no = #{appNo}")
    String queryProtocolVersionByAppNo(@Param("appNo") String appNo);

}
