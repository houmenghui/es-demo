package com.esdemo.modules.dao;


import com.esdemo.modules.bean.SettleDayBatch;
import com.esdemo.modules.bean.SettleMonthBatch;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.Map;
@Mapper
public interface SettleBatchDao {

    @Select("SELECT * FROM agent_daysettle_share_collect WHERE collec_time = #{collecTime} ORDER BY id DESC LIMIT 1")
    @ResultType(SettleDayBatch.class)
    SettleDayBatch getSettleDayBatchByCollecTime(@Param("collecTime") String collecTime);

    @Select("SELECT SUM(total_money) subSumTotalAmount FROM agent_daysettle_share_collect WHERE parent_id = #{parentId} AND collec_time = #{collecTime}")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumSettleDayTotalMoneyByParentId(@Param("parentId") String parentId, @Param("collecTime") String collecTime);

    @Select("SELECT * FROM agent_monthsettle_share_collect WHERE collec_time = #{collecTime} ORDER BY id DESC LIMIT 1")
    @ResultType(SettleMonthBatch.class)
    SettleMonthBatch getSettleMonthBatchByCollecTime(@Param("collecTime") String collecTime);

    @Select("SELECT SUM(total_money) subSumTotalAmount FROM agent_monthsettle_share_collect WHERE parent_id = #{parentId} AND collec_time = #{collecTime}")
    @ResultType(Map.class)
    Map<String, BigDecimal> sumSettleMonthTotalMoneyByParentId(@Param("parentId") String parentId, @Param("collecTime") String collecTime);

    @Insert(" INSERT INTO agent_daysettle_share_collect(agent_no, agent_node, parent_id, total_count, total_money, acc_money, collec_time, create_time)" +
            " VALUES(#{settleDayBatch.agentNo}, #{settleDayBatch.agentNode}, #{settleDayBatch.parentId}, #{settleDayBatch.totalCount}, #{settleDayBatch.totalMoney}," +
            " #{settleDayBatch.accMoney}, #{settleDayBatch.collecTime}, NOW())")
    int insertSettleDayBatch(@Param("settleDayBatch") SettleDayBatch settleDayBatch);

    @Insert(" INSERT INTO agent_monthsettle_share_collect(agent_no, agent_node, parent_id, total_count, total_money, acc_money, collec_time, create_time)" +
            " VALUES(#{settleMonthBatch.agentNo}, #{settleMonthBatch.agentNode}, #{settleMonthBatch.parentId}, #{settleMonthBatch.totalCount}, #{settleMonthBatch.totalMoney}," +
            " #{settleMonthBatch.accMoney}, #{settleMonthBatch.collecTime}, NOW())")
    int insertSettleMonthBatch(@Param("settleMonthBatch") SettleMonthBatch settleMonthBatch);

    @Select(" SELECT IFNULL(COUNT(id), 0) collectDay, IFNULL(SUM(total_money), 0) collectSum, IFNULL(SUM(total_count), 0) collectCount FROM agent_daysettle_share_collect WHERE " +
            " agent_no = #{agentNo} AND collec_time >= #{beginOfMonth} AND collec_time <= #{endOfMonth} ")
    @ResultType(Map.class)
    Map<String, Object> hasCollectDay(@Param("agentNo") String agentNo, @Param("beginOfMonth") String beginOfMonth, @Param("endOfMonth") String endOfMonth);
}
