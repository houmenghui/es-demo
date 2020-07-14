package com.esdemo.modules.dao;

import com.esdemo.frame.utils.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/6/6 10:35
 */
@Mapper
public interface SuperPushDao {

    /**
     * 获取超级推分润明细记录
     */
    @SelectProvider(type = SuperPushDao.SqlProvider.class, method = "getSuperPushShareRecord")
    @ResultType(List.class)
    List<Map<String, Object>> getSuperPushShareRecord(@Param("params_map") Map<String, Object> params_map);

    /**
     * 统计超级推收益
     */
    @SelectProvider(type = SuperPushDao.SqlProvider.class, method = "getSuperPushShareCount")
    Map<String, Object> getSuperPushShareCount(@Param("params_map") Map<String, Object> params_map);

    /**
     * 获取邀请有奖商户信息记录
     */
    @SelectProvider(type = SuperPushDao.SqlProvider.class, method = "getInvPriMerInfoRecord")
    @ResultType(List.class)
    List<Map<String, Object>> getInvPriMerInfoRecord(@Param("params_map") Map<String, Object> params_map);

    /**
     * 统计邀请有奖商户记录
     */
    @SelectProvider(type = SuperPushDao.SqlProvider.class, method = "countInvPriMerInfoRecord")
    Map<String,Object> countInvPriMerInfoRecord(@Param("params_map") Map<String, Object> params_map);

    class SqlProvider {
        /**
         * 获取超级推分润明细记录
         */
        public String getSuperPushShareRecord(Map<String, Object> params_map){
            @SuppressWarnings("unchecked")
            final Map<String,Object> map = (Map<String,Object>)params_map.get("params_map");
            SQL sql = new SQL();
            sql.SELECT("sps.id,sps.order_no,sps.trans_amount,DATE_FORMAT(sps.trans_time, '%Y-%m-%d %H:%i:%S') as trans_time,sps.merchant_no,sps.mobile,sps.agent_node,sps.share_type,sps.share_no,sps.share_amount,sps.share_rate,sps.share_status,DATE_FORMAT(sps.share_time, '%Y-%m-%d %H:%i:%S') as share_time,DATE_FORMAT(sps.create_time, '%Y-%m-%d %H:%i:%S') as create_time,sps.collection_status,sps.collection_batch_no,m.merchant_name");
            sql.FROM("super_push_share sps");
            sql.INNER_JOIN("merchant_info m");
            sql.WHERE("m.merchant_no = sps.merchant_no");

            if(!"".equals(StringUtils.filterNull(map.get("agent_node")))){
                sql.WHERE(" m.parent_node like #{params_map.agent_node}\"%\"");
            }
            if(!"".equals(StringUtils.filterNull(map.get("agent_no")))){
                sql.WHERE(" sps.share_no = #{params_map.agent_no}");
            }
            sql.ORDER_BY("sps.id desc");
            return sql.toString();
        }

        /**
         * 统计超级推收益
         */
        public String getSuperPushShareCount(Map<String,Object> params_map){
            Map<String,String> map = (Map<String,String>) params_map.get("params_map");
            SQL sql = new SQL();
            sql.SELECT("count(1) count_num,sum(sps.share_amount) sum_share_amount");
            sql.FROM("super_push_share sps");
            sql.WHERE("sps.share_no = #{params_map.agent_no}");
            if(!"".equals(StringUtils.filterNull(map.get("start_time"))) && !"".equals(StringUtils.filterNull(map.get("end_time")))){
                sql.WHERE("sps.trans_time BETWEEN #{params_map.start_time} and #{params_map.end_time}");
            }
            return sql.toString();
        }

        /**
         * 获取邀请有奖商户信息记录
         */
        public String getInvPriMerInfoRecord(Map<String,Object> params_map){
            SQL sql = new SQL();
            sql.SELECT("ip.id,ip.merchant_no,ip.agent_node,ip.prizes_amount,ip.account_status,DATE_FORMAT(ip.account_time, '%Y-%m-%d %H:%i:%S') as account_time,DATE_FORMAT(ip.create_time, '%Y-%m-%d %H:%i:%S') as create_time,DATE_FORMAT(ip.update_time, '%Y-%m-%d %H:%i:%S') as update_time,ip.operator,ip.order_no,ip.prizes_type,ip.prizes_object,m.merchant_name,m.mobilephone mobile_phone" +
                    ",case" +
                    " when ip.account_status=0 then '待入账'" +
                    " when ip.account_status=1 then '已入账'" +
                    " when ip.account_status=2 then '入账失败'" +
                    " else '其他'" +
                    " end account_status_zh");
            sql.FROM("invite_prizes_merchant_info ip");
            sql.INNER_JOIN("merchant_info m on ip.merchant_no = m.merchant_no");
            //sql.WHERE("ip.agent_node like #{params.agent_node}\"%\"");
            sql.WHERE("ip.prizes_object = #{params_map.agent_no}");
            sql.ORDER_BY("ip.id desc");
            return sql.toString();
        }

        /**
         * 统计邀请有奖商户
         */
        public String countInvPriMerInfoRecord(Map<String,Object> params_map){
            SQL sql = new SQL();
            Map<String,String> map = (Map<String,String>) params_map.get("params_map");
            sql.SELECT("count(1) count_num,sum(prizes_amount) sum_prizes_amount");
            sql.FROM("invite_prizes_merchant_info");
            sql.WHERE("account_status in ('0','1') and agent_node like #{params_map.agent_node}\"%\"");
            if(!"".equals(StringUtils.filterNull(map.get("start_time"))) && !"".equals(StringUtils.filterNull(map.get("end_time")))){
                sql.WHERE("create_time BETWEEN #{params_map.start_time} and #{params_map.end_time}");
            }
            return sql.toString();
        }

    }


}
