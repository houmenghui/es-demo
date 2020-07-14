package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/21 11:41
 */
@Mapper
public interface NoticeDao {
    /*
    获取OemType,one_level_id
    */
    @Select(
            "select aoi.oem_type,ai.one_level_id from agent_info ai LEFT JOIN agent_oem_info aoi on aoi.one_agent_no=ai.one_level_id where ai.agent_no=#{agentNo} "
    )
    Map<String, Object> getOemInfo(@Param("agentNo") String agentNo);

    /*
    获取首页消息
    */
    @Select(
            "select * from notice_info where sys_type='2' and ((receive_type='1') or (receive_type='2' and 'true'=#{one_level_id_flag})) and status='2' and find_in_set(#{oem_type},oem_type) order by strong desc,issued_time desc LIMIT 4 "
    )
    List<Map<String, Object>> getHomeMsg(@Param("one_level_id_flag") String one_level_id,@Param("oem_type") String oem_type);

    /*
    获取消息详情
     */
    @Select(
            "select * from notice_info where nt_id=#{nt_id} "
    )
    Map<String, Object> getMsgDetail(@Param("nt_id") String nt_id);


    /*
    获取消息列表
   */
    @Select(
            "select * from notice_info where sys_type='2' and ((receive_type='1') or (receive_type='2' and 'true'=#{one_level_id_flag})) and status='2' and find_in_set(#{oem_type},oem_type) order by strong desc,issued_time desc"
    )
    List<Map<String, Object>> getMsgList(@Param("one_level_id_flag") String one_level_id_flag,@Param("oem_type") String oem_type);


    /*
    获取弹窗消息
    */
    @Select(
            "select * from notice_info where sys_type='2' and ((receive_type='1') or (receive_type='2' and 'true'=#{one_level_id_flag})) and status='2' and find_in_set(#{oem_type},oem_type) and  now() between valid_begin_time and valid_end_time and show_status<>0 order by strong desc,issued_time desc  "
    )
    List<Map<String, Object>> getPopupMsg(@Param("one_level_id_flag") String one_level_id,@Param("oem_type") String oem_type);


    /*
    获取昨天收入
    */
    @Select(
            "SELECT IFNULL(SUM(tab.cnt_amount),0.00) from (" +
                    " SELECT hpb.total_money cnt_amount from agent_dayhpb_share_collect hpb where hpb.collec_time BETWEEN #{start_time} and #{end_time} and agent_no=#{agent_no}" +
                    " UNION ALL" +
                    " SELECT settle.total_money cnt_amount from agent_daysettle_share_collect settle where settle.collec_time BETWEEN #{start_time} and #{end_time} and agent_no=#{agent_no}" +
                    " UNION ALL" +
                    " SELECT trans.total_money cnt_amount from agent_daytrans_share_collect trans where trans.collec_time BETWEEN #{start_time} and #{end_time} and agent_no=#{agent_no}" +
                    " ) tab"
    )
    String getYesterdayIncome(@Param("agent_no") String agent_no, @Param("start_time") String start_time, @Param("end_time") String end_time);

    /**
     * 根据用户ID更新消息阅读时间
     *
     * @param user_id
     * @returnv
     */
    @Update("update user_entity_info set last_notice_time = now() where user_id = #{user_id} and apply ='1' ")
    int updatelastTime(@Param("user_id") String user_id);

}
