package com.esdemo.modules.dao;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.AgentInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 机具申请
 * @date 2019/5/24
 */
@Mapper
public interface TerminalApplyRecordDao {

    AgentInfo getAgentInfoByNo(@Param("agentNO")String agentNO);


    /**
     * 统计代理商名下机具申请记录数
     * @param agentNode
     * @return
     */
    int countTerminalApplyRecord(@Param("agentNode") String agentNode);

    /**
     * 分配机具至指定代理商名下
     * @param params
     * @return
     * @author ZengJA
     * @date 2017-10-20 15:27:42
     */
    Integer distributionTerminalInfo(Map<String,String> params);

    /**
     * 单表-查询商户信息
     * @param params
     * @return
     * @author ZengJA
     * @date 2017-10-26 09:40:30
     */
    @SelectProvider(method = "getMerInfo", type = SqlProvider.class)
    Map<String,Object> getMerInfo(Map<String, String> params);

    /**
     * 单表-查询机具信息
     * @param params
     * @return
     */
    @SelectProvider(method = "getTerminalInfo", type = SqlProvider.class)
    Map<String,Object> getTerminalInfo(Map<String, String> params);

    /**
     * 获取机具申请记录
     * @author ZengJA
     * @date 2017-07-28 14:49:55
     */
    @SelectProvider(method = "getTerminalApplyRecord", type = SqlProvider.class)
    List<Map<String,Object>> getTerminalApplyRecord(@Param("params") Map<String, String> params );

    /**
     * 查询所有活动（在数据字典表中）
     * @return
     */
    @Select("select sys_name activity_name,sys_value activity_value,`status` activity_status,remark activity_remark from sys_dict where sys_key = 'ACTIVITY_TYPE' and parent_id = 'ACTIVITY_TYPE'")
    List<Map<String,Object>> getAllActivityInfo();

    /**
     *
     * @param params
     * @return
     */
    @UpdateProvider(method = "updateModifyTerminalApplyRecord", type = SqlProvider.class)
    Integer updateModifyTerminalApplyRecord(Map<String,String> params);

    class SqlProvider{

        /**
         * 查询商户-单表
         * @param params
         * @return
         * @author ZengJA
         * @date 2017-10-26 09:40:38
         */
        public String getMerInfo(Map<String,String> params){
            StringBuilder sql = new StringBuilder("select * from merchant_info where 1=1");
            if (StringUtils.isNotBlank(params.get("address"))) {sql.append(" and address = #{address}");}
            if (StringUtils.isNotBlank(params.get("agent_no"))) {sql.append(" and agent_no = #{agent_no}");}
            if (StringUtils.isNotBlank(params.get("bonus_flag"))) {sql.append(" and bonus_flag = #{bonus_flag}");}
            if (StringUtils.isNotBlank(params.get("business_type"))) {sql.append(" and business_type = #{business_type}");}
            if (StringUtils.isNotBlank(params.get("city"))) {sql.append(" and city = #{city}");}
            if (StringUtils.isNotBlank(params.get("create_time"))) {sql.append(" and create_time = #{create_time}");}
            if (StringUtils.isNotBlank(params.get("creator"))) {sql.append(" and creator = #{creator}");}
            if (StringUtils.isNotBlank(params.get("district"))) {sql.append(" and district = #{district}");}
            if (StringUtils.isNotBlank(params.get("email"))) {sql.append(" and email = #{email}");}
            if (StringUtils.isNotBlank(params.get("id"))) {sql.append(" and id = #{id}");}
            if (StringUtils.isNotBlank(params.get("id_card_no"))) {sql.append(" and id_card_no = #{id_card_no}");}
            if (StringUtils.isNotBlank(params.get("industry_type"))) {sql.append(" and industry_type = #{industry_type}");}
            if (StringUtils.isNotBlank(params.get("last_update_time"))) {sql.append(" and last_update_time = #{last_update_time}");}
            if (StringUtils.isNotBlank(params.get("lawyer"))) {sql.append(" and lawyer = #{lawyer}");}
            if (StringUtils.isNotBlank(params.get("mender"))) {sql.append(" and mender = #{mender}");}
            if (StringUtils.isNotBlank(params.get("merchant_name"))) {sql.append(" and merchant_name = #{merchant_name}");}
            if (StringUtils.isNotBlank(params.get("merchant_no"))) {sql.append(" and merchant_no = #{merchant_no}");}
            if (StringUtils.isNotBlank(params.get("merchant_type"))) {sql.append(" and merchant_type = #{merchant_type}");}
            if (StringUtils.isNotBlank(params.get("mer_account"))) {sql.append(" and mer_account = #{mer_account}");}
            if (StringUtils.isNotBlank(params.get("mobilephone"))) {sql.append(" and mobilephone = #{mobilephone}");}
            if (StringUtils.isNotBlank(params.get("one_agent_no"))) {sql.append(" and one_agent_no = #{one_agent_no}");}
            if (StringUtils.isNotBlank(params.get("operator"))) {sql.append(" and operator = #{operator}");}
            if (StringUtils.isNotBlank(params.get("parent_node"))) {sql.append(" and parent_node = #{parent_node}");}
            if (StringUtils.isNotBlank(params.get("pre_frozen_amount"))) {sql.append(" and pre_frozen_amount = #{pre_frozen_amount}");}
            if (StringUtils.isNotBlank(params.get("province"))) {sql.append(" and province = #{province}");}
            if (StringUtils.isNotBlank(params.get("push_flag"))) {sql.append(" and push_flag = #{push_flag}");}
            if (StringUtils.isNotBlank(params.get("recommended_source"))) {sql.append(" and recommended_source = #{recommended_source}");}
            if (StringUtils.isNotBlank(params.get("register_source"))) {sql.append(" and register_source = #{register_source}");}
            if (StringUtils.isNotBlank(params.get("remark"))) {sql.append(" and remark = #{remark}");}
            if (StringUtils.isNotBlank(params.get("risk_status"))) {sql.append(" and risk_status = #{risk_status}");}
            if (StringUtils.isNotBlank(params.get("sale_name"))) {sql.append(" and sale_name = #{sale_name}");}
            if (StringUtils.isNotBlank(params.get("source_reference_no"))) {sql.append(" and source_reference_no = #{source_reference_no}");}
            if (StringUtils.isNotBlank(params.get("source_sys"))) {sql.append(" and source_sys = #{source_sys}");}
            if (StringUtils.isNotBlank(params.get("status"))) {sql.append(" and status = #{status}");}
            if (StringUtils.isNotBlank(params.get("team_id"))) {sql.append(" and team_id = #{team_id}");}
            if (StringUtils.isNotBlank(params.get("voice_flag"))) {sql.append(" and voice_flag = #{voice_flag}");}
            if (sql.toString().endsWith("1=1")) {sql.append(" and 1=2");}
            sql.append(" limit 1");
            return sql.toString();
        }

        /**
         * 查询机具信息
         * @param params
         * @return
         * @author ZengJA
         * @date 2017-10-20 15:27:29
         */
        public String getTerminalInfo(Map<String,String> params){
            StringBuilder sql = new StringBuilder("select * from terminal_info where 1=1");
            if (StringUtils.isNotBlank(params.get("activity_type"))) {sql.append(" and activity_type = #{activity_type}");}
            if (StringUtils.isNotBlank(params.get("agent_no"))) {sql.append(" and agent_no = #{agent_no}");}
            if (StringUtils.isNotBlank(params.get("agent_node"))) {sql.append(" and agent_node = #{agent_node}");}
            if (StringUtils.isNotBlank(params.get("allot_batch"))) {sql.append(" and allot_batch = #{allot_batch}");}
            if (StringUtils.isNotBlank(params.get("batch_no"))) {sql.append(" and batch_no = #{batch_no}");}
            if (StringUtils.isNotBlank(params.get("bp_id"))) {sql.append(" and bp_id = #{bp_id}");}
            if (StringUtils.isNotBlank(params.get("cashier_no"))) {sql.append(" and cashier_no = #{cashier_no}");}
            if (StringUtils.isNotBlank(params.get("collection_code"))) {sql.append(" and collection_code = #{collection_code}");}
            if (StringUtils.isNotBlank(params.get("CREATE_TIME"))) {sql.append(" and CREATE_TIME = #{CREATE_TIME}");}
            if (StringUtils.isNotBlank(params.get("id"))) {sql.append(" and id = #{id}");}
            if (StringUtils.isNotBlank(params.get("last_check_in_time"))) {sql.append(" and last_check_in_time = #{last_check_in_time}");}
            if (StringUtils.isNotBlank(params.get("merchant_no"))) {sql.append(" and merchant_no = #{merchant_no}");}
            if (StringUtils.isNotBlank(params.get("model"))) {sql.append(" and model = #{model}");}
            if (StringUtils.isNotBlank(params.get("need_check"))) {sql.append(" and need_check = #{need_check}");}
            if (StringUtils.isNotBlank(params.get("open_status"))) {sql.append(" and open_status = #{open_status}");}
            if (StringUtils.isNotBlank(params.get("pos_type"))) {sql.append(" and pos_type = #{pos_type}");}
            if (StringUtils.isNotBlank(params.get("PSAM_NO"))) {sql.append(" and PSAM_NO = #{PSAM_NO}");}
            if (StringUtils.isNotBlank(params.get("recommended_source"))) {sql.append(" and recommended_source = #{recommended_source}");}
            if (StringUtils.isNotBlank(params.get("serial_no"))) {sql.append(" and serial_no = #{serial_no}");}
            if (StringUtils.isNotBlank(params.get("single_share_amount"))) {sql.append(" and single_share_amount = #{single_share_amount}");}
            if (StringUtils.isNotBlank(params.get("SN"))) {sql.append(" and SN = #{SN}");}
            if (StringUtils.isNotBlank(params.get("START_TIME"))) {sql.append(" and START_TIME = #{START_TIME}");}
            if (StringUtils.isNotBlank(params.get("terminal_id"))) {sql.append(" and terminal_id = #{terminal_id}");}
            if (StringUtils.isNotBlank(params.get("terminal_name"))) {sql.append(" and terminal_name = #{terminal_name}");}
            if (StringUtils.isNotBlank(params.get("tmk"))) {sql.append(" and tmk = #{tmk}");}
            if (StringUtils.isNotBlank(params.get("tmk_tak"))) {sql.append(" and tmk_tak = #{tmk_tak}");}
            if (StringUtils.isNotBlank(params.get("tmk_tpk"))) {sql.append(" and tmk_tpk = #{tmk_tpk}");}
            if (StringUtils.isNotBlank(params.get("type"))) {sql.append(" and type = #{type}");}
            if (sql.toString().endsWith("1=1")) {sql.append(" and 1=2");}
            return sql.toString();
        }
        /**
         * 获取机具申请记录
         * @param params 查询参数
         * @return
         * @author ZengJA
         * @date 2017-07-28 14:47:29
         */
        public String getTerminalApplyRecord(Map<String,Object> params){
            Map<String,String> map = (Map<String,String>) params.get("params");
            SQL sql = new SQL();
            sql.SELECT("ta.*,ta.SN sn " +
                    ",pa.agent_no agent_no_parent,pa.agent_name agent_name_parent" +
                    ",a.agent_no agent_no_one,a.agent_name agent_name_one" +
                    ",m.merchant_name,m.merchant_type,m.business_type,hp.type_name bp_name");
            sql.FROM("terminal_apply ta");
            sql.INNER_JOIN("merchant_info m on ta.merchant_no = m.merchant_no");
            sql.LEFT_OUTER_JOIN("hardware_product hp on ta.product_type = hp.hp_id");
            sql.LEFT_OUTER_JOIN("agent_info a on a.agent_no = m.one_agent_no");
            sql.LEFT_OUTER_JOIN("agent_info pa on pa.agent_node = m.parent_node");

            if(StringUtils.isNotBlank(map.get("agent_node"))){
                sql.WHERE(" m.parent_node like #{params.agent_node}\"%\"");
            }
            String record_status = map.get("record_status");
            if("DCL".equalsIgnoreCase(record_status)){//待处理
                sql.WHERE("(ta.status = '0' or ta.status='2')");
            }else if("YCL".equalsIgnoreCase(record_status)){//已处理
                sql.WHERE("ta.status = '1'");
            }
            sql.ORDER_BY("DCL".equalsIgnoreCase(record_status) ? "ta.id" : "ta.id desc");//如果是待处理，则按先进先出原则，反之按先进后出原则。
            return sql.toString();
        }

        /**
         * 修改机具申请记录<br>
         * 1.强制要求根据记录ID进行修改<br>
         * 2.强制要求验证当前代理商是否是这个申请商户的上级代理商或直属代理商<br>
         * @param params
         * @return
         */
        public String updateModifyTerminalApplyRecord(Map<String,String> params){
            SQL sql = new SQL();
            sql.UPDATE("terminal_apply");
            sql.SET("status=#{record_status}");
            sql.SET("remark=#{record_remark}");
            if (StringUtils.isNotBlank(StringUtils.filterNull(params.get("SN")))) {
                sql.SET("sn=#{SN}");
            }
            sql.WHERE("id=#{record_id} and merchant_no in (select merchant_no from merchant_info where parent_node like #{agent_node}\"%\")");
            return sql.toString();
        }
    }
}
