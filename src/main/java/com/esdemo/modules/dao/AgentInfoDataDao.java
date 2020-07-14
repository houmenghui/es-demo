package com.esdemo.modules.dao;

import com.esdemo.modules.bean.*;
import com.esdemo.modules.bean.Vo.AccountCardUp;
import com.esdemo.modules.bean.Vo.AgentLowerLevelUp;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AgentInfoDataDao {

    @Select(
            "select * from agent_info where agent_no=#{agentNo} "
    )
    AgentInfoData getAgentInfoData(@Param("agentNo")String agentNo);

    @Insert(
            "INSERT INTO agent_info " +
                    " (agent_no,agent_node,agent_name,agent_level," +
                    "  parent_id,one_level_id,is_oem,team_id," +
                    "  email,mobilephone,address,account_name,account_type," +
                    "  account_no,bank_name,cnaps_no,sale_name,creator," +
                    "  status,create_date,is_approve,count_level,province,city,area," +
                    "  sub_bank,account_province,account_city,agent_oem,agent_type," +
                    "  agent_share_level,id_card_no" +
                    " )" +
                    " VALUES ( " +
                    " #{agentNo},concat(#{agentParent.agentNode},#{agentNo},'-'),#{info.agentName},(#{agentParent.agentLevel}+1), " +
                    "  #{agentParent.agentNo},#{agentParent.oneLevelId},#{agentParent.isOem},#{agentParent.teamId}, " +
                    "  #{info.email},#{info.mobilephone},#{info.address},#{card.accountName},#{card.accountType}, " +
                    "  #{card.accountNo},#{card.bankName},#{card.cnapsNo},#{info.saleName},#{userInfoBean.userId}, " +
                    "  '1',now(),#{agentParent.isApprove},#{agentParent.countLevel},#{info.province},#{info.city},#{info.area}, " +
                    "  #{card.subBank},#{card.accountProvince},#{card.accountCity},#{agentParent.agentOem},#{agentParent.agentType}, " +
                    "  #{agentParent.agentShareLevel},#{card.idCardNo} " +
                    ") "
    )
    int addAgentLowerLevel(@Param("userInfoBean") UserInfoBean userInfoBean, @Param("agentParent")AgentInfoData agentParent,
                           @Param("info") AgentLowerLevelUp info, @Param("agentNo") String agentNo, @Param("card") AccountCardUp accountCard);

    @Select("select * from  user_info where mobilephone=#{mobilephone} and team_id=#{teamId}")
    @ResultType(AgentUserInfo.class)
    AgentUserInfo selectAgentUser(@Param("mobilephone")String mobilephone,@Param("teamId") String teamId);

    //新增代理商的用户
    @Insert("insert into user_info(user_id,user_name,mobilephone,status,password,team_id,create_time,email) values(#{agent.userId},#{agent.userName},"
            + "#{agent.mobilephone},1,#{agent.password},#{agent.teamId},now(),#{agent.email})")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "agent.id", before = false, resultType = Long.class)
    int insertAgentUser(@Param("agent") AgentUserInfo agent);

    //新增代理商的结构组织
    @Insert("insert into user_entity_info(user_id,user_type,entity_id,apply,manage,status,last_notice_time,is_agent) values(#{agent.userId},1,#{agent.entityId},1,1,1,now(),#{agent.isAgent})")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "agent.id", before = false, resultType = Long.class)
    int insertAgentEntity(@Param("agent") AgentUserEntity agent);

    @Select("select * from  user_entity_info where user_id=#{userId} and entity_id=#{agentNo}")
    @ResultType(AgentUserEntity.class)
    AgentUserEntity selectAgentUserEntity(@Param("userId") String userId, @Param("agentNo") String agentNo);

    @Update("update agent_info set has_account=#{status} where agent_no=#{agentNo}")
    int updateAgentAccount(@Param("agentNo") String agentNo, @Param("status") int status);

    @Update(
            "update agent_info set share_rule_init=#{shareRuleInit} where agent_no=#{agentNo} "
    )
    int setAgentToBeSetIgnore(@Param("agentNo")String agentNo,@Param("shareRuleInit")String shareRuleInit);

    @Select(
            "select * from sys_dict where sys_key like 'agent_oem_prize_buckle_rank%' "
    )
    List<Map<String,Object>> getAgentOemPrizeBuckleRank();

    class SqlProvider {

    }

}
