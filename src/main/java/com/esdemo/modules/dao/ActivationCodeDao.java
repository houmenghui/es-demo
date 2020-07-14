package com.esdemo.modules.dao;

import com.esdemo.frame.enums.ActCodeQueryRange;
import com.esdemo.frame.enums.ActCodeQueryType;
import com.esdemo.frame.enums.PublicCode;
import com.esdemo.modules.bean.ActCodeQueryBean;
import com.esdemo.modules.bean.ActivationCodeBean;
import com.esdemo.modules.bean.AgentInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author zhangly
 * @description
 * @date 2019/5/20
 */
@Mapper
public interface ActivationCodeDao {

    /**
     * 分页获取Nfc激活码信息
     *
     * @param queryBean
     * @return
     */
    @SelectProvider(type = SqlProvider.class, method = "listNfcActivationCode")
    List<ActivationCodeBean> listNfcActivationCode(@Param("queryBean") ActCodeQueryBean queryBean, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 查询激活码详情
     *
     * @param codeId
     * @return
     */
    @Select("SELECT yac.id, yac.uuid_code,yac.unified_merchant_no,yac.agent_no,yac.agent_node,yac.status,yac.activate_time,yac.create_time,yac.update_time,yac.nfc_orig_code" +
            " FROM yfb_activation_code yac WHERE yac.id = #{codeId}")
    @ResultType(ActivationCodeBean.class)
    ActivationCodeBean getActivationCodeById(@Param("codeId") String codeId);

    /**
     * 下发NFC激活码信息
     *
     * @param queryBean
     * @param operateAgentInfo
     * @return
     */
    @UpdateProvider(type = SqlProvider.class, method = "allotNfcActivationCode2Agent")
    @ResultType(Long.class)
    long allotNfcActivationCode2Agent(@Param("queryBean") ActCodeQueryBean queryBean, @Param("operateAgentInfo") AgentInfo operateAgentInfo, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 回收NFC激活码
     *
     * @param queryBean
     * @return
     */
    @UpdateProvider(type = SqlProvider.class, method = "recoveryNfcActivation")
    @ResultType(Long.class)
    long recoveryNfcActivation(@Param("queryBean") ActCodeQueryBean queryBean, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 统计可回收NFC激活码
     *
     * @param queryBean
     * @return
     */
    @SelectProvider(type = SqlProvider.class, method = "countRecoveryNfcActivation")
    @ResultType(Long.class)
    long countRecoveryNfcActivation(@Param("queryBean") ActCodeQueryBean queryBean, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 分配母码
     *
     * @param queryBean
     * @return
     */
    @UpdateProvider(type = SqlProvider.class, method = "assignParentCode")
    @ResultType(Long.class)
    long assignParentCode(@Param("queryBean") ActCodeQueryBean queryBean, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 回收母码
     *
     * @param queryBean
     * @return
     */
    @UpdateProvider(type = SqlProvider.class, method = "recoveryParentCode")
    @ResultType(Long.class)
    long recoveryParentCode(@Param("queryBean") ActCodeQueryBean queryBean, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 统计可回收母码数量
     *
     * @param queryBean
     * @return
     */
    @SelectProvider(type = SqlProvider.class, method = "countRecoveryParentCode")
    @ResultType(Long.class)
    long countRecoveryParentCode(@Param("queryBean") ActCodeQueryBean queryBean, @Param("loginAgentInfo") AgentInfo loginAgentInfo);

    /**
     * 根据超级还商户号获取V2商户信息
     *
     * @param repayMerNo
     * @return
     */
    @Select("select mi.merchant_name,mi.merchant_no from merchant_info mi LEFT JOIN yfb_unified_account_product uap ON(uap.pro_code = 'gatherService' AND uap.pro_mer_no = mi.merchant_no) " +
            "LEFT JOIN yfb_unified_account_product uap2 ON(uap2.un_account_mer_no = uap.un_account_mer_no) " +
            "WHERE uap2.pro_code = 'repay' and uap2.pro_mer_no = #{repayMerNo};")
    @ResultType(Map.class)
    Map<String, Object> getV2MerInfoByRepayMerNo(@Param("repayMerNo") String repayMerNo);

    /**
     * 根据代理商编号和母码获取激活码对象
     *
     * @param agentNo     代理商编号
     * @param nfcOrigCode 母码
     * @return
     */
    @Select("SELECT yac.id, yac.uuid_code,yac.unified_merchant_no,yac.agent_no,yac.agent_node,yac.status,yac.activate_time,yac.create_time,yac.update_time,yac.nfc_orig_code" +
            " FROM yfb_activation_code yac WHERE yac.nfc_orig_code = #{nfcOrigCode} AND yac.agent_no = #{agentNo} AND yac.code_type = 'nfc'")
    @ResultType(ActivationCodeBean.class)
    ActivationCodeBean getActivationCodeByAgentNoAndNfcOrigCode(@Param("agentNo") String agentNo, @Param("nfcOrigCode") String nfcOrigCode);

    class SqlProvider {
        /**
         * 分页获取激活码信息
         *
         * @param param
         * @return
         */
        public String listNfcActivationCode(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                SELECT("yac.id, yac.uuid_code,yac.unified_merchant_no," +
                        "yac.agent_no,yac.agent_node,ai.agent_name,ai.parent_id," +
                        "yac.status,yac.activate_time," +
                        "yac.create_time,yac.update_time, yac.nfc_orig_code");
                FROM("yfb_activation_code yac");
                LEFT_OUTER_JOIN("agent_info ai ON ai.agent_no = yac.agent_no");
            }};
            listWhereStr(sql, queryBean, loginAgentInfo);
            return sql.toString();
        }

        /**
         * 下发激活码信息
         *
         * @param param
         * @return
         */
        public String allotNfcActivationCode2Agent(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo operateAgentInfo = (AgentInfo) param.get("operateAgentInfo");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                UPDATE("yfb_activation_code yac");
                SET("yac.agent_no = #{operateAgentInfo.agentNo}");
                SET("yac.agent_node = #{operateAgentInfo.agentNode}");
            }};
            sql.WHERE("yac.code_type = 'nfc'");
            sql.WHERE("yac.status = '1'");
            sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{loginAgentInfo.agentNo}");
            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }
            long[] idArray = queryBean.getIdArray();
            if (idArray != null && idArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length; i++) {
                    sb.append("#{queryBean.idArray[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sql.WHERE("yac.id in (" + sb.toString() + ")");
            }
            return sql.toString();
        }

        /**
         * 回收NFC激活码
         *
         * @param param
         * @return
         */
        public String recoveryNfcActivation(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                UPDATE("yfb_activation_code yac");
                SET("yac.agent_no = #{loginAgentInfo.agentNo}");
                SET("yac.agent_node = #{loginAgentInfo.agentNode}");
                SET("yac.nfc_orig_code = NULL");
            }};
            sql.WHERE("yac.code_type = 'nfc'");
            sql.WHERE("yac.status = '1'");
            if (StringUtils.isNotBlank(queryBean.getActCodeStatus())) {
                sql.WHERE("yac.status = #{queryBean.actCodeStatus}");
            }

            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }
            if(ActCodeQueryType.ALL.getType().equals(queryBean.getQueryType())){
                if (ActCodeQueryRange.MY.getRange().equals(queryBean.getQueryRange()) && StringUtils.isNotBlank(queryBean.getAgentNo())) {
                    sql.WHERE("yac.agent_no =  #{queryBean.agentNo}");
                } else if(StringUtils.isNotBlank(queryBean.getAgentNode())) {
                    sql.WHERE("yac.agent_node like concat(#{queryBean.agentNode},'%')");
                }
            }
            if (StringUtils.isNotBlank(queryBean.getIsAddPublic())) {
                if (PublicCode.YES.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
                }
                if (PublicCode.NOT.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{queryBean.agentNo}");
                }
            }
            if (StringUtils.isNotBlank(queryBean.getMerchantNo())) {
                String[] repayMerNos = queryBean.getRepayMerNos();
                if (repayMerNos != null && repayMerNos.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < repayMerNos.length; i++) {
                        sb.append("#{queryBean.repayMerNos[" + i + "]},");
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    sql.WHERE("yac.unified_merchant_no in (" + sb.toString() + ")");
                }
            }
            sql.WHERE("EXISTS (SELECT 1 FROM agent_info ai " +
                    "   WHERE ai.agent_no = yac.agent_no " +
                    "   AND ai.parent_id = #{loginAgentInfo.agentNo})");
            long[] idArray = queryBean.getIdArray();
            if (idArray != null && idArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length; i++) {
                    sb.append("#{queryBean.idArray[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sql.WHERE("yac.id in (" + sb.toString() + ")");
            }
            return sql.toString();
        }

        /**
         * 统计可回收NFC激活码数量
         *
         * @param param
         * @return
         */
        public String countRecoveryNfcActivation(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                SELECT("COUNT(yac.id) AS canRecoveryNfcActivationCount");
                FROM("yfb_activation_code yac");
            }};
            sql.WHERE("yac.code_type = 'nfc'");
            sql.WHERE("yac.status = '1'");
            if (StringUtils.isNotBlank(queryBean.getActCodeStatus())) {
                sql.WHERE("yac.status = #{queryBean.actCodeStatus}");
            }
            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }
            if(ActCodeQueryType.ALL.getType().equals(queryBean.getQueryType())){
                if (ActCodeQueryRange.MY.getRange().equals(queryBean.getQueryRange()) && StringUtils.isNotBlank(queryBean.getAgentNo())) {
                    sql.WHERE("yac.agent_no =  #{queryBean.agentNo}");
                } else if(StringUtils.isNotBlank(queryBean.getAgentNode())) {
                    sql.WHERE("yac.agent_node like concat(#{queryBean.agentNode},'%')");
                }
            }
            if (StringUtils.isNotBlank(queryBean.getIsAddPublic())) {
                if (PublicCode.YES.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
                }
                if (PublicCode.NOT.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{queryBean.agentNo}");
                }
            }
            long[] idArray = queryBean.getIdArray();
            if (idArray != null && idArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length; i++) {
                    sb.append("#{queryBean.idArray[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sql.WHERE("yac.id in (" + sb.toString() + ")");
            }
            if (StringUtils.isNotBlank(queryBean.getMerchantNo())) {
                String[] repayMerNos = queryBean.getRepayMerNos();
                if (repayMerNos != null && repayMerNos.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < repayMerNos.length; i++) {
                        sb.append("#{queryBean.repayMerNos[" + i + "]},");
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    sql.WHERE("yac.unified_merchant_no in (" + sb.toString() + ")");
                }
            }
            sql.WHERE("EXISTS (SELECT 1 FROM agent_info ai " +
                    "   WHERE ai.agent_no = yac.agent_no " +
                    "   AND ai.parent_id = #{loginAgentInfo.agentNo})");
            return sql.toString();
        }

        /**
         * 分配母码
         *
         * @param param
         * @return
         */
        public String assignParentCode(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                UPDATE("yfb_activation_code yac");
                SET("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
            }};
            sql.WHERE("yac.code_type = 'nfc'");
            sql.WHERE("yac.status = '1'");
            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }
            sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{loginAgentInfo.agentNo}");
            long[] idArray = queryBean.getIdArray();
            if (idArray != null && idArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length; i++) {
                    sb.append("#{queryBean.idArray[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sql.WHERE("yac.id in (" + sb.toString() + ")");
            }
            return sql.toString();
        }

        /**
         * 回收母码
         *
         * @param param
         * @return
         */
        public String recoveryParentCode(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                UPDATE("yfb_activation_code yac");
                SET("yac.nfc_orig_code = NULL");
            }};
            sql.WHERE("yac.code_type = 'nfc'");
            sql.WHERE("yac.status = '1'");
            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }
            sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
            sql.WHERE("yac.agent_no = #{loginAgentInfo.agentNo}");
            long[] idArray = queryBean.getIdArray();
            if (idArray != null && idArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length; i++) {
                    sb.append("#{queryBean.idArray[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sql.WHERE("yac.id in (" + sb.toString() + ")");
            }
            if (StringUtils.isNotBlank(queryBean.getIsAddPublic())) {
                if (PublicCode.YES.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
                }
                if (PublicCode.NOT.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{loginAgentInfo.agentNo}");
                }
            }
            return sql.toString();
        }

        /**
         * 统计可回收母码数量
         *
         * @param param
         * @return
         */
        public String countRecoveryParentCode(Map<String, Object> param) {
            final ActCodeQueryBean queryBean = (ActCodeQueryBean) param.get("queryBean");
            final AgentInfo loginAgentInfo = (AgentInfo) param.get("loginAgentInfo");
            SQL sql = new SQL() {{
                SELECT("COUNT(yac.id) AS canRecoveryParentCodeCount");
                FROM("yfb_activation_code yac");
            }};
            sql.WHERE("yac.code_type = 'nfc'");
            sql.WHERE("yac.status = '1'");
            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }
            sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
            sql.WHERE("yac.agent_no = #{loginAgentInfo.agentNo}");
            if (StringUtils.isNotBlank(queryBean.getIsAddPublic())) {
                if (PublicCode.YES.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
                }
                if (PublicCode.NOT.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{loginAgentInfo.agentNo}");
                }
            }
            return sql.toString();
        }

        /**
         * 列表查询条件
         * @param sql
         * @param queryBean
         * @param loginAgentInfo
         */
        private static void listWhereStr(SQL sql, ActCodeQueryBean queryBean, AgentInfo loginAgentInfo) {
            sql.WHERE("yac.code_type = 'nfc'");

            if (StringUtils.isNotBlank(queryBean.getActCodeStatus())) {
                sql.WHERE("yac.status = #{queryBean.actCodeStatus}");
            }

            if (StringUtils.isNotBlank(queryBean.getQueryType())) {
                if (ActCodeQueryType.UN_USE.getType().equals(queryBean.getQueryType())) {
                    sql.WHERE("yac.status = '1'");
                    sql.WHERE("yac.agent_no =  #{queryBean.agentNo}");
                } else {
                    sql.WHERE("yac.status IN ('1', '2')");
                    //判断查询范围，默认查询全部
                    if (ActCodeQueryRange.MY.getRange().equals(queryBean.getQueryRange())) {
                        sql.WHERE("yac.agent_no =  #{queryBean.agentNo}");
                    } else {
                        sql.WHERE("yac.agent_node like concat(#{queryBean.agentNode},'%')");
                    }
                }
            }

            if (StringUtils.isNotBlank(queryBean.getBeginId())) {
                sql.WHERE("yac.id >= #{queryBean.beginId}");
            }
            if (StringUtils.isNotBlank(queryBean.getEndId())) {
                sql.WHERE("yac.id <= #{queryBean.endId}");
            }

            if (StringUtils.isNotBlank(queryBean.getIsAddPublic())) {
                //查询下级代理商时，并且是全部激活码时，不能看到通用码
                if(ActCodeQueryType.ALL.getType().equals(queryBean.getQueryType()) && !loginAgentInfo.getAgentNo().equals(queryBean.getAgentNo())){
                    sql.WHERE("1 = 2");
                }
                if (PublicCode.YES.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code = #{queryBean.nfcOrigCode}");
                }
                if (PublicCode.NOT.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
                    sql.WHERE("yac.nfc_orig_code is null and yac.agent_no = #{queryBean.agentNo}");
                }
            }

            if (StringUtils.isNotBlank(queryBean.getMerchantNo())) {
                String[] repayMerNos = queryBean.getRepayMerNos();
                if (repayMerNos != null && repayMerNos.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < repayMerNos.length; i++) {
                        sb.append("#{queryBean.repayMerNos[" + i + "]},");
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    sql.WHERE("yac.unified_merchant_no in (" + sb.toString() + ")");
                }
            }

            long[] idArray = queryBean.getIdArray();
            if (idArray != null && idArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idArray.length; i++) {
                    sb.append("#{queryBean.idArray[" + i + "]},");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sql.WHERE("yac.id in (" + sb.toString() + ")");
            }
        }
    }
}
