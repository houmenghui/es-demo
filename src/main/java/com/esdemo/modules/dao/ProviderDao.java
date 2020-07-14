package com.esdemo.modules.dao;

import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.ProviderBean;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description
 * @date 2019/5/20
 */
@Mapper
public interface ProviderDao {

    @SelectProvider(type = SqlProvider.class, method = "checkAgentNoIsDirectChildren")
    int checkAgentNoIsDirectChildren(@Param("list") List<String> agentNoList, @Param("loginAgent") AgentInfo loginAgent, @Param("type") String type);

    @InsertProvider(type = SqlProvider.class, method = "openOemServiceCost")
    void openOemServiceCost(@Param("list") List<ProviderBean> wantAddAgent, @Param("type") String type);

    @Select("SELECT agent_no,rate,single_amount,full_repay_rate,full_repay_single_amount," +
            "perfect_repay_rate,perfect_repay_single_amount,nfc_orig_code" +
            " FROM yfb_service_cost WHERE service_type = #{type} AND agent_no = #{agentNo}")
    ProviderBean queryServiceCost(@Param("agentNo") String agentNo, @Param("type") String type);

    @Select("SELECT agent_no, " +
            "trade_fee_rate rate,trade_single_fee singleAmount," +
            "full_repay_fee_rate fullRepayRate,full_repay_single_fee fullRepaySingleAmount," +
            "perfect_repay_fee_rate perfectRepayRate,perfect_repay_single_fee perfectRepaySingleAmount,common_code_url commonCodeUrl" +
            " FROM yfb_oem_service WHERE agent_no = #{agentNo} AND oem_type = #{type}")
    ProviderBean queryOemServiceCost(@Param("agentNo") String agentNo, @Param("type") String type);

    @Select("SELECT COUNT(1) FROM yfb_activation_code " +
            "WHERE nfc_orig_code =  #{nfcOrigCode} " +
            "AND agent_no = #{agentNo} " +
            "AND can_use = '1' ")
    int countParentCode(@Param("nfcOrigCode") String nfcOrigCode, @Param("agentNo") String agentNo);

    @Select("SELECT COUNT(1) FROM yfb_activation_code " +
            "WHERE nfc_orig_code =  #{nfcOrigCode} " +
            "AND agent_no = #{agentNo} " +
            "AND status = '2' " +
            "AND can_use = '1' ")
    int countUsedParentCode(@Param("nfcOrigCode") String nfcOrigCode, @Param("agentNo") String agentNo);


    class SqlProvider {
        public String checkAgentNoIsDirectChildren(Map<String, Object> param) {
            final List<String> agentNoList = (List<String>) param.get("list");
            SQL sql = new SQL() {{
                SELECT("count(*)");
                FROM("agent_info ai");
                LEFT_OUTER_JOIN("yfb_service_cost ysc ON ai.agent_no = ysc.agent_no and ysc.service_type = #{type}");
                WHERE("ai.parent_id = #{loginAgent.agentNo}");  // 父级是登陆代理商
                WHERE("ysc.agent_no is null");      // 且在yfb_service_cost找不到记录
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < agentNoList.size(); i++) {
                    sb.append("#{list[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                WHERE("ai.agent_no in (" + sb.toString() + ")");
            }};
            return sql.toString();
        }

        public String openOemServiceCost(Map<String, Object> param) {
            List<ProviderBean> agentList = (List<ProviderBean>) param.get("list");
            StringBuilder sb = new StringBuilder();
            sb.append("REPLACE INTO yfb_service_cost(agent_no, rate, single_amount, service_type,full_repay_rate,full_repay_single_amount, nfc_orig_code) values ");
            for (int i = 0; i < agentList.size(); i++) {
                sb.append("(#{list[" + i + "].agentNo}, #{list[" + i + "].rate}, #{list[" + i + "].singleAmount}, #{type}, #{list[" + i + "].fullRepayRate}, #{list[" + i + "].fullRepaySingleAmount}, #{list[" + i + "].nfcOrigCode}),");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            return sb.toString();
        }
    }
}
