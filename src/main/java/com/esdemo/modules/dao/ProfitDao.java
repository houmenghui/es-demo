package com.esdemo.modules.dao;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.ServiceQuota;
import com.esdemo.modules.bean.ServiceRate;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lmc
 * @date 2019/5/29 14:42
 */
@Mapper
public interface ProfitDao {

    /*
    近6个月累计分润不包含当月
    */
    @Select("select IFNULL(sum(cnt_amount),0.00) cnt_amount from (" +
            " SELECT trans.total_money cnt_amount FROM agent_monthtrans_share_collect trans WHERE trans.agent_no = #{agent_no} and trans.collec_time BETWEEN #{time_start} and #{time_end}" +
            " UNION ALL" +
            " SELECT phb.total_money cnt_amount FROM agent_monthhpb_share_collect phb where phb.agent_no = #{agent_no} and phb.collec_time BETWEEN #{time_start} and #{time_end}" +
            " UNION ALL" +
            " SELECT settle.total_money cnt_amount from agent_monthsettle_share_collect settle where settle.agent_no = #{agent_no} and settle.collec_time BETWEEN #{time_start} and #{time_end}" +
            " ) tab")
    String getAccumulatedIncome(@Param("agent_no") String agent_no, @Param("time_start") String time_start, @Param("time_end") String time_end); //

    /*
    获取当天分润
    */
    @SelectProvider(type = SqlProvider.class, method = "getTodayIncome")
    String getTodayIncome(@Param("agent_no") String agent_no, @Param("agent_level") String agent_level, @Param("agent_node")String agent_node);//

    /*
    获取本月分润
    */
    @Select(
            "SELECT IFNULL(SUM(tab.cnt_amount),0.00) cnt_amount from (" +
                    " SELECT SUM(hpb.total_money) cnt_amount from agent_dayhpb_share_collect hpb where hpb.collec_time >= #{time_str} and hpb.agent_no = #{agent_no}" +
                    " UNION ALL" +
                    " SELECT SUM(settle.total_money) cnt_amount from agent_daysettle_share_collect settle where settle.collec_time >= #{time_str} and settle.agent_no = #{agent_no}" +
                    " UNION ALL" +
                    " SELECT SUM(trans.total_money) cnt_amount from agent_daytrans_share_collect trans where trans.collec_time >= #{time_str} and trans.agent_no = #{agent_no}" +
                    " ) tab"
    )
    String getMonthIncome(@Param("agent_no") String agent_no, @Param("time_str") String time_str);

    /*
    获取分润账户
    */
    @Select(
            "SELECT (bea.curr_balance - bea.control_amount - bea.settling_amount) available_balance,bea.curr_balance balance,bea.control_amount freeze_amount " +
                    "FROM bill_ext_account bea " +
                    "LEFT JOIN ext_account_info eai ON bea.account_no = eai.account_no " +
                    "WHERE bea.subject_no='224105' AND eai.user_id=#{agent_no} "
    )
    Map<String, Object> getShareAccount(@Param("agent_no") String agent_no);

    /*
    获取活动补贴
    */
    @Select(
            "SELECT (bea.curr_balance - bea.control_amount - bea.settling_amount) available_balance,bea.curr_balance balance,bea.control_amount freeze_amount " +
                    "FROM bill_ext_account bea " +
                    "LEFT JOIN ext_account_info eai ON bea.account_no = eai.account_no " +
                    "WHERE bea.subject_no='224106' AND eai.user_id=#{agent_no} "
    )
    Map<String, Object> getActivitySubsidy(@Param("agent_no") String agent_no);

    /*
    查询我的分润趋势
    */
    @SelectProvider(type = ProfitDao.SqlProvider.class, method = "getProfitTendency")
    String getProfitTendency(@Param("select_type") String select_type, @Param("agent_no") String agent_no
            , @Param("start_time") String start_time, @Param("end_time") String end_time);

    /**
     * 查询我的分润趋势
     * @param select_type 查询类型，1：七日；2：半年；
     * @param agent_no    代理商编号
     * @param start_time  开始时间
     * @param end_time    结束时间
     * @return
     */
    @SelectProvider(type = ProfitDao.SqlProvider.class, method = "getProfitTendencyGroupByTime")
    List<Map<String, Object>> getProfitTendencyGroupByTime(@Param("select_type") String select_type, @Param("agent_no") String agent_no
            , @Param("start_time") String start_time, @Param("end_time") String end_time);

    /*
   查询账户明细
    */

    @SelectProvider(type = ProfitDao.SqlProvider.class, method = "getProfitDetail")
    @ResultType(List.class)
    List<Map<String, Object>> getProfitDetail(@Param("params_map") Map<String, Object> params_map);

    /*
   查询账户统计
    */
    @SelectProvider(type = ProfitDao.SqlProvider.class, method = "getProfitCount")
    @ResultType(Map.class)
    Map<String, Object> getProfitCount(@Param("params_map") Map<String, Object> params_map);


    /**
     * 查询总开关的状态
     *
     * @return
     */
    @Select("SELECT * FROM agent_account_control where default_status = '1'")
    @ResultType(Map.class)
    Map<String, Object> selectDefaultStatus();

    /**
     * 根据代理商编号查询到当前代理商设置的留存金额
     *
     * @param agent_no
     * @return
     */
    @Select(
            "SELECT * FROM agent_account_control where agent_no = #{agent_no}"
    )
    @ResultType(Map.class)
    Map<String, Object> selectRetainAmount(@Param("agent_no") String agent_no);

    @Select("SELECT (IFNULL(activity_terminal_freeze_amount,0) + IFNULL(activity_other_freeze_amount,0)) AS pre_freeze_amount " +
            "FROM agent_pre_record_total " +
            "WHERE agent_no = #{agent_no}")
    @ResultType(Map.class)
    Map<String, Object> getPreFreezeInActivitySubSidy(@Param("agent_no") String agent_no);

    @Select("SELECT (IFNULL(terminal_freeze_amount,0) + IFNULL(other_freeze_amount,0)) AS pre_freeze_amount " +
            "FROM agent_pre_record_total " +
            "WHERE agent_no = #{agent_no}")
    @ResultType(Map.class)
    Map<String, Object> getPreFreezeInShareAccount(@Param("agent_no")String agent_no);

    class SqlProvider {
        public String getProfitDetail(Map<String, Object> params_map) {
            @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) params_map.get("params_map");
            String sql = new SQL() {
                {
                    SELECT(" eti.record_amount,eti.avali_balance,CONCAT(eti.record_date,' ',eti.record_time) trans_time,eti.debit_credit_side,rart.trans_type_name as summary_info");
                    FROM(" ext_trans_info eti ");
                    LEFT_OUTER_JOIN(" ext_account_info eai on eti.account_no=eai.account_no ");
                    LEFT_OUTER_JOIN(" record_account_rule_trans_type rart on rart.trans_type_code=eti.trans_type ");

                    WHERE(" 1=1 and eai.user_id=#{params_map.agent_no} ");
                    //select_type  0-全部 1-收入 2-支出
                    if ("1".equals(StringUtils.filterNull(map.get("select_type")))) {
                        WHERE(" eti.debit_credit_side='credit' ");
                    }
                    if ("2".equals(StringUtils.filterNull(map.get("select_type")))) {
                        WHERE(" eti.debit_credit_side='debit' ");
                    }
                    //profit_type  1-分润账户  2-活动补贴
                    if ("1".equals(StringUtils.filterNull(map.get("profit_type")))) {
                        WHERE(" eai.subject_no='224105' ");
                    }
                    if ("2".equals(StringUtils.filterNull(map.get("profit_type")))) {
                        WHERE(" eai.subject_no='224106' ");
                    }
                    if (!"".equals(StringUtils.filterNull(map.get("date_start"))) || !"".equals(StringUtils.filterNull(map.get("date_end")))) {
                        WHERE(" eti.record_date between #{params_map.date_start} and #{params_map.date_end} ");
                    }
                    ORDER_BY("eti.id desc");
                }
            }.toString();
            return sql;
        }

        /*
        查询统计信息
         */
        public String getProfitCount(Map<String, Object> params_map) {
            @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) params_map.get("params_map");
            String sql = new SQL() {
                {
                    SELECT(" sum(IF (eti.debit_credit_side = 'credit', eti.record_amount,'0.00')) AS credit, sum(IF (eti.debit_credit_side = 'debit', eti.record_amount,'0.00')) AS debit ");
                    FROM(" ext_trans_info eti ");
                    LEFT_OUTER_JOIN(" ext_account_info eai on eti.account_no=eai.account_no ");
                    //交易冻结 000025 交易解冻  //000026
                    WHERE(" 1=1 and eai.user_id=#{params_map.agent_no} and eti.trans_type<>'000025' and eti.trans_type<>'000026' ");
                    //select_type  0-全部 1-收入 2-支出
                    if ("1".equals(StringUtils.filterNull(map.get("select_type")))) {
                        WHERE(" eti.debit_credit_side='credit' ");
                    }
                    if ("2".equals(StringUtils.filterNull(map.get("select_type")))) {
                        WHERE(" eti.debit_credit_side='debit' ");
                    }
                    //profit_type  1-分润账户  2-活动补贴
                    if ("1".equals(StringUtils.filterNull(map.get("profit_type")))) {
                        WHERE(" eai.subject_no='224105' ");
                    }
                    if ("2".equals(StringUtils.filterNull(map.get("profit_type")))) {
                        WHERE(" eai.subject_no='224106' ");
                    }
                    if (!"".equals(StringUtils.filterNull(map.get("date_start"))) || !"".equals(StringUtils.filterNull(map.get("date_end")))) {
                        WHERE(" eti.record_date between #{params_map.date_start} and #{params_map.date_end} ");
                    }
                    ORDER_BY("eti.id desc");
                }
            }.toString();
            return sql;
        }

        public String getTodayIncome(String agent_no, String agent_level, String agent_node) {
            StringBuffer sql = new StringBuffer("SELECT SUM(cnt_amount) cnt_amount from (");
            sql.append(" select profit cnt_amount from agent_profit_day_count where agent_no = #{agent_no}  and count_date = DATE(now()) ")
                    .append(" UNION ALL")
                    .append("  SELECT   IFNULL(SUM(amount), 0) AS cnt_amount FROM xhlf_agent_account_detail WHERE agent_no =  #{agent_no}   ")
                    .append("  AND account_time >=  DATE(now()) and account_status = '1'")
                    .append(" UNION ALL  SELECT IFNULL(SUM(-amount), 0) AS cnt_amount FROM xhlf_agent_deduction_account_detail WHERE agent_no = #{ agentNo } AND account_time >= DATE(now()) AND account_status in ('2','3') ")
                    .append(" UNION ALL")
                    .append(" SELECT IFNULL(SUM(cbd.cash_back_amount),0) cnt_amount FROM cash_back_detail cbd where cbd.agent_no = #{agent_no} and cbd.entry_time  > DATE(now()) and entry_status = '1' and  amount_type  != '3' ")
                    .append(" UNION ALL")
                    .append(" SELECT IFNULL(SUM(-cbd.cash_back_amount),0) cnt_amount FROM cash_back_detail cbd where cbd.agent_no = #{agent_no} and cbd.entry_time  > DATE(now()) and entry_status = '1' and amount_type  = '3' ")
                    .append(" ) tab");
            return sql.toString();
        }

        public String getProfitTendency(String select_type, String agent_no, String start_time, String end_time) {
            StringBuffer sql = new StringBuffer("SELECT IFNULL(SUM(tab.cnt_amount),0.00) cnt_amount from (");
            if(Objects.equals(select_type,"1")){
                //按日趋势
                sql.append(" SELECT SUM(hpb.total_money) cnt_amount from agent_dayhpb_share_collect hpb where hpb.collec_time BETWEEN #{start_time} and #{end_time} and hpb.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT SUM(settle.total_money) cnt_amount from agent_daysettle_share_collect settle where settle.collec_time BETWEEN #{start_time} and #{end_time} and settle.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT SUM(trans.total_money) cnt_amount from agent_daytrans_share_collect trans where trans.collec_time BETWEEN #{start_time} and #{end_time} and trans.agent_no = #{agent_no}");
            } else if (Objects.equals(select_type,"2")){
                //按月趋势
                sql.append(" SELECT SUM(hpb.total_money) cnt_amount from agent_monthhpb_share_collect hpb where hpb.collec_time BETWEEN #{start_time} and #{end_time} and hpb.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT SUM(settle.total_money) cnt_amount from agent_monthsettle_share_collect settle where settle.collec_time BETWEEN #{start_time} and #{end_time} and settle.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT SUM(trans.total_money) cnt_amount from agent_monthtrans_share_collect trans where trans.collec_time BETWEEN #{start_time} and #{end_time} and trans.agent_no = #{agent_no}");
            }
            sql.append(" ) tab");
            return sql.toString();
        }

        /**
         * 收入趋势，按时间自动分组
         * @param select_type
         * @param agent_no
         * @param start_time
         * @param end_time
         * @return
         */
        public String getProfitTendencyGroupByTime(String select_type, String agent_no, String start_time, String end_time) {
            StringBuffer sql = new StringBuffer("SELECT IFNULL(SUM(tab.cnt_amount),0.00) Y,tab.dt X from (");
            if(Objects.equals(select_type,"1")){
                //按日趋势
                sql.append(" SELECT hpb.total_money cnt_amount,DATE_FORMAT(hpb.collec_time,'%m-%d') dt from agent_dayhpb_share_collect hpb where hpb.collec_time BETWEEN #{start_time} and #{end_time} and hpb.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT settle.total_money cnt_amount,DATE_FORMAT(settle.collec_time,'%m-%d') dt from agent_daysettle_share_collect settle where settle.collec_time BETWEEN #{start_time} and #{end_time} and settle.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT trans.total_money cnt_amount,DATE_FORMAT(trans.collec_time,'%m-%d') dt from agent_daytrans_share_collect trans where trans.collec_time BETWEEN #{start_time} and #{end_time} and trans.agent_no = #{agent_no}");
            } else if (Objects.equals(select_type,"2")){
                //按月趋势
                sql.append(" SELECT hpb.total_money cnt_amount,DATE_FORMAT(hpb.collec_time,'%Y-%m') dt from agent_monthhpb_share_collect hpb where hpb.collec_time BETWEEN #{start_time} and #{end_time} and hpb.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT settle.total_money cnt_amount,DATE_FORMAT(settle.collec_time,'%Y-%m') dt from agent_monthsettle_share_collect settle where settle.collec_time BETWEEN #{start_time} and #{end_time} and settle.agent_no = #{agent_no}")
                        .append(" UNION ALL")
                        .append(" SELECT trans.total_money cnt_amount,DATE_FORMAT(trans.collec_time,'%Y-%m') dt from agent_monthtrans_share_collect trans where trans.collec_time BETWEEN #{start_time} and #{end_time} and trans.agent_no = #{agent_no}");
            }
            sql.append(" ) tab").append(" GROUP BY tab.dt ORDER BY tab.dt");
            return sql.toString();
        }
    }

    //查询安全密码
    @Select(
            "select safe_password from agent_info WHERE agent_no = #{agentNo}"
    )
    String getSafePassword(@Param("agentNo") String agentNo);

    /*
    查询服务信息表
     */
    @Select(" SELECT * FROM out_account_service where service_type = #{serviceType} and out_account_status = '1' order by level ")
    @ResultType(List.class)
    List<Map<String,Object>> selectByServiceType(@Param("serviceType")String serviceType);

    /**
     * 海涛,国栋,水育确认上游金额小于设置金额,自动关闭通道开关,然后账务手动开启
     * @param id
     * @return
     */
    @Update(" UPDATE out_account_service SET out_account_status = '0' WHERE id = #{id} ")
    @ResultType(Integer.class)
    Integer updateWithdrawSwitch(@Param("id")Integer id);

    /**
     * 获取一级服务费率
     * @param serviceId
     * @return
     */
    @Select(" SELECT id,service_id,holidays_mark,card_type,quota_level,agent_no,rate_type,\n" +
            "\tsingle_num_amount,rate,capping,safe_line,check_status,lock_status,\n" +
            "\tladder1_rate,ladder1_max,ladder2_rate,ladder2_max,\n" +
            "\tladder3_rate,ladder3_max,ladder4_rate,ladder4_max\n" +
            " FROM service_manage_rate WHERE service_id = #{serviceId} AND agent_no = 0")
    @ResultType(ServiceRate.class)
    ServiceRate getFristAgentServiceRateById(@Param("serviceId")String serviceId);

    /*
    获取服务管控限额
     */
    @Select("select smq.* from service_manage_quota smq,activity_config ac where ac.agent_service_id = smq.service_id and smq.agent_no = 0 ")
    @ResultType(ServiceQuota.class)
    ServiceQuota queryHlsServiceQuota();

    /*
    是否可以提现
     */
    @Select("SELECT COUNT(1) FROM service_info si \n" +
            "WHERE CURRENT_TIME BETWEEN si.trad_Start AND si.trad_end\n" +
            "AND CURRENT_DATE BETWEEN si.use_starttime AND si.use_endtime\n" +
            "AND  si.service_id = #{serviceId}")
    boolean canWithdrawCash(@Param("serviceId") Long serviceId);

    /*
    获取提现信息
     */
    @Select("SELECT * from 	settle_order_info i where "
            + "i.settle_user_no = #{entityId} AND NOT EXISTS (SELECT 1 from settle_transfer st WHERE "
            + "i.settle_order = st.trans_id AND st.settle_type = i.settle_type) AND i.create_time > CURDATE() and i.syn_status = '1' "
            + " and i.settle_status = '0' and i.sub_type = #{subType}")
    @ResultType(Map.class)
    Map<String, Object> findWithDrawCash(@Param("entityId")String entityId,@Param("subType")String subType);

    /*
    记录提现记录
     */
    @Insert("insert settle_order_info(create_time,settle_type,source_system,create_user,settle_user_type,settle_user_no,settle_status"
            + ",syn_status,settle_order_status,settle_amount,agent_node,holidays_mark,acq_enname,source_order_no,source_batch_no,sub_type) "
            + "values(now(),#{map.settleType},#{map.sourceSystem},#{map.createUser},#{map.settleUserType},#{map.settleUserNo},#{map.settleStatus},"
            + "#{map.synStatus},#{map.settleOrderStatus},#{map.settleAmount},#{map.agentNode},#{map.holidaysMark},#{map.acqenname},#{map.sourceOrderNo},"
            + "#{map.sourceBatchNo},#{map.subType})")
    @SelectKey(statement="select LAST_INSERT_ID()", keyProperty="map.settle_order", before=false, resultType=Long.class)
    int insertWithDrawCash(@Param("map")Map<String, Object> map);
}
