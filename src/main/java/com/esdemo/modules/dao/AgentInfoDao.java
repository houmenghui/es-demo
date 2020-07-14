package com.esdemo.modules.dao;

import com.esdemo.modules.bean.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.data.domain.PageRequest;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description
 * @date 2019/5/20
 */
@Mapper
public interface AgentInfoDao {

    /**
     * 查询代理商详情
     *
     * @param agentNO
     * @return
     */
    AgentInfo getAgentInfoByNo(@Param("agentNO") String agentNO);

    AgentInfo getAgentInfoByName(@Param("agentName") String agentName);

    MerchantInfo getMerchantByNo(@Param("merchantNo") String merchantNo);

    MerchantInfo getMerchantByName(@Param("merchantName") String merchantName);

    AgentInfo selectByAgentNo(@Param("agentNo") String agentNo);

    int existAgentByMobilephoneAndTeamId(@Param("agent") AgentInfo agent);

    AgentUserInfo selectAgentUser(@Param("mobilephone") String mobilephone, @Param("teamId") String teamId);

    String selectPoster(@Param("teamId") String teamId);

    Map<String, Object> selectByActivityTypeNo(@Param("agentNo") String agentNo, @Param("activityTypeNo") String activityTypeNo);

    /**
     * 查询总开关的状态
     *
     * @return
     */
    Map<String, Object> selectDefaultStatus();

    Map<String, Object> selectActivityBySn(@Param("sn")String sn);

    @InsertProvider(type = SqlProvider.class, method = "insertAgentActivity")
    int insertAgentActivity(@Param("list") List happyBackList);

    /**
     * 插入下级代理商
     *
     * @param agent
     * @return
     */
    @Insert("insert into agent_info(agent_no,agent_node,agent_name,agent_level,parent_id,one_level_id,is_oem,team_id,email,phone,cluster,"
            + "agent_area,mobilephone,link_name,address,account_name,account_type,account_no,bank_name,cnaps_no,sale_name,creator,"
            + "status,create_date,province,city,area,count_level,account_province,account_city,sub_bank,agent_type,agent_oem,regist_type) values("
            + "#{agent.agentNo},#{agent.agentNode},#{agent.agentName},#{agent.agentLevel},#{agent.parentId},#{agent.oneLevelId},#{agent.isOem},#{agent.teamId},"
            + "#{agent.email},#{agent.phone},#{agent.cluster},#{agent.agentArea},#{agent.mobilephone},#{agent.linkName},"
            + "#{agent.address},#{agent.accountName},#{agent.accountType},#{agent.accountNo},#{agent.bankName},#{agent.cnapsNo},#{agent.saleName},#{agent.creator},"
            + "#{agent.status},now(),#{agent.province},#{agent.city},#{agent.area},#{agent.countLevel},#{agent.accountProvince},#{agent.accountCity},#{agent.subBank},"
            + "#{agent.agentType},#{agent.agentOem},#{agent.registType})")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "agent.id", before = false, resultType = Long.class)
    int insertAgentInfo(@Param("agent") AgentInfo agent);

    @InsertProvider(type = SqlProvider.class, method = "insertAgentProductList")
    int insertAgentProductList(@Param("list") List<JoinTable> bp);

    /**
     * 批量修改业务产品default_bp_flag为1
     *
     * @param agent_no
     * @param list
     * @return
     * @author ZengJA
     * @date 2017-08-03 16:38:41
     */
    @UpdateProvider(type = SqlProvider.class, method = "setDefaultBpFlagIs1")
    int setDefaultBpFlagIs1(@Param("agent_no") String agent_no, @Param("list") List<String> list);

    /**
     * 获取同样类型的父级代理商的分润规则
     *
     * @param childrenRule 下级的代理商的分润规则
     * @return 上级代理商同样类型的分润规则
     */
    AgentShareRule getSameTypeParentAgentShare(@Param("childrenRule") AgentShareRule childrenRule);

    /**
     * 获取同样类型的顶级代理商的最小服务费率
     *
     * @param rule       分润规则
     * @param oneLevelId 顶级代理商
     * @return
     */
    Map<String, Object> getSameTypeRootAgentMinServiceRate(@Param("rule") AgentShareRule rule,
                                                           @Param("oneLevelId") String oneLevelId,
                                                           @Param("agentNo") String agentNo);

    @InsertProvider(type = SqlProvider.class, method = "insertAgentShareList")
    int insertAgentShareList(@Param("list") List<AgentShareRule> shareList);

    /**
     * 获取代理商的所代理的业务产品中组长和组员的关系
     *
     * @param agentNo 代理商编号
     * @return 队长与队员的对应关系
     */
    List<Map<String, Long>> getLeaderAndMember(@Param("agentNo") String agentNo);


    AgentInfo selectBelongAgent(@Param("agentNo")String agentNo,@Param("entityId")String entityId);

    /**
     * 新增队员的服务Id
     *
     * @param leader  队长的业务id
     * @param member  队员的业务id
     * @param agentNo 需要新增分润的代理商编号
     */
    void insertMemberAgentShare(@Param("leader") String leader, @Param("member") String member, @Param("agentNo") String agentNo);

    //新增代理商的用户
    @Insert("insert into user_info(user_id,user_name,mobilephone,status,password,team_id,create_time,email) values(#{agent.userId},#{agent.userName},"
            + "#{agent.mobilephone},1,#{agent.password},#{agent.teamId},now(),#{agent.email})")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "agent.id", before = false, resultType = Long.class)
    int insertAgentUser(@Param("agent") AgentUserInfo agent);

    //新增代理商的结构组织
    @Insert("insert into user_entity_info(user_id,user_type,entity_id,apply,manage,status,last_notice_time,is_agent) values(#{agent.userId},1,#{agent.entityId},1,1,1,now(),#{agent.isAgent})")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "agent.id", before = false, resultType = Long.class)
    int insertAgentEntity(@Param("agent") AgentUserEntity agent);

    //给代理商授权管理员授
    @Insert("insert into agent_user_role(user_id,role_id) values(#{userId},5)")
    int insertAgentRole(Long userId);

    //修改代理商是否已开账户
    @Update("update agent_info set has_account=#{status} where agent_no=#{agentNo}")
    int updateAgentAccount(@Param("agentNo") String agentNo, @Param("status") int status);

    @Select("SELECT COUNT(1) FROM agent_info a ,agent_info b WHERE b.agent_node LIKE CONCAT(a.agent_node,'%') "
            + "AND  a.agent_no=#{curAgentNo} AND b.agent_no=#{agentNo}")
    @ResultType(Integer.class)
    int hasPermiss(@Param("curAgentNo") String curAgentNo, @Param("agentNo") String agentNo);

    //修改代理商安全手机
    @Update("UPDATE agent_info SET safephone = #{safePhone} WHERE agent_no = #{agentNo}")
    int updateSafePhone(@Param("agentNo") String agentNo, @Param("safePhone") String safePhone);

    @Select("SELECT d.* FROM agent_info a " +
            "LEFT JOIN agent_business_product p ON a.agent_no = p.agent_no  " +
            "LEFT JOIN business_product_define d ON  d.bp_id = p.bp_id " +
            "WHERE a.agent_no=#{agentNo} " +
            "AND d.effective_status = 1 " +
            "ORDER BY d.bp_name ASC ")
    @ResultType(BusinessProductDefine.class)
    List<BusinessProductDefine> getAgentProductList(@Param("agentNo") String agentNo);

    @Select("select * from agent_share_rule ar " +
            "left join service_info sr on sr.service_id=ar.service_id " +
            "where agent_no=#{agentNo} " +
            "and sr.effective_status = 1 ")
    @ResultType(AgentShareRule.class)
    List<AgentShareRule> getAgentShareList(@Param("agentNo") String agentNo);

    AgentShareRule getAgentShare(@Param("shareId") Long shareId);

    List<String> getLearderOrIndividualBpId(@Param("bpIds") List<String> bpIds, @Param("agentNo") String agentNo);

    ServiceInfo selectByServiceId(@Param("serviceId") String serviceId);

    List<ServiceRate> getNewAgentServicesByBpId(@Param("bpIds") List<String> bpIds);

    /**
     * 根据一级代理商编号查询欢乐返活动配置,新增代理商时欢乐返活动信息
     *
     * @param agentNo
     * @return
     */
    @Select("select aa.activity_type_no,aa.agent_no,aa.agent_node," +
            "aht.activity_type_name,aht.activity_code,aht.trans_amount" +
            " from agent_activity aa " +
            "left join activity_hardware_type aht on aa.activity_type_no = aht.activity_type_no " +
            "where aa.agent_no = #{agentNo} and  aa.sub_type = 1")
    @ResultType(List.class)
    List<HappyBackData> selectHappyBack(@Param("agentNo") String agentNo);

    /**
     * 转换显示
     *
     * @param funcNum
     * @return
     */
    @Select(" select function_name from function_manage where function_number = #{funcNum}")
    @ResultType(String.class)
    String getFunctionManagerByNum(@Param("funcNum") String funcNum);

    @Select("SELECT * FROM agent_activity WHERE agent_no = #{agentNo}  AND activity_type_no = #{activityTypeNo}")
    @ResultType(AgentActivity.class)
    AgentActivity findAgentActivityByParentAndType(@Param("agentNo") String agentNo, @Param("activityTypeNo") String activityTypeNo);


    @Select("SELECT DISTINCT sr.*,si.service_name FROM agent_business_product abp " +
            "LEFT JOIN business_product_info bpi ON abp.bp_id=bpi.bp_id " +
            "LEFT JOIN service_manage_rate sr ON bpi.service_id=sr.service_id AND sr.agent_no=#{oneAgentNo} " +
            "left join service_info si on si.service_id=sr.service_id  " +
            "WHERE abp.agent_no=#{agentNo} " +
            "and si.effective_status = 1 ")
    @ResultType(ServiceRate.class)
    List<ServiceRate> getAgentRate(@Param("oneAgentNo") String oneAgentNo, @Param("agentNo") String agentNo);

    @Select("SELECT DISTINCT sr.*,si.service_name FROM agent_business_product abp " +
            "LEFT JOIN business_product_info bpi ON abp.bp_id=bpi.bp_id " +
            "LEFT JOIN service_manage_quota sr ON bpi.service_id=sr.service_id AND sr.agent_no=#{oneAgentNo} " +
            "left join service_info si on si.service_id=sr.service_id " +
            "WHERE abp.agent_no=#{agentNo} " +
            "and si.effective_status = 1 ")
    @ResultType(ServiceQuota.class)
    List<ServiceQuota> getAgentQuota(@Param("oneAgentNo") String oneAgentNo, @Param("agentNo") String agentNo);

    //修改代理商安全密码
    @Update("UPDATE agent_info SET safe_password = MD5(CONCAT(#{safePassword}, '{', #{agentNo}, '}')) WHERE agent_no = #{agentNo}")
    int updateSafePassword(@Param("agentNo") String agentNo, @Param("safePassword") String safePassword);

    long countDirectChildren(@Param("parentId") String parentId);

    List<AgentInfo> listDirectChildren(@Param("parentId") String parentId, @Param("page") PageRequest page);

    List<AgentInfo> getAllDirectChildren(@Param("parentId") String parentId);

    long countAgentInfoByKeyword(@Param("loginUserInfo") UserInfoBean loginUserInfo,
                                 @Param("agentType") String agentType,
                                 @Param("keyword") String keyword);

    List<AgentInfo> listAgentInfoByKeyword(@Param("loginUserInfo") UserInfoBean loginUserInfo,
                                           @Param("agentType") String agentType,
                                           @Param("keyword") String keyword,
                                           @Param("page") PageRequest page);

    @SelectProvider(type = SqlProvider.class, method = "queryAgentInfoList")
    @ResultType(AgentInfo.class)
    List<AgentInfo> queryAgentInfoList(@Param("param") Map<String, String> param);

    @Select("select bank_no from pos_card_bin c  where  c.card_length = length(#{accountNo}) AND c.verify_code = left(#{accountNo},  c.verify_length) limit 1")
    @ResultType(String.class)
    String queryBankNo(@Param("accountNo")String accountNo);

    @Select(" SELECT MAX(agent_level + 0) FROM agent_info WHERE agent_level is NOT NULL")
    @ResultType(Integer.class)
    int getMaxAgentLevel();

    @Select(" SELECT agent_no, agent_node, agent_name, agent_level, parent_id, one_level_id FROM agent_info WHERE agent_level = #{agentLevel}")
    @ResultType(Map.class)
    List<Map<String,Object>> getAgentInfoByLevel(@Param("agentLevel") int agentLevel);

    @Select(
           " select " +
                   "  IF(account_no is null or account_no='',0,1) AS accountNo, " +
                   "  IF(safephone is null or account_no='',0,1) AS safephone, " +
                   "  IF(safe_password is null or account_no='',0,1) AS safePassword " +
                   " from agent_info WHERE agent_no=#{currAgentNo} "
    )
    @ResultType(Map.class)
    Map<String, Object> getTodoStatus(@Param("currAgentNo")String currAgentNo);

    class SqlProvider {

        /**
         * 查询代理商
         *
         * @param param
         * @return
         * @author ZengJA
         * @date 2017-02-27 14:04:54
         */
        public String queryAgentInfoList(Map<String, Object> param) {
            final Map<String, String> map = (Map<String, String>) param.get("param");
            SQL sql = new SQL();
            sql.SELECT("*");
            sql.FROM("agent_info");
            if (StringUtils.isNotBlank(map.get("agentName"))) {
                /**
                 * hasChild: 必传
                 *      ALL 全部
                 *      OFFICAL 直属
                 *      CHILDREN 直属外的下级
                 *      OTHERALL 是,当前登录代理商不包含自己,否则包含
                 *      SELF 否,查自己
                 */
                String hasChild = map.get("hasChild");
                switch (hasChild) {
                    case "ALL":
                        sql.WHERE("agent_node like #{param.agentNode} and agent_no != #{param.agentName}");
                        break;
                    case "OFFICAL":
                        sql.WHERE("agent_node like #{param.agentNode} and agent_level = #{param.nextAgentLevel}");
                        break;
                    case "CHILDREN":
                        sql.WHERE("agent_node like #{param.agentNode} " +
                                "and agent_no != #{param.agentName} and agent_level != #{param.nextAgentLevel}");
                        break;
                    case "OTHERALL":
                        sql.WHERE("agent_node like #{param.agentNode} and agent_no != #{param.entityId}");
                        break;
                    case "SELF":
                        sql.WHERE("agent_no = #{param.agentName}");
                        break;
                }
            }
            if (StringUtils.isNotBlank(map.get("startDate"))) {
                sql.WHERE(" create_date >= concat(#{param.startDate}, ' 00:00:00')");
            }
            if (StringUtils.isNotBlank(map.get("endDate"))) {
                sql.WHERE(" create_date <= concat(#{param.endDate}, ' 23:59:59')");
            }
            if (StringUtils.isNotBlank(map.get("mobilephone"))) {
                sql.WHERE(" mobilephone = #{param.mobilephone}");
            }
            if (StringUtils.isNotBlank(map.get("openFloor9Points"))) {
                sql.WHERE(" open_floor9_points = #{param.openFloor9Points}");
            }
            return sql.toString();
        }

        public String insertAgentActivity(Map<String, List<Map<String, String>>> param) {
            List<Map<String, String>> list = param.get("list");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("insert into agent_activity(activity_type_no,agent_no,agent_node,cash_back_amount," +
                    "tax_rate,create_time, repeat_register_amount,repeat_register_ratio," +
                    "full_prize_amount,not_full_deduct_amount,repeat_full_prize_amount,repeat_not_full_deduct_amount) values");
            MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].activityTypeNo},#'{'list[{0}].agentNo}," +
                    "#'{'list[{0}].agentNode} ,#'{'list[{0}].cashBackAmount},#'{'list[{0}].taxRate},now()," +
                    "#'{'list[{0}].repeatRegisterAmount},#'{'list[{0}].repeatRegisterRatio}," +
                    "#'{'list[{0}].fullPrizeAmount},#'{'list[{0}].notFullDeductAmount}," +
                    "#'{'list[{0}].repeatFullPrizeAmount},#'{'list[{0}].repeatNotFullDeductAmount})");
            return forAppend(stringBuilder, messageFormat, list.size());
        }

        public String insertAgentProductList(Map<String, List<JoinTable>> param) {
            List<JoinTable> list = param.get("list");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("replace into agent_business_product(agent_no,bp_id,status) values");
            MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].key3},#'{'list[{0}].key1},#'{'list[{0}].key2})");
            return forAppend(stringBuilder, messageFormat, list.size());
        }

        /**
         * 批量修改业务产品default_bp_flag为1
         *
         * @return
         * @author ZengJA
         * @date 2017-08-03 16:38:41
         */
        public String setDefaultBpFlagIs1(Map<String, Object> params) {
            final List<String> list = (List<String>) params.get("list");
            SQL sql = new SQL();
            sql.UPDATE("agent_business_product");
            sql.SET("default_bp_flag = 1");
            sql.WHERE("agent_no = #{agent_no}");
            StringBuilder sb = new StringBuilder("bp_id IN (");
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    sb.append("#{list[" + i + "]}");
                } else {
                    sb.append(",#{list[" + i + "]}");
                }
            }
            sb.append(")");
            sb.append(" AND ( bp_id NOT IN( SELECT bp_id FROM business_product_group )");
            sb.append(" OR bp_id IN (");
            sb.append(" SELECT bpg.bp_id FROM business_product_group bpg");
            sb.append(" JOIN business_product_define bpd ON bpg.bp_id = bpd.bp_id");
            sb.append(" WHERE bpd.allow_individual_apply = 1))");
            sql.WHERE(sb.toString());
            System.err.println(sql.toString() + "  <自定义费率>  " + list);
            return sql.toString();
        }

        public String insertAgentShareList(Map<String, List<AgentShareRule>> param) {
            List<AgentShareRule> list = param.get("list");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("insert into agent_share_rule(agent_no,service_id,card_type,holidays_mark,efficient_date,disabled_date,profit_type,per_fix_income,per_fix_inrate,safe_line,capping,"
                    + "share_profit_percent,ladder,cost_rate_type,per_fix_cost,cost_rate,cost_capping,cost_safeline,check_status,lock_status,ladder1_rate,ladder1_max,ladder2_rate,ladder2_max,ladder3_rate,"
                    + "ladder3_max,ladder4_rate,ladder4_max) values");
            MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].agentId},#'{'list[{0}].serviceId},#'{'list[{0}].cardType},#'{'list[{0}].holidaysMark},now(),#'{'list[{0}].disabledDate},"
                    + "#'{'list[{0}].profitType},#'{'list[{0}].perFixIncome},#'{'list[{0}].perFixInrate},#'{'list[{0}].safeLine},#'{'list[{0}].capping},#'{'list[{0}].shareProfitPercent},#'{'list[{0}].ladder},#'{'list[{0}].costRateType},"
                    + "#'{'list[{0}].perFixCost},#'{'list[{0}].costRate},#'{'list[{0}].costCapping},#'{'list[{0}].costSafeline},#'{'list[{0}].checkStatus},#'{'list[{0}].lockStatus},#'{'list[{0}].ladder1Rate},#'{'list[{0}].ladder1Max},"
                    + "#'{'list[{0}].ladder2Rate},#'{'list[{0}].ladder2Max},#'{'list[{0}].ladder3Rate},#'{'list[{0}].ladder3Max},#'{'list[{0}].ladder4Rate},#'{'list[{0}].ladder4Max})");
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(messageFormat.format(new Integer[]{i}));
                stringBuilder.append(",");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);

            return stringBuilder.toString();
        }


        private String forAppend(StringBuilder stringBuilder, MessageFormat messageFormat, int size) {
            for (int i = 0; i < size; i++) {
                stringBuilder.append(messageFormat.format(new Integer[]{i}));
                stringBuilder.append(",");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
    }
}
