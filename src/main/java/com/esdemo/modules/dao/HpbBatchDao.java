package com.esdemo.modules.dao;

import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.HpbDayBatch;
import com.esdemo.modules.bean.HpbMonthBatch;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface HpbBatchDao {

    @Select("SELECT * FROM agent_dayhpb_share_collect WHERE collec_time = #{collecTime} ORDER BY id DESC LIMIT 1")
    @ResultType(HpbDayBatch.class)
    HpbDayBatch getHpbDayBatchByCollecTime(@Param("collecTime") String collecTime);

    @Select("SELECT SUM(total_money) subSumTotalAmount FROM agent_dayhpb_share_collect WHERE parent_id = #{parentId} AND collec_time = #{collecTime}")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumHpbDayTotalMoneyByParentId(@Param("parentId") String parentId, @Param("collecTime") String collecTime);

    @Select("SELECT * FROM agent_monthhpb_share_collect WHERE collec_time = #{collecTime} ORDER BY id DESC LIMIT 1")
    @ResultType(HpbMonthBatch.class)
    HpbMonthBatch getHpbMonthBatchByCollecTime(@Param("collecTime") String collecTime);

    @Select("SELECT SUM(total_money) subSumTotalAmount FROM agent_monthhpb_share_collect WHERE parent_id = #{parentId} AND collec_time = #{collecTime}")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumHpbMonthTotalMoneyByParentId(@Param("parentId") String parentId, @Param("collecTime") String collecTime);

    @Insert(" INSERT INTO agent_dayhpb_share_collect(agent_no, agent_node, parent_id, total_count, total_money, acc_money, collec_time, create_time)" +
            " VALUES(#{hpbDayBatch.agentNo}, #{hpbDayBatch.agentNode}, #{hpbDayBatch.parentId}, #{hpbDayBatch.totalCount}, #{hpbDayBatch.totalMoney}," +
            " #{hpbDayBatch.accMoney}, #{hpbDayBatch.collecTime}, NOW())")
    int insertHpbDayBatch(@Param("hpbDayBatch") HpbDayBatch hpbDayBatch);

    @Insert(" INSERT INTO agent_monthhpb_share_collect(agent_no, agent_node, parent_id, total_count, total_money, acc_money, collec_time, create_time)" +
            " VALUES(#{hpbMonthBatch.agentNo}, #{hpbMonthBatch.agentNode}, #{hpbMonthBatch.parentId}, #{hpbMonthBatch.totalCount}, #{hpbMonthBatch.totalMoney}," +
            " #{hpbMonthBatch.accMoney}, #{hpbMonthBatch.collecTime}, NOW())")
    int insertHpbMonthBatch(@Param("hpbMonthBatch") HpbMonthBatch hpbMonthBatch);

    @Select(" SELECT IFNULL(COUNT(id), 0) collectDay, IFNULL(SUM(total_money), 0) collectSum, IFNULL(SUM(total_count), 0) collectCount FROM agent_dayhpb_share_collect WHERE " +
            " agent_no = #{agentNo} AND collec_time >= #{beginOfMonth} AND collec_time <= #{endOfMonth} ")
    @ResultType(Map.class)
    Map<String, Object> hasCollectDay(@Param("agentNo") String agentNo, @Param("beginOfMonth") String beginOfMonth, @Param("endOfMonth") String endOfMonth);

    @SelectProvider(type = HpbBatchDao.SqlProvider.class, method = "summaryHpbIncomeByTerms")
    List<Map<String, Object>> summaryHpbIncomeByTerms(@Param("timeUnit") TimeUnitEnum timeUnit, @Param("agentNode") String agentNode,
                                                      @Param("beginTimeStr") String beginTimeStr, @Param("endTimeStr") String endTimeStr);

    class SqlProvider {
        /**
         * 统计代理商分润收入数据
         *
         * @param param
         * @return
         */
        public String summaryHpbIncomeByTerms(Map<String, Object> param) {
            final TimeUnitEnum timeUnit = (TimeUnitEnum) param.get("timeUnit");
            final String agentNode = StringUtils.filterNull(param.get("agentNode"));
            final String beginTimeStr = StringUtils.filterNull(param.get("beginTimeStr"));
            final String endTimeStr = StringUtils.filterNull(param.get("endTimeStr"));


            String timeUnitField = "collec_time";
            String hpbTable = "agent_dayhpb_share_collect";
            if (TimeUnitEnum.MONTH == timeUnit) {
                timeUnitField = "DATE_FORMAT(collec_time, '%Y-%m')";
                hpbTable = "agent_monthhpb_share_collect";
            }
            String finalTimeUnitField = timeUnitField;
            String finalHpbTable = hpbTable;
            SQL sql = new SQL() {{
                SELECT(finalTimeUnitField + " collect_time, SUM(total_money) total_money, SUM(acc_money) acc_money ");
                FROM(finalHpbTable);
                GROUP_BY(finalTimeUnitField);
            }};
            if (StringUtils.isNotBlank(beginTimeStr)) {
                sql.WHERE("collec_time >= #{beginTimeStr} ");
            }
            if (StringUtils.isNotBlank(endTimeStr)) {
                sql.WHERE("collec_time <= #{endTimeStr} ");
            }
            sql.WHERE("agent_node = #{agentNode} ");
            return sql.toString();
        }
    }
}
