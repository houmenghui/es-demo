package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.*;

import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：短信数据层
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Mapper
public interface SmsDao {

    /**
     * 查询最近5分钟的一条验证码
     *
     * @param mobileNo
     * @param teamId
     * @return
     */
    @Select("SELECT * FROM agent_valid WHERE UNIX_TIMESTAMP(create_time)>UNIX_TIMESTAMP(NOW())-5*60 AND mobile_no=#{mobileNo} AND team_id=#{teamId} ORDER BY create_time DESC LIMIT 1")
    @ResultType(Map.class)
    Map<String, Object> getLatest5MinuteSmsCode(@Param("mobileNo") String mobileNo, @Param("teamId") String teamId);

    /**
     * 保存验证码记录
     *
     * @param mobileNo
     * @param validCode
     * @param teamId
     * @return
     */
    @Insert("INSERT INTO agent_valid(mobile_no, valid_code, create_time, team_id, status) VALUES(#{mobileNo}, #{validCode}, now(), #{teamId}, '1')")
    int insertSmsCode(@Param("mobileNo") String mobileNo, @Param("validCode") String validCode, @Param("teamId") String teamId);

    /**
     * 更新验证码状态
     *
     * @param Id
     * @return
     */
    @Update("UPDATE agent_valid SET status = #{newStatus} WHERE id = #{Id}")
    int updateSmsStatus(@Param("Id") String Id, @Param("newStatus") String newStatus);
}
