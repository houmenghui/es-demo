package com.esdemo.modules.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface CollectDao {


    @Select(" select sum(collectCount) collectCount ,sum(collectSum) collectSum from (  SELECT IFNULL(COUNT(id), 0) AS collectCount, IFNULL(SUM(cash_back_amount), 0) AS collectSum FROM cash_back_detail  WHERE agent_no = #{agentNo} AND entry_time >= #{beginTime}  AND entry_time <= #{endTime}  and entry_status = '1' and amount_type != '3'  union all SELECT IFNULL(COUNT(id), 0) AS collectCount, IFNULL(SUM(-cash_back_amount), 0) AS collectSum FROM cash_back_detail  WHERE agent_no = #{agentNo} AND entry_time >= #{beginTime}  AND entry_time <= #{endTime}  and entry_status = '1' and amount_type = '3'  union all SELECT IFNULL(COUNT(id), 0) AS collectCount, IFNULL(SUM(amount), 0) AS collectSum FROM xhlf_agent_account_detail  WHERE agent_no = #{agentNo}  AND account_time >= #{beginTime}  AND account_time <=  #{endTime} and account_status = '1'   UNION ALL  SELECT IFNULL(COUNT(id), 0) AS collectCount, IFNULL(SUM(-amount), 0) AS collectSum FROM xhlf_agent_deduction_account_detail WHERE agent_no = #{agentNo} AND account_time >= #{beginTime}AND account_time <= #{endTime}  AND account_status in ('2','3')     ) c")
    @ResultType(Map.class)
    Map<String, Object> collectCashBackByAgentAndTime(@Param("agentNo") String agentNo, @Param("beginTime") String beginTime, @Param("endTime") String endTime);


    @Select(" select IFNULL(COUNT(cto.id), 0) AS collectCount, IFNULL(SUM(cto.profits_${agentLevel}), 0) AS collectSum, IFNULL(SUM(cto.trans_amount), 0) collectSumTrans from collective_trans_order cto \n" +
            " INNER JOIN merchant_info mi on mi.merchant_no = cto.merchant_no " +
            " where cto.trans_time >= #{beginTime} " +
            " and cto.trans_time <=  #{endTime} " +
            " and cto.trans_status = 'SUCCESS' " +
            " and mi.parent_node like #{agentNode} ")
    @ResultType(Map.class)
    Map<String, Object> collectTransByAgentAndTime(@Param("agentNode") String agentNode, @Param("agentLevel") String agentLevel, @Param("beginTime") String beginTime, @Param("endTime") String endTime);


    @Select(" SELECT IFNULL(COUNT(st.id), 0) AS collectCount, IFNULL(SUM(st.profits_${agentLevel}), 0) AS collectSum FROM settle_transfer st " +
            " INNER JOIN merchant_info mi ON mi.merchant_no = st.settle_user_no " +
            " WHERE st.status = '4' AND st.create_time >= #{beginTime} AND st.create_time <= #{endTime} AND st.settle_user_type = 'M' " +
            " AND mi.parent_node LIKE #{agentNode}")
    @ResultType(Map.class)
    Map<String, Object> collectSettleByAgentAndTime(@Param("agentNode") String agentNode, @Param("agentLevel") String agentLevel,
                                                    @Param("beginTime") String beginTime, @Param("endTime") String endTime);
}
