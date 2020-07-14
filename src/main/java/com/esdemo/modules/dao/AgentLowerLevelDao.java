package com.esdemo.modules.dao;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.bean.Vo.*;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 代理商开设下级
 */
@Mapper
public interface AgentLowerLevelDao {

    @SelectProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "getAgentLowerLevelAllList")
    @ResultType(AgentLowerLevelInfoVo.class)
    List<AgentLowerLevelInfoVo> getAgentLowerLevelAllList(@Param("userInfoBean")UserInfoBean userInfoBean,@Param("queryInfo") AgentLowerLevelFilter queryInfo);

    @Select(
            "select agent.id,agent.agent_no,agent.agent_node,agent.agent_name,agent.agent_level,agent.parent_id," +
                    " agent.mobilephone,agent.status,agent.create_date,agent.one_level_id " +
                    " from agent_info agent" +
                    " where agent_no=#{agentNo}"
    )
    AgentLowerLevelInfoVo getAgentLowerLevelDetail(@Param("agentNo")String agentNo);

    @Select(
            "select agent.id,agent.agent_no,agent.agent_node,agent.agent_name,agent.agent_level,agent.parent_id," +
                    " agent.account_name,agent.id_card_no,agent.one_level_id " +
                    " from agent_info agent" +
                    " where agent_no=#{agentNo}"
    )
    AgentCardInfoVo getBindingSettlementCardBeforeData(@Param("agentNo")String agentNo);

    @Select(
            "select  bpd.bp_id,bpd.agent_show_name,bpd.team_id,bpd.allow_individual_apply,bpd.effective_status, " +
                    " abp.agent_no,bpg.group_no,team.team_name " +
                    " from agent_business_product abp " +
                    "  LEFT JOIN business_product_define bpd ON bpd.bp_id=abp.bp_id " +
                    "  LEFT JOIN business_product_group bpg ON bpg.bp_id = bpd.bp_id " +
                    "   LEFT JOIN team_info team ON team.team_id=bpd.team_id " +
                    " where abp.agent_no=#{agentNo} " +
                    "   and abp.status='1' and bpd.effective_status='1' " +
                    " ORDER BY bpd.team_id, bpg.group_no DESC,bpd.allow_individual_apply DESC "
    )
    @ResultType(AgentBpIdInfoVo.class)
    List<AgentBpIdInfoVo> getAgentBpIdByOrder(@Param("agentNo")String agentNo);

    @SelectProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "getAgentBpId")
    @ResultType(AgentBpIdInfo.class)
    List<AgentBpIdInfo> getAgentBpId(@Param("agentNo")String agentNo);

    /**
     * 获取代理商服务 最大服务条数的，服务数据列表
     * @param bpId
     * @param agentNo
     * @param oneAgentNo
     * @return
     */
    @Select(
            "select   bpi.bp_id, " +
                    " asr.service_id,asr.card_type,asr.holidays_mark, " +
                    " asr.profit_type,asr.cost_rate_type,asr.share_profit_percent,asr.per_fix_cost,asr.cost_rate, " +
                    " IF(si.service_type='10001' or si.service_type='10000',1,0) AS cashOutStatus, " +
                    " si.service_name,si.link_service,si.service_type,si.is_price_update," +
                    " CONCAT(si.service_type,IFNULL(si2.service_type,'t')) AS serviceType2  " +
                    " from business_product_info bpi " +
                    " JOIN service_manage_rate smr ON (smr.service_id= bpi.service_id and smr.agent_no=#{oneAgentNo}) " +
                    " LEFT JOIN agent_share_rule asr ON (asr.service_id=smr.service_id and asr.card_type=smr.card_type and asr.holidays_mark=smr.holidays_mark )" +
                    " LEFT JOIN service_info si ON bpi.service_id = si.service_id " +
                    " LEFT JOIN service_info si2 ON si2.link_service = si.service_id " +
                    " where bpi.bp_id=#{bpId} and asr.agent_no=#{agentNo} "
    )
    @ResultType(AgentShareRuleInfo.class)
    List<AgentShareRuleInfo> getAgentShare(@Param("bpId")Long bpId,@Param("agentNo") String agentNo,@Param("oneAgentNo")String oneAgentNo);

    /**
     * 获取 代理商某个业务产品下所有配置的服务数据
     * @param bpId
     * @param agentNo
     * @return
     */
    @Select(
            "select   bpi.bp_id, " +
                    " asr.service_id,asr.card_type,asr.holidays_mark, " +
                    " asr.profit_type,asr.cost_rate_type,asr.share_profit_percent,asr.per_fix_cost,asr.cost_rate, " +
                    " IF(info.service_type='10001' or info.service_type='10000',1,0) AS cashOutStatus, " +
                    " info.service_name,info.link_service,info.service_type,info.is_price_update " +
                    " from business_product_info bpi " +
                    "  JOIN agent_share_rule asr ON asr.service_id=bpi.service_id" +
                    " LEFT JOIN service_info info ON bpi.service_id = info.service_id " +
                    " where bpi.bp_id=#{bpId} and asr.agent_no=#{agentNo} "
    )
    @ResultType(AgentShareRuleInfo.class)
    List<AgentShareRuleInfo> getShareByBpId(@Param("bpId")String bpId,@Param("agentNo") String agentNo);


    @Select(
            " select service_type from service_info where link_service=#{serviceId} "
    )
    String getLinkServiceType(@Param("serviceId")String serviceId);

    /**
     * 活动子类型目前无组织，返回固定值
     * @param agentNo
     * @param subType
     * @return
     */
    @Select(
            "select DISTINCT acti.*,aht.activity_type_name,aht.trans_amount,'110010' AS teamId, '安收宝' AS teamName,acg.id AS group_no " +
                    " from agent_activity acti " +
                    "  JOIN activity_hardware_type aht ON aht.activity_type_no=acti.activity_type_no" +
                    "  LEFT JOIN (select hg.*,hgd.activity_type_no from hlf_group hg " +
                    "                INNER JOIN hlf_group_detail hgd ON hgd.group_id=hg.id " +
                    "             where hg.group_type='1' " +
                    "  ) acg ON acg.activity_type_no=aht.activity_type_no " +
                    " where acti.agent_no=#{agentNo} and acti.sub_type=#{subType} " +
                    " ORDER BY acg.id DESC,acti.activity_type_no ASC "

    )
    @ResultType(AgentActivityVo.class)
    List<AgentActivityVo> getAgentActivity(@Param("agentNo")String agentNo,@Param("subType") String subType);


    @Select(
            "select * from agent_activity where agent_no=#{agentNo} and activity_type_no=#{activityTypeNo}"
    )
    @ResultType(AgentActivity.class)
    AgentActivity getAgentActivityInfo(@Param("agentNo")String agentNo,@Param("activityTypeNo") String activityTypeNo);

    @Update(
            "update agent_info set " +
                    " account_name=#{info.accountName},id_card_no=#{info.idCardNo},account_type=#{info.accountType}," +
                    " account_no=#{info.accountNo},bank_name=#{info.bankName},account_province=#{info.accountProvince}," +
                    " account_city=#{info.accountCity},sub_bank=#{info.subBank},cnaps_no=#{info.cnapsNo} " +
                    " where agent_no=#{info.agentNo} and (account_no is null or account_no='') "
    )
    int setBindingSettlementCard(@Param("info")AccountCardUp accountCard);

    @Select(
            "select * from agent_info where agent_name=#{agentName} and team_id=#{teamId} limit 1 "
    )
    @ResultType(AgentLowerLevelInfoVo.class)
    AgentLowerLevelInfoVo checkAgentName(@Param("agentName")String agentName, @Param("teamId")String teamId);

    @Select(
            "select * from agent_info where mobilephone=#{mobilephone} and team_id=#{teamId} limit 1 "
    )
    @ResultType(AgentLowerLevelInfoVo.class)
    AgentLowerLevelInfoVo checkAgentPhone(@Param("mobilephone")String mobilephone,@Param("teamId") String teamId);

    @Select(
            "select user_id from user_info where email=#{email} and team_id=#{teamId} limit 1 "
    )
    @ResultType(String.class)
    String checkUserInfoEmail(@Param("email")String email,@Param("teamId") String teamId);


    @SelectProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "getServiceManageRateList")
    @ResultType(ServiceManageRateVo.class)
    List<ServiceManageRateVo> getServiceManageRateList(@Param("bpIds")String bpIds, @Param("oneAgentNo") String oneAgentNo,
                                                     @Param("shareParent")AgentShareRuleInfo shareParent, @Param("serviceId") String serviceId);


    @Insert(
            " INSERT INTO agent_business_product (agent_no,bp_id,status,default_bp_flag) " +
                    " VALUES (#{agentNo},#{info.bpId},'1',#{info.allowIndividualApply})"
    )
    int addAgentBusinessProduct(@Param("info")AgentBpIdInfoVo itmeVo,@Param("agentNo") String agentNo);

    /**
     *
     * @param bpId
     * @param serviceType 组合serviceType
     * @param cardType
     * @param holidaysMark
     * @param oneAgentNo
     * @return
     */
    @Select(
            "select bpi.service_id " +
                    " from business_product_info bpi " +
                    "  JOIN service_manage_rate smr ON (smr.service_id= bpi.service_id and smr.agent_no=#{oneAgentNo})  " +
                    "  LEFT JOIN service_info si ON bpi.service_id = si.service_id " +
                    "  LEFT JOIN service_info si2 ON si2.link_service = si.service_id " +
                    " where bpi.bp_id=#{bpId} and CONCAT(si.service_type,IFNULL(si2.service_type,'t'))=#{serviceType} " +
                    "  and smr.card_type=#{cardType} and smr.holidays_mark=#{holidaysMark}"
    )
    String findGroupServiceId(@Param("bpId")Long bpId, @Param("serviceType")String serviceType,
                            @Param("cardType") String cardType, @Param("holidaysMark")String holidaysMark,
                            @Param("oneAgentNo") String oneAgentNo);


    @Select(
            "select * from activity_hardware_type where activity_type_no=#{activityTypeNo}"
    )
    @ResultType(ActivityTypeVo.class)
    ActivityTypeVo getActivityTypeVo(@Param("activityTypeNo")String activityTypeNo);

    /**
     * 查询代理商某组的队长BPid
     * @param agentNo
     * @param groupNo
     */
    @Select(
            "select abp.bp_id " +
                    " from agent_business_product abp " +
                    "  LEFT JOIN business_product_define bpd ON bpd.bp_id=abp.bp_id " +
                    "  LEFT JOIN business_product_group bpg ON bpg.bp_id = bpd.bp_id " +
                    " where abp.agent_no=#{agentNo} and bpg.group_no=#{groupNo} " +
                    "   and abp.status='1' and bpd.effective_status='1' and bpd.allow_individual_apply='1' "
    )
    String getGroupBpId(@Param("agentNo")String agentNo,@Param("groupNo") String groupNo);

    @Select(
            "select abp.bp_id " +
                    " from agent_business_product abp " +
                    "  LEFT JOIN business_product_define bpd ON bpd.bp_id=abp.bp_id " +
                    "  LEFT JOIN business_product_group bpg ON bpg.bp_id = bpd.bp_id " +
                    " where abp.agent_no=#{agentNo} and bpg.group_no=#{groupNo} " +
                    "   and abp.status='1' and bpd.effective_status='1' "
    )
    List<String> getGroupBpIdList(@Param("agentNo")String agentNo,@Param("groupNo") String groupNo);

    @Select(
            "select * from agent_business_product where agent_no=#{agentNo} and bp_id=#{bpId}"
    )
    String getAgentBpIdByAgentNo(@Param("agentNo")String agentNo,@Param("bpId") String bpId);

    @Select(
            "select id from agent_share_rule where " +
                    " agent_no=#{agentNo} and service_id=#{info.serviceId} and card_type=#{info.cardType} and holidays_mark=#{info.holidaysMark}"
    )
    String getAgentShareRuleByServiceId(@Param("agentNo")String agentNo, @Param("info")AgentShareRuleInfo shareParent);

    @Select(
            "select id from agent_share_rule where " +
                    " agent_no=#{agentNo} and service_id=#{serviceIdBp} and card_type=#{info.cardType} and holidays_mark=#{info.holidaysMark}"
    )
    String getAgentShareRuleByServiceIdBp(@Param("agentNo")String agentNo,@Param("serviceIdBp")String serviceIdBp, @Param("info")AgentShareRuleInfo shareParent);

    @Select(
            "select * from agent_share_rule where agent_no=#{agentNo} " +
                    " and service_id=#{info.serviceId} and card_type=#{info.cardType} and holidays_mark=#{info.holidaysMark} "
    )
    AgentShareRuleInfo getAgentShareRuleInfoByServiceId(@Param("agentNo")String agentNo, @Param("info")AgentShareRuleInfo shareParent);


    @InsertProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "insertAgentShareRuleDetail")
    @Options(useGeneratedKeys = true, keyProperty = "share.shareId")
    int insertAgentShareRuleDetail(@Param("shareParent")AgentShareRuleInfo shareParent,@Param("share") AgentShareRuleInfo share,
                          @Param("agentNo") String agentNo,@Param("serviceId") String serviceId);

    @InsertProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "insertAgentShareRuleTask")
    int insertAgentShareRuleTask(@Param("shareParent")AgentShareRuleInfo shareParent, @Param("share") AgentShareRuleInfo share,
                                   @Param("shareId") String shareId, @Param("efficientDate") Date efficientDate);

    /**
     * 删除分润定时表的相同shareId,相同生效日期的数据
     * @param shareId
     * @param efficientDate
     * @return
     */
    @Delete(
            "delete from agent_share_rule_task where share_id=#{shareId} and efficient_date=#{efficientDate} "
    )
    int deleteAgentShareRuleTask(@Param("shareId") String shareId, @Param("efficientDate") Date efficientDate);

    @InsertProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "addAgentActivity")
    int addAgentActivity(@Param("info")AgentActivityVo info, @Param("infoParent")AgentActivity infoParent,
                          @Param("agentNo") String agentNo);

    @UpdateProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "updateAgentActivity")
    int updateAgentActivity(@Param("info")AgentActivityVo info,@Param("infoParent") AgentActivity infoParent,@Param("agentNo") String agentNo);

    @UpdateProvider(type = AgentLowerLevelDao.SqlProvider.class,method = "updateAgentActivityLower")
    int updateAgentActivityLower(@Param("info")AgentActivityVo info,@Param("infoParent") AgentActivity infoParent,@Param("agentNo") String agentNo);

    @Select(
            "select  asrt.id " +
                    " from business_product_info bpi " +
                    "  JOIN agent_share_rule asr ON asr.service_id=bpi.service_id" +
                    "  JOIN agent_share_rule_task asrt ON asrt.share_id=asr.id " +
                    " where bpi.bp_id=#{bpId} and asr.agent_no=#{agentNo} " +
                    "       and asrt.efficient_date>now() and asrt.effective_status='0'" +
                    " LIMIT 1"
    )
    Integer getShareTaskCheck(@Param("bpId")String bpId, @Param("agentNo")String agentNo);


    class SqlProvider{
        public String insertAgentShareRuleTask(Map<String,Object> params) {
            StringBuffer sb=new StringBuffer();
            sb.append(" INSERT INTO agent_share_rule_task ( ");
            sb.append("   share_id,efficient_date, ");
            sb.append("   profit_type,share_profit_percent,cost_rate_type," +
                    "     per_fix_cost,cost_rate,check_status ");
            sb.append(" ) ");
            sb.append(" VALUES (");
            sb.append("   #{shareId},#{efficientDate},");
            sb.append("   #{shareParent.profitType},#{share.shareProfitPercent},#{shareParent.costRateType}, ");
            sb.append("   #{share.perFixCost},#{share.costRate},'1' ");
            sb.append(" ) ");
            return sb.toString();
        }

        public String insertAgentShareRuleDetail(Map<String,Object> params) {
            String serviceId = (String) params.get("serviceId");
            StringBuffer sb=new StringBuffer();
            sb.append(" INSERT INTO agent_share_rule ( ");
            sb.append("   agent_no,service_id,card_type,holidays_mark,efficient_date, ");
            sb.append("   profit_type,share_profit_percent,cost_rate_type," +
                    "     per_fix_cost,cost_rate,check_status,lock_status ");
            sb.append(" ) ");
            sb.append(" VALUES (");
            sb.append("   #{agentNo},");
            if(StringUtils.isNotBlank(serviceId)){
                sb.append("   #{serviceId},");
            }else{
                sb.append("   #{shareParent.serviceId},");
            }
            sb.append("   #{shareParent.cardType},#{shareParent.holidaysMark},now(),");
            sb.append("   #{shareParent.profitType},#{share.shareProfitPercent},#{shareParent.costRateType}, ");
            sb.append("   #{share.perFixCost},#{share.costRate}, ");
            sb.append("   '1','0' ");
            sb.append(" ) ");
            return sb.toString();
        }

        public String updateAgentActivityLower(Map<String,Object> params) {
            AgentActivityVo info = (AgentActivityVo) params.get("info");
            AgentActivity infoParent = (AgentActivity) params.get("infoParent");
            String agentNo = (String) params.get("agentNo");
            StringBuffer sb=new StringBuffer();
            sb.append(" update agent_activity set  ");
            if("1".equals(infoParent.getSubType())){
                if(info.getFullPrizeAmount()!=null&&info.getFullPrizeAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   full_prize_amount=0, ");
                }
                if(info.getNotFullDeductAmount()!=null&&info.getNotFullDeductAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   not_full_deduct_amount=0, ");
                }
            }else if("2".equals(infoParent.getSubType())){
                if(info.getOneRewardAmount()!=null&&info.getOneRewardAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   one_reward_amount=0, ");
                }
                if(info.getTwoRewardAmount()!=null&&info.getTwoRewardAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   two_reward_amount=0, ");
                }
                if(info.getThreeRewardAmount()!=null&&info.getThreeRewardAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   three_reward_amount=0, ");
                }
                if(info.getFourRewardAmount()!=null&&info.getFourRewardAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   four_reward_amount=0, ");
                }
                if(info.getDeductionAmount()!=null&&info.getDeductionAmount().compareTo(BigDecimal.ZERO)==0){
                    sb.append("   deduction_amount=0, ");
                }
            }
            String str=sb.toString().substring(0, sb.lastIndexOf(","));
            StringBuffer restr=new StringBuffer(str);
            restr.append("  where  activity_type_no=#{infoParent.activityTypeNo} and agent_no!=#{agentNo} ");
            restr.append("    and  agent_node like concat(#{infoParent.agentNode},#{agentNo},'-%') ");
            return restr.toString();
        }
        public String updateAgentActivity(Map<String,Object> params) {
            AgentActivityVo info = (AgentActivityVo) params.get("info");
            AgentActivity infoParent = (AgentActivity) params.get("infoParent");
            String agentNo = (String) params.get("agentNo");
            StringBuffer sb=new StringBuffer();
            sb.append(" update agent_activity set  ");
            sb.append("   cash_back_amount=#{info.cashBackAmount},tax_rate=#{info.taxRate},");
            if("1".equals(infoParent.getSubType())){
                sb.append("   full_prize_amount=#{info.fullPrizeAmount},not_full_deduct_amount=#{info.notFullDeductAmount}, ");
            }else if("2".equals(infoParent.getSubType())){
                sb.append("   one_reward_amount=#{info.oneRewardAmount},two_reward_amount=#{info.twoRewardAmount}, ");
                sb.append("   three_reward_amount=#{info.threeRewardAmount},four_reward_amount=#{info.fourRewardAmount}, ");
                sb.append("   deduction_amount= #{info.deductionAmount},      ");
            }
            sb.append("   reward_rate=#{info.rewardRate}      ");
            sb.append("  where  activity_type_no=#{infoParent.activityTypeNo} and agent_no=#{agentNo} ");
            return sb.toString();
        }
        public String addAgentActivity(Map<String,Object> params) {
            AgentActivityVo info = (AgentActivityVo) params.get("info");
            AgentActivity infoParent = (AgentActivity) params.get("infoParent");
            String agentNo = (String) params.get("agentNo");
            StringBuffer sb=new StringBuffer();
            sb.append(" INSERT INTO  agent_activity ( ");
            sb.append("     activity_type_no,agent_no,agent_node, ");
            sb.append("     cash_back_amount,tax_rate,create_time,sub_type, ");
            if("1".equals(infoParent.getSubType())){
                sb.append(" full_prize_amount,not_full_deduct_amount, ");
            }else if("2".equals(infoParent.getSubType())){
                sb.append("     one_reward_amount,two_reward_amount,three_reward_amount,four_reward_amount, ");
                sb.append("     deduction_amount, ");
            }
            sb.append("     reward_rate, ");
            sb.append("     repeat_register_amount,repeat_register_ratio,repeat_full_prize_amount, ");
            sb.append("     repeat_not_full_deduct_amount, ");
            sb.append("     one_repeat_reward_amount,two_repeat_reward_amount,three_repeat_reward_amount,four_repeat_reward_amount, ");
            sb.append("     repeat_deduction_amount ");
            sb.append(" )");
            sb.append(" VALUES ( ");
            sb.append("     #{infoParent.activityTypeNo},#{agentNo},concat(#{infoParent.agentNode},#{agentNo},'-'), ");
            sb.append("     #{info.cashBackAmount},#{info.taxRate},now(),#{infoParent.subType},");
            if("1".equals(infoParent.getSubType())){
                sb.append(" #{info.fullPrizeAmount},#{info.notFullDeductAmount},");
            }else if("2".equals(infoParent.getSubType())){
                sb.append(" #{info.oneRewardAmount},#{info.twoRewardAmount},#{info.threeRewardAmount},#{info.fourRewardAmount}, ");
                sb.append(" #{info.deductionAmount},");
            }
            sb.append("     #{info.rewardRate},");
            //重复的都复制上级的
            sb.append("     #{infoParent.repeatRegisterAmount},#{infoParent.repeatRegisterRatio},#{infoParent.repeatFullPrizeAmount}, ");
            sb.append("     #{infoParent.repeatNotFullDeductAmount}, ");
            sb.append("     #{infoParent.oneRepeatRewardAmount},#{infoParent.twoRepeatRewardAmount},#{infoParent.threeRepeatRewardAmount},#{infoParent.fourRepeatRewardAmount}, ");
            sb.append("     #{infoParent.repeatDeductionAmount} ");
            sb.append(" )");
            return sb.toString();
        }
        public String getServiceManageRateList(Map<String,Object> params) {
            String bpIds = (String) params.get("bpIds");
            String oneAgentNo = (String) params.get("oneAgentNo");
            AgentShareRuleInfo shareParent = (AgentShareRuleInfo) params.get("shareParent");
            String serviceId = (String) params.get("serviceId");
            StringBuffer sb=new StringBuffer();
            sb.append(" select ");
            sb.append("    bpi.bp_id,info.service_type,smr.service_id,smr.card_type,smr.holidays_mark,smr.rate_type,  ");
            sb.append("    smr.single_num_amount,smr.rate  ");
            sb.append("  from service_manage_rate smr ");
            sb.append("   JOIN service_info info ON info.service_id = smr.service_id ");
            sb.append("   LEFT JOIN service_info si2 ON si2.link_service = info.service_id ");
            sb.append("   JOIN business_product_info bpi ON bpi.service_id=smr.service_id ");
            sb.append(" where 1=1  ");
            if(StringUtils.isNotBlank(oneAgentNo)){
                sb.append("  and smr.agent_no =#{oneAgentNo} ");
            }
            if(StringUtils.isNotBlank(shareParent.getCardType())){
                sb.append("  and smr.card_type =#{shareParent.cardType} ");
            }
            if(StringUtils.isNotBlank(shareParent.getHolidaysMark())){
                sb.append("  and smr.holidays_mark =#{shareParent.holidaysMark} ");
            }
            if(StringUtils.isNotBlank(shareParent.getServiceType2())){
                sb.append("   and CONCAT(info.service_type,IFNULL(si2.service_type,'t')) =#{shareParent.serviceType2} ");
            }
            if(StringUtils.isNotBlank(bpIds)){
                sb.append("   and bpi.bp_id in ("+bpIds+") ");
            }
            if(StringUtils.isNotBlank(serviceId)){
                sb.append("  and smr.service_id =#{serviceId} ");
            }
            return sb.toString();
        }
        public String getAgentBpId(Map<String,Object> params) {
            String agentNo = (String) params.get("agentNo");
            StringBuffer sb=new StringBuffer();
            sb.append(" select ");
            sb.append("    bpd.bp_id,bpd.agent_show_name,bpd.team_id,bpd.allow_individual_apply,bpd.effective_status,  ");
            sb.append("    abp.agent_no,bpg.group_no ");
            sb.append("  from agent_business_product abp ");
            sb.append("   LEFT JOIN business_product_define bpd ON bpd.bp_id=abp.bp_id ");
            sb.append("   LEFT JOIN business_product_group bpg ON bpg.bp_id = bpd.bp_id ");
            sb.append(" where abp.agent_no=#{agentNo}  ");
            sb.append("     and abp.status='1' and bpd.effective_status='1' ");
            return sb.toString();
        }

        public String getAgentLowerLevelAllList(Map<String,Object> params) {
            UserInfoBean userInfoBean = (UserInfoBean) params.get("userInfoBean");
            AgentLowerLevelFilter queryInfo = (AgentLowerLevelFilter) params.get("queryInfo");
            StringBuffer sb=new StringBuffer();
            sb.append(" select ");
            sb.append(" agent.id,agent.agent_no,agent.agent_node,agent.agent_name,agent.agent_level,agent.parent_id,");
            if (queryInfo!=null&&StringUtils.isNotBlank(queryInfo.getShareRuleInit())) {
                sb.append("  CASE WHEN account_no is null and share_rule_init='1' THEN 1 " +
                        "         WHEN account_no is not null and share_rule_init='1' THEN 2 " +
                        "    END toBeSetStatus, ");
            }
            sb.append(" agent.mobilephone,agent.status,agent.create_date ");
            sb.append(" from agent_info agent ");
            sb.append(" where 1=1");
            if(queryInfo==null){//什么条件都不传
                sb.append(" and agent.agent_node like concat(#{userInfoBean.agentNode},'%')");
                return sb.toString();
            }
            if (StringUtils.isNotBlank(queryInfo.getAgentNo())) {
                if(queryInfo.getLowerStatus()!=null){
                    if(1==queryInfo.getLowerStatus().intValue()){//是，全链条
                        sb.append(" and agent.agent_node like concat((select agent_node from agent_info where agent_no=#{queryInfo.agentNo}),'%') ");
                    }else if(2==queryInfo.getLowerStatus().intValue()){//否，本级
                        sb.append(" and agent.agent_no = #{queryInfo.agentNo}");
                    }else if(3==queryInfo.getLowerStatus().intValue()){//仅下级
                        sb.append(" and agent.parent_id = #{queryInfo.agentNo}");
                    }
                }else{
                    sb.append(" and agent.agent_node like concat((select agent_node from agent_info where agent_no=#{queryInfo.agentNo}),'%') ");
                }
            }else{
                //当前登入代理商需要判断开关
                sb.append(" and agent.agent_node like concat(#{userInfoBean.agentNode},'%')");
            }
            if (queryInfo.getCreateDateBegin()!=null) {
                sb.append(" and agent.create_date >= #{queryInfo.createDateBegin}");
            }
            if (queryInfo.getCreateDateEnd()!=null) {
                sb.append(" and agent.create_date <= #{queryInfo.createDateEnd}");
            }
            if (StringUtils.isNotBlank(queryInfo.getShareRuleInit())) {
                sb.append(" and agent.share_rule_init = #{queryInfo.shareRuleInit}");
            }
            if (StringUtils.isNotBlank(queryInfo.getRegistType())) {
                sb.append(" and agent.regist_type = #{queryInfo.registType}");
            }
            sb.append(" order by agent.create_date desc");
            return sb.toString();
        }

    }
}
