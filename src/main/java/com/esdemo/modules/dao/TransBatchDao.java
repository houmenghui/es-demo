package com.esdemo.modules.dao;


import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.TransDayBatch;
import com.esdemo.modules.bean.TransMonthBatch;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Mapper
public interface TransBatchDao {

    @Select("SELECT * FROM agent_daytrans_share_collect WHERE collec_time = #{collecTime} ORDER BY id DESC LIMIT 1")
    @ResultType(TransDayBatch.class)
    TransDayBatch getTransDayBatchByCollecTime(@Param("collecTime") String collecTime);

    @Select("SELECT * FROM agent_monthtrans_share_collect WHERE collec_time = #{collecTime} ORDER BY id DESC LIMIT 1")
    @ResultType(TransMonthBatch.class)
    TransMonthBatch getTransMonthBatchByCollecTime(@Param("collecTime") String collecTime);

    @Insert(" INSERT INTO agent_daytrans_share_collect(agent_no, agent_node, parent_id, total_count, total_trans_amount, total_money, acc_money, collec_time, create_time)" +
            " VALUES(#{transDayBatch.agentNo}, #{transDayBatch.agentNode}, #{transDayBatch.parentId}, #{transDayBatch.totalCount}, #{transDayBatch.totalTransAmount}, " +
            " #{transDayBatch.totalMoney}, #{transDayBatch.accMoney}, #{transDayBatch.collecTime}, NOW())")
    int insertTransDayBatch(@Param("transDayBatch") TransDayBatch transDayBatch);

    @Insert(" INSERT INTO agent_monthtrans_share_collect(agent_no, agent_node, parent_id, total_count, total_trans_amount, total_money, acc_money, collec_time, create_time)" +
            " VALUES(#{transMonthBatch.agentNo}, #{transMonthBatch.agentNode}, #{transMonthBatch.parentId}, #{transMonthBatch.totalCount}, #{transMonthBatch.totalTransAmount}, " +
            " #{transMonthBatch.totalMoney}, #{transMonthBatch.accMoney}, #{transMonthBatch.collecTime}, NOW())")
    int insertTransMonthBatch(@Param("transMonthBatch") TransMonthBatch transMonthBatch);

    @Select("SELECT SUM(total_money) subSumTotalAmount FROM agent_daytrans_share_collect WHERE parent_id = #{parentId} AND collec_time = #{collecTime}")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumTransDayTotalMoneyByParentId(@Param("parentId") String parentId, @Param("collecTime") String collecTime);

    @Select("SELECT SUM(total_money) subSumTotalAmount FROM agent_monthtrans_share_collect WHERE parent_id = #{parentId} AND collec_time = #{collecTime}")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumTransMonthTotalMoneyByParentId(@Param("parentId") String parentId, @Param("collecTime") String collecTime);

    @Select(" SELECT IFNULL(COUNT(id), 0) collectDay, IFNULL(SUM(total_money), 0) collectSum, IFNULL(SUM(total_count), 0) collectCount, " +
            " IFNULL(SUM(total_trans_amount), 0) collectTrans FROM agent_daytrans_share_collect WHERE " +
            " agent_no = #{agentNo} AND collec_time >= #{beginOfMonth} AND collec_time <= #{endOfMonth} ")
    @ResultType(Map.class)
    Map<String, Object> hasCollectDay(@Param("agentNo") String agentNo, @Param("beginOfMonth") String beginOfMonth, @Param("endOfMonth") String endOfMonth);

    @SelectProvider(type = TransBatchDao.SqlProvider.class, method = "sumTodayTotalIncome")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumTodayTotalIncome(@Param("agentNo") String agentNo);

    @SelectProvider(type = TransBatchDao.SqlProvider.class, method = "summaryProfitIncomeByTerms")
    List<Map<String, Object>> summaryProfitIncomeByTerms(@Param("timeUnit") TimeUnitEnum timeUnit, @Param("agentNode") String agentNode,
                                                         @Param("beginTimeStr") String beginTimeStr, @Param("endTimeStr") String endTimeStr);

    class SqlProvider {
        /**
         * 统计代理商分润收入数据
         *
         * @param param
         * @return
         */
        public String summaryProfitIncomeByTerms(Map<String, Object> param) {
            final TimeUnitEnum timeUnit = (TimeUnitEnum) param.get("timeUnit");
            final String agentNode = StringUtils.filterNull(param.get("agentNode"));
            final String beginTimeStr = StringUtils.filterNull(param.get("beginTimeStr"));
            final String endTimeStr = StringUtils.filterNull(param.get("endTimeStr"));

            String timeUnitField = "t.collec_time";
            String transTable = "agent_daytrans_share_collect";
            String settleTable = "agent_daysettle_share_collect";
            if (TimeUnitEnum.MONTH == timeUnit) {
                timeUnitField = "DATE_FORMAT(t.collec_time, '%Y-%m')";
                transTable = "agent_monthtrans_share_collect";
                settleTable = "agent_monthsettle_share_collect";
            }

            String fromUnionSql = "(select tsc.total_money, tsc.acc_money, tsc.collec_time from " + transTable + " tsc where 1 = 1  {0}" +
                    " UNION ALL " +
                    "select ssc.total_money, ssc.acc_money, ssc.collec_time from " + settleTable + "  ssc where 1 = 1 {1}) t";
            String replace_one = " and tsc.agent_node = #{agentNode} ", replace_two = " and ssc.agent_node = #{agentNode} ";
            if (StringUtils.isNotBlank(beginTimeStr)) {
                replace_one += " and tsc.collec_time >= #{beginTimeStr} ";
                replace_two += " and ssc.collec_time >= #{beginTimeStr} ";
            }
            if (StringUtils.isNotBlank(endTimeStr)) {
                replace_one += " and tsc.collec_time <= #{endTimeStr} ";
                replace_two += " and ssc.collec_time <= #{endTimeStr} ";
            }
            fromUnionSql = MessageFormat.format(fromUnionSql, new Object[]{replace_one, replace_two});

            String finalFromUnionSql = fromUnionSql;
            String finalTimeUnitField = timeUnitField;
            SQL sql = new SQL() {{
                SELECT(finalTimeUnitField + " collect_time, SUM(t.total_money) total_money, SUM(t.acc_money) acc_money");
                FROM(finalFromUnionSql);
                GROUP_BY(finalTimeUnitField);
            }};
            return sql.toString();
        }

        /**
         * 统计代理商今日收入
         *
         * @param param
         * @return
         */
        public String sumTodayTotalIncome(Map<String, Object> param) {

            StringBuffer sql = new StringBuffer("SELECT SUM(cnt_amount) cnt_amount from (");
            sql.append(" select profit cnt_amount from agent_profit_day_count where agent_no = #{agentNo}  and count_date = DATE(now()) ")
                    .append(" UNION ALL")
                    .append("  SELECT IFNULL(SUM(amount), 0) AS cnt_amount FROM xhlf_agent_account_detail WHERE agent_no =  #{agentNo}   ")
                    .append("  AND account_time >=  DATE(now()) and account_status = '1'")
                    .append(" UNION ALL SELECT IFNULL(SUM(-amount), 0) AS cnt_amount   FROM xhlf_agent_deduction_account_detail WHERE agent_no = #{ agentNo } AND account_time >= DATE(now())   AND account_status in ('2','3') ")
                    .append(" UNION ALL")
                    .append(" SELECT IFNULL(SUM(cbd.cash_back_amount),0) cnt_amount FROM cash_back_detail cbd where cbd.agent_no = #{agentNo} and cbd.entry_time  > DATE(now()) and entry_status = '1' and  amount_type  != '3' ")
                    .append(" UNION ALL")
                    .append(" SELECT IFNULL(SUM(-cbd.cash_back_amount),0) cnt_amount FROM cash_back_detail cbd where cbd.agent_no = #{agentNo} and cbd.entry_time  > DATE(now()) and entry_status = '1' and amount_type  = '3' ")
                    .append(" ) tab");
            return sql.toString();
        }
    }
}
