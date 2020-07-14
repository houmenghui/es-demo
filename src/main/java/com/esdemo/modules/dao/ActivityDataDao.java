package com.esdemo.modules.dao;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.ActivityAndDataQueryBean;
import com.esdemo.modules.bean.ActivityDataDetail;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface ActivityDataDao {


    @Select(" select aa.*,aht.activity_type_name , aht.merchant_reward_amount ,aht.merchant_repeat_reward_amount,aht.one_limit_days,aht.two_limit_days ,aht.three_limit_days,aht.four_limit_days,aht.deduction_limit_days,aht.merchant_limit_days  from agent_activity aa   left JOIN activity_hardware_type aht on aa.activity_type_no= aht.activity_type_no where aht.activity_code = '009' and aa.agent_no = #{agentNo} and aa.sub_type = #{subType} ")
    List<Map<String,Object>> agentActivityBySubType(@Param("agentNo") String agentNo, @Param("subType") String subType);


    @Select(" select * from activity_mer_count_config where status = '1' and activity_mer_count_type = #{activityMerCountType} and activity_mer_count_subtype is not null order by sort desc  , update_time desc  ")
    List<Map<String, Object>> activityMerCountConfigByActType(@Param("activityMerCountType") String activityMerCountType);

    @Select(" select * from activity_mer_count_config where status = '1' and activity_mer_count_type = #{activityMerCountType} and activity_mer_count_subtype is null limit 1   ")
    Map<String, Object> activityMerCountConfigByStatus(@Param("activityMerCountType") String activityMerCountType);

    @Select(" select * from activity_mer_count_config where status = '1' and activity_mer_count_subtype is not null  order by sort desc  ")
    List<Map<String, Object>> activityMerCountConfig();

    @Select( " SELECT" +
            " case when deduction_account_status = '0' then '考核中'" +
            " when deduction_account_status in ('1','2','3') then '需扣款' " +
            " when deduction_account_status = '4' then '无需扣款' end activityDataStatus , " +
            " xao.merchant_no merchantNo," +
            " mi.merchant_name merchantName," +
            " deduction_target_amount reachStandAmount," +
            " ( SELECT" +
            " IFNULL(sum(xmttd.total_amount), 0) transAmount" +
            " FROM" +
            " xhlf_merchant_trans_total_day xmttd" +
            " WHERE  xmttd.merchant_no = xao.merchant_no" +
            " AND  xmttd.type = '2'" +
            " AND CONCAT(  xmttd.total_day," +
            " ' 23:59:59' ) BETWEEN xao.deduction_start_time" +
            " AND xao.deduction_end_time ) transAmount," +
            " DATE_FORMAT( xao.deduction_end_time,'%Y-%m-%d %H:%i:%s') activityEndTime," +
            " xao.agent_trans_total_type transType FROM" +
            " xhlf_activity_order xao" +
            " LEFT JOIN activity_detail ad ON ad.merchant_no = xao.merchant_no and ad.active_order = xao.active_order  " +
            " LEFT JOIN merchant_info mi ON xao.merchant_no = mi.merchant_no" +
            " WHERE  xao.merchant_no = #{merchantNo} and xao.current_cycle = '1' and ad.status != '1'  ")
    @ResultType(ActivityDataDetail.class)
    ActivityDataDetail xhlfActivityOrderDeductionDetail(@Param("merchantNo") String merchantNo);

    @Select("SELECT" +
            " case when current_target_status = '0' then '未开始' " +
            "  when current_target_status = '1' then '考核中' " +
            "  when current_target_status = '2' then '已达标' " +
            "  when current_target_status = '3' then '未达标' end activityDataStatus ,  " +
            " xao.merchant_no merchantNo," +
            " mi.merchant_name merchantName," +
            " target_amount reachStandAmount," +
            " (   SELECT" +
            "  IFNULL(sum(xmttd.total_amount), 0) transAmount" +
            "  FROM" +
            "  xhlf_merchant_trans_total_day xmttd" +
            "  WHERE   xmttd.merchant_no = xao.merchant_no" +
            "  AND xao.agent_trans_total_type = xmttd.type" +
            "  AND CONCAT(   xmttd.total_day," +
            "   ' 23:59:59' ) BETWEEN xao.reward_start_time" +
            "  AND xao.reward_end_time ) transAmount," +
            "  DATE_FORMAT(xao.reward_end_time,'%Y-%m-%d %H:%i:%s')  activityEndTime," +
            " xao.agent_trans_total_type transType" +
            " FROM  xhlf_activity_order xao" +
            " LEFT JOIN activity_detail ad ON ad.merchant_no = xao.merchant_no and ad.active_order = xao.active_order " +
            " LEFT JOIN merchant_info mi ON xao.merchant_no = mi.merchant_no" +
            " WHERE  xao.merchant_no = #{merchantNo} and xao.current_cycle = #{currentCycle}  and ad.status != '1' ")
    @ResultType(ActivityDataDetail.class)
    ActivityDataDetail xhlfActivityOrderRewardDetail(@Param("merchantNo") String merchantNo ,@Param("currentCycle") String currentCycle );

    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "countActivityMerchantBySubType")
    Map<String,Object>  countActivityMerchantBySubType(@Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "fullRewardMerCount")
    Map<String,Object>  fullRewardMerCount(@Param("type") String type, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "deductionAmountMerCount")
    Map<String,Object> deductionAmountMerCount(@Param("type") String type, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "newRewardAmountMerCount")
    Map<String,Object>  newRewardAmountMerCount(@Param("currentCycle") String currentCycle,@Param("type") String type, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);


    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "newDeductionAmountMerCount")
    Map<String,Object>  newDeductionAmountMerCount(@Param("type") String type, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);


    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "newMerchantRewardAmountMerCount")
    Map<String,Object> newMerchantRewardAmountMerCount(@Param("type") String type, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);


    /**
     * 欢乐返活动详情，满奖不满扣用同一个方法
     * @param merchantNo
     * @param activityDataType
     * @return
     */
    @SelectProvider(type = ActivityDataDao.SqlProvider.class ,method = "happyBackCountDetail")
    @ResultType(ActivityDataDetail.class)
    ActivityDataDetail happyBackCountDetail(@Param("merchantNo") String merchantNo ,@Param("activityDataType") String activityDataType );


    @Select("SELECT" +
            " case when activity_target_status = '0' then '考核中' " +
            " when activity_target_status = '1' then '已达标' " +
            " when activity_target_status = '2' then '未达标' end activityDataStatus , " +
            " xamo.merchant_no merchantNo," +
            " mi.merchant_name merchantName," +
            " target_amount reachStandAmount," +
            " (  SELECT" +
            "  IFNULL(sum(xmttd.total_amount), 0) transAmount" +
            "  FROM" +
            "  xhlf_merchant_trans_total_day xmttd" +
            "  WHERE xmttd.merchant_no = xamo.merchant_no" +
            "  AND xmttd.type = '2' AND CONCAT(" +
            "  xmttd.total_day,  ' 23:59:59'  ) BETWEEN xamo.reward_start_time" +
            "  AND xamo.reward_end_time ) transAmount," +
            " DATE_FORMAT(xamo.reward_end_time,'%Y-%m-%d %H:%i:%s') activityEndTime," +
            " '2' transType FROM " +
            " xhlf_activity_merchant_order xamo" +
            " LEFT JOIN activity_detail ad ON ad.merchant_no = xamo.merchant_no and ad.active_order = xamo.active_order" +
            " LEFT JOIN merchant_info mi ON xamo.merchant_no = mi.merchant_no" +
            " WHERE xamo.merchant_no = #{merchantNo} and ad.status != '1'  ")
    @ResultType(ActivityDataDetail.class)
    ActivityDataDetail newMerchantRewardAmountCountDetail(@Param("merchantNo") String merchantNo);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "activityMerchants")
    @ResultType(Map.class)
    List<Map<String, Object>> activityMerchants(@Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "deductionAndFullAmountMerList")
    @ResultType(Map.class)
    List<Map<String, Object>> deductionAndFullAmountMerList( @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "newDeductionAndFullAmountMerList")
    @ResultType(Map.class)
    List<Map<String, Object>> newDeductionAndFullAmountMerList(@Param("currentCycle") String currentCycle, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "newMerchantRewardAmountMerList")
    @ResultType(Map.class)
    List<Map<String, Object>> newMerchantRewardAmountMerList( @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "activityMerchants")
    Map<String, Object> activityMerchantsCensus( @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "deductionAndFullAmountMerList")
    Map<String, Object> deductionAndFullAmountMerCensus(@Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "newMerchantRewardAmountMerList")
    Map<String, Object> newMerchantRewardAmountMerCensus(@Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @SelectProvider(type = ActivityDataDao.SqlProvider.class,method = "newDeductionAndFullAmountMerList")
    Map<String, Object> newDeductionAndFullAmountMerCensus(@Param("currentCycle") String currentCycle, @Param("queryBean") ActivityAndDataQueryBean activityAndDataQueryBean);

    @Select(" select * from activity_detail where merchant_no = #{merchantNo}")
    @ResultType(Map.class)
    Map<String, Object> findMerActivityDetail(@Param("merchantNo") String merchantNo);

    @Select(" select * from xhlf_activity_order where merchant_no = #{merchantNo} and current_cycle = #{cycle}")
    @ResultType(Map.class)
    Map<String, Object> findXhlfActivityOrder(@Param("merchantNo") String merchantNo ,@Param("cycle") String cycle);

    @Select("select sum(cto.trans_amount) from collective_trans_order cto " +
            " INNER JOIN add_creaditcard_log acl on acl.encrypt_account_no = cto.bank_card_id " +
            " and acl.merchant_no = cto.merchant_no and cto.trans_time >= acl.create_time" +
            " where cto.merchant_no = #{info.merchant_no} and cto.pay_method = '1' and cto.trans_status = 'SUCCESS' " +
            " and cto.order_type in ('0', '5') " +
            " and cto.trans_time BETWEEN #{info.transStartTime} and #{info.transEndTime};")
    @ResultType(BigDecimal.class)
    BigDecimal queryTransTotal(@Param("info") Map<String,Object> info);


    @Select("select sum(cto.trans_amount) from collective_trans_order cto  " +
            " where cto.merchant_no = #{info.merchant_no} and cto.pay_method = '1' and cto.trans_status = 'SUCCESS' " +
            " and cto.order_type in ('0', '5') " +
            " and cto.trans_time BETWEEN #{info.transStartTime} and #{info.transEndTime}")
    @ResultType(BigDecimal.class)
    BigDecimal queryMerchantTransTotal(@Param("info") Map<String,Object> info);

    @Select("select sum(cto.trans_amount) from collective_trans_order cto  " +
            " where cto.merchant_no = #{info.merchant_no} and cto.pay_method = '1' and cto.trans_status = 'SUCCESS' " +
            " and cto.trans_time BETWEEN #{info.transStartTime} and #{info.transEndTime}")
    @ResultType(BigDecimal.class)
    BigDecimal queryMerchantTransTotal2(@Param("info") Map<String,Object> info);

    @Select("select * from add_creaditcard_log where merchant_no = #{merchantNo} order by create_time asc limit 1")
    @ResultType(Map.class)
    Map<String, Object> selectFirstMerchantCreditcard(@Param("merchantNo") String merchantNo);


    @Select("select cto.trans_amount,cto.account_no,cto.bank_card_id from collective_trans_order cto " +
            " where cto.merchant_no = #{info.merchant_no} and cto.pay_method = '1' and cto.trans_status = 'SUCCESS' " +
            " and cto.order_type in ('0', '5') and cto.card_type = '1'" +
            " and cto.trans_time BETWEEN #{info.transStartTime} and #{info.transEndTime} " +
            " and not exists (" +
            "   select 1 from xhlf_merchant_trans_card card where card.bank_card_id = cto.bank_card_id and card.merchant_no <> #{info.merchant_no}" +
            "   and card.type = #{info.agent_trans_total_type}" +
            ")")
    @ResultType(Map.class)
    List<Map<String,Object>> queryMerchantTransListType7(@Param("info") Map<String,Object> order);

    @Select("select sum(cto.trans_amount) from collective_trans_order cto  " +
            " where cto.merchant_no = #{info.merchantNo} and cto.pay_method = '1' and cto.trans_status = 'SUCCESS' " +
            " and cto.order_type in ('0', '5') and cto.card_type = '1'" +
            " and cto.trans_time BETWEEN #{info.transStartTime} and #{info.transEndTime};")
    @ResultType(BigDecimal.class)
    BigDecimal queryMerchantCreditTransTotal(@Param("info") Map<String,Object> info);

    class SqlProvider{

        public String newMerchantRewardAmountMerList(Map<String,Object> params) {
            final ActivityAndDataQueryBean queryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            SQL sql = new SQL();
            if("count".equals(queryBean.getCountOrDetail())){
                sql.SELECT(" count(1) merAciCensus ");
            }else {
                sql.SELECT(" ad.merchant_no merchantNo ,aht.activity_type_no activityTypeNo , aht.activity_type_name activityTypeName ,aht.sub_type subType,mi.merchant_name merchantName ,mi.mobilephone phone ");
            }
            sql.FROM(" activity_detail ad ");
            sql.JOIN(" activity_hardware_type aht on ad.activity_type_no = aht.activity_type_no ");
            sql.JOIN(" merchant_info mi on mi.merchant_no = ad.merchant_no");
            sql.JOIN(" xhlf_activity_merchant_order xao on xao.merchant_no = ad.merchant_no and xao.active_order = ad.active_order");

            sql =  actMerchantWhere(sql,queryBean);

            //新欢乐送商户奖励考核状态
            if (!StringUtils.isEmpty(queryBean.getRewardStatus())) { //钟展策说的，扣款考核状态只区分活动
                sql.WHERE(" xao.activity_target_status = #{queryBean.rewardStatus} ");
            }
            if(!StringUtils.isEmpty(queryBean.getActivityDataStatus())){
                sql.WHERE(" xao.activity_target_status = #{queryBean.activityDataStatus} ");
            }



            if (!StringUtils.isEmpty(queryBean.getStartActivityTime())) {

                sql.WHERE(" xao.reward_end_time >= #{queryBean.startActivityTime} ");

            }

            if (!StringUtils.isEmpty(queryBean.getEndActivityTime())) {

                sql.WHERE(" xao.reward_end_time <= #{queryBean.endActivityTime} ");

            }

            if (!StringUtils.isEmpty(queryBean.getSortType())) {

                if (Objects.equals("desc", queryBean.getSortType())) {
                    sql.ORDER_BY(" reward_end_time  desc ");
                } else {
                    sql.ORDER_BY("  reward_end_time asc ");
                }
            }else {
                sql.ORDER_BY(" ad.active_time desc ");
            }

            return sql.toString();
        }

        public String newDeductionAndFullAmountMerList(Map<String,Object> params) {
            final ActivityAndDataQueryBean queryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            SQL sql = new SQL();
            if("count".equals(queryBean.getCountOrDetail())){
                sql.SELECT(" count(1) merAciCensus ");
            }else {
                sql.SELECT(" ad.merchant_no merchantNo ,aht.activity_type_no activityTypeNo , aht.activity_type_name activityTypeName ,aht.sub_type subType,mi.merchant_name merchantName ,mi.mobilephone phone ");
            }
            sql.FROM(" activity_detail ad ");
            sql.JOIN(" activity_hardware_type aht on ad.activity_type_no = aht.activity_type_no ");
            sql.JOIN(" merchant_info mi on mi.merchant_no = ad.merchant_no");
            sql.JOIN(" xhlf_activity_order xao on xao.merchant_no = ad.merchant_no and xao.active_order = ad.active_order");

            sql =  actMerchantWhere(sql,queryBean);

            if(!StringUtils.isEmpty(queryBean.getActivityDataStatus())){
                //奖励
                if( !"newDeductionAmount".equals(queryBean.getActivityData().getActivityDataType())   ){
                    sql.WHERE(" xao.current_target_status= #{queryBean.activityDataStatus} ");
                }
                //扣款
                if("newDeductionAmount".equals(queryBean.getActivityData().getActivityDataType()) ){

                    //0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款
                    if(Objects.equals("1",queryBean.getActivityDataStatus())){
                        sql.WHERE(" xao.deduction_account_status= '0' ");
                    }
                    if(Objects.equals("4",queryBean.getActivityDataStatus())){
                        sql.WHERE(" xao.deduction_account_status in ('1','2','3') ");
                    }
                    if(Objects.equals("5",queryBean.getActivityDataStatus())){
                        sql.WHERE(" xao.deduction_account_status = '4' ");
                    }
                }
            }


            //新欢乐送奖励考核状态
            if (!StringUtils.isEmpty(queryBean.getRewardStatus())) { //钟展策说的，扣款考核状态只区分活动
                sql.WHERE(" xao.activity_target_status = #{queryBean.rewardStatus} ");
            }

            if (!StringUtils.isEmpty(queryBean.getDeductionStatus()) ) {//钟展策说的，扣款考核状态只区分活动
                if (Objects.equals("0", queryBean.getDeductionStatus())) { //考核中
                    sql.WHERE(" xao.deduction_account_status = #{queryBean.deductionStatus} ");
                } else if (Objects.equals("1", queryBean.getDeductionStatus())) {//需扣款
                    sql.WHERE(" xao.deduction_account_status not in ('0','4') ");
                } else if (Objects.equals("2", queryBean.getDeductionStatus())) {//无需扣款
                    sql.WHERE(" xao.deduction_account_status = '4'");
                }

            }

            sql.WHERE(" xao.current_cycle = #{currentCycle}");


            if (!StringUtils.isEmpty(queryBean.getStartActivityTime())) {
                if (Objects.equals("newDeductionAmount", queryBean.getActivityData().getActivityDataType())) {
                    sql.WHERE(" xao.deduction_end_time >= #{queryBean.startActivityTime} ");
                } else {
                    sql.WHERE(" xao.reward_end_time >= #{queryBean.startActivityTime} ");
                }

            }

            if (!StringUtils.isEmpty(queryBean.getEndActivityTime())) {

                if (Objects.equals("newDeductionAmount", queryBean.getActivityData().getActivityDataType())) {
                    sql.WHERE(" xao.deduction_end_time <= #{queryBean.endActivityTime} ");
                } else {
                    sql.WHERE(" xao.reward_end_time <= #{queryBean.endActivityTime} ");
                }
            }

            if (!StringUtils.isEmpty(queryBean.getSortType())) {

                String fileItem = "";
                if (Objects.equals("newDeductionAmount", queryBean.getActivityData().getActivityDataType())) {
                    fileItem = " deduction_end_time ";
                } else {
                    fileItem = " reward_end_time ";
                }
                if (Objects.equals("desc", queryBean.getSortType())) {
                    sql.ORDER_BY(fileItem+"  desc ");
                } else {
                    sql.ORDER_BY(fileItem+ "  asc ");
                }
            }else {
                sql.ORDER_BY(" ad.active_time desc ");
            }

            return sql.toString();
        }


        public String deductionAndFullAmountMerList(Map<String,Object> params) {
            final ActivityAndDataQueryBean queryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            SQL sql = new SQL();
            if("count".equals(queryBean.getCountOrDetail())){
                sql.SELECT(" count(1) merAciCensus ");
            }else {
                sql.SELECT(" ad.merchant_no merchantNo ,aht.activity_type_no activityTypeNo , aht.activity_type_name activityTypeName ,aht.sub_type subType,mi.merchant_name merchantName ,mi.mobilephone phone ");
            }
            sql.FROM(" activity_detail ad ");
            sql.JOIN(" activity_hardware_type aht on ad.activity_type_no = aht.activity_type_no ");
            sql.JOIN(" merchant_info mi on mi.merchant_no = ad.merchant_no");

            sql =  actMerchantWhere(sql,queryBean);

            sql.WHERE(" aht.sub_type = '1' ");
            //不满扣类型只有扣款状态无奖励状态理论上是不要这个的，钟展策说状态是根据活动区分
            if (!StringUtils.isEmpty(queryBean.getRewardStatus()) ) { //钟展策说的，奖励考核状态只区分活动
                if (Objects.equals("1", queryBean.getRewardStatus())) {//已达标
                    sql.WHERE(" ad.is_standard = #{queryBean.rewardStatus} ");
                } else {
                    sql.WHERE(" ad.is_standard = '0' ");
                    if (Objects.equals("0", queryBean.getRewardStatus())) {//考核中
                        sql.WHERE(" ( ( ad.end_cumulate_time < ad.min_overdue_time  and ad.min_overdue_time is not null ) or ( ad.end_cumulate_time < ad.overdue_time and ad.min_overdue_time is  null  )  or ad.end_cumulate_time is null )");
                    } else { //未达标
                        sql.WHERE(" ( ( ad.end_cumulate_time >=  ad.min_overdue_time  and ad.min_overdue_time is not null ) or (ad.end_cumulate_time >= ad.overdue_time and ad.min_overdue_time is  null  ) )");
                    }
                }

            }

            if( !StringUtils.isEmpty(queryBean.getActivityDataStatus())){
                //参与子活动状态 0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款
                if ("fullRewardAmount".equals(queryBean.getActivityData().getActivityDataType()) ){

                    if (Objects.equals("2", queryBean.getActivityDataStatus())) {//已达标
                        sql.WHERE(" ad.is_standard = '1' ");
                        // 0 未开始  1 考核中 3 未达标(活动的状态)
                    } else if( Objects.equals("0", queryBean.getActivityDataStatus()) || Objects.equals("1", queryBean.getActivityDataStatus()) || Objects.equals("3", queryBean.getActivityDataStatus())) {
                        sql.WHERE(" ad.is_standard = '0' ");
                        if (Objects.equals("0", queryBean.getActivityDataStatus()) || Objects.equals("1", queryBean.getActivityDataStatus()) ) {//考核中
                            sql.WHERE(" ( ( ad.end_cumulate_time < ad.min_overdue_time  and ad.min_overdue_time is not null ) or ( ad.end_cumulate_time < ad.overdue_time and ad.min_overdue_time is  null  )  or ad.end_cumulate_time is null )");
                        } else { //未达标
                            sql.WHERE(" ( ( ad.end_cumulate_time >=  ad.min_overdue_time  and ad.min_overdue_time is not null ) or (ad.end_cumulate_time >= ad.overdue_time and ad.min_overdue_time is  null  ) )");
                        }

                    }
                }
                //deductionAmount
                if ("deductionAmount".equals(queryBean.getActivityData().getActivityDataType()) ){

                    if (Objects.equals("0", queryBean.getActivityDataStatus()) || Objects.equals("1", queryBean.getActivityDataStatus())) { //考核中
                        sql.WHERE("  ad.overdue_time >  now()  " );
                    }
                    if (Objects.equals("4", queryBean.getActivityDataStatus())) { //需扣款
                        sql.WHERE(" ad.overdue_time <= now() and  ad.cumulate_amount_minus > ad.cumulate_trans_min_amount ");
                    }
                    if (Objects.equals("5", queryBean.getActivityDataStatus())) { //无需扣款
                        sql.WHERE(" ad.overdue_time <= now() and  ad.cumulate_amount_minus <= ad.cumulate_trans_min_amount ");
                    }
                }

            }

            //钟展策说的，扣款考核状态只区分活动 202005
            // 20200609 更新 增加子活动 考核状态
            if (!StringUtils.isEmpty(queryBean.getDeductionStatus()) ) {

                if (Objects.equals("0", queryBean.getDeductionStatus())) { //考核中
                    sql.WHERE("  ad.overdue_time >  now()  " );
                } if (Objects.equals("1", queryBean.getDeductionStatus())) { //需扣款
                    sql.WHERE(" ad.overdue_time <= now() and  ad.cumulate_amount_minus > ad.cumulate_trans_min_amount ");
                } if (Objects.equals("2", queryBean.getDeductionStatus())) { //无需扣款
                    sql.WHERE(" ad.overdue_time <= now() and  ad.cumulate_amount_minus <= ad.cumulate_trans_min_amount ");
                }

            }

            if(!StringUtils.isEmpty(queryBean.getStartActivityTime())){
                if(Objects.equals("deductionAmount",queryBean.getActivityData().getActivityDataType())) {
                    sql.WHERE("  ad.overdue_time >= #{queryBean.startActivityTime}  ");
                }else {
                    sql.WHERE("  (( ad.overdue_time >= #{queryBean.startActivityTime} and ad.end_cumulate_time is null  ) or ad.end_cumulate_time  >= #{queryBean.startActivityTime} ) ");
                }
            }

            if(!StringUtils.isEmpty(queryBean.getEndActivityTime())){

                if(Objects.equals("deductionAmount",queryBean.getActivityData().getActivityDataType())) {
                    sql.WHERE("  ad.overdue_time <= #{queryBean.endActivityTime}  ");
                }else {
                    sql.WHERE("  (( ad.overdue_time <= #{queryBean.endActivityTime} and ad.end_cumulate_time is null  ) or ad.end_cumulate_time  <= #{queryBean.endActivityTime} ) ");
                }
            }

            if(!StringUtils.isEmpty(queryBean.getSortType())){
                if(Objects.equals("desc",queryBean.getSortType())){
                    sql.ORDER_BY("  overdue_time desc ");
                }else {
                    sql.ORDER_BY("  overdue_time asc ");
                }
            }else {
                sql.ORDER_BY(" ad.active_time desc ");
            }

            return sql.toString();
        }


        public String activityMerchants(Map<String,Object> params) {
            final ActivityAndDataQueryBean queryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            SQL sql = new SQL();
            if("count".equals(queryBean.getCountOrDetail())){
                sql.SELECT(" count(1) merAciCensus ");
            }else {
                sql.SELECT(" ad.merchant_no merchantNo ,aht.activity_type_no activityTypeNo , aht.activity_type_name activityTypeName ,aht.sub_type subType,mi.merchant_name merchantName ,mi.mobilephone phone ");
            }
            sql.FROM(" activity_detail ad ");
            sql.JOIN(" activity_hardware_type aht on ad.activity_type_no = aht.activity_type_no ");
            sql.JOIN(" merchant_info mi on mi.merchant_no = ad.merchant_no");

            sql =  actMerchantWhere(sql,queryBean);

            if(Objects.equals(queryBean.getSubType().getActivityDataType(), "newHappyGive")){
                sql.JOIN(" xhlf_activity_order xao on xao.merchant_no = ad.merchant_no and xao.active_order = ad.active_order ");
                sql.WHERE("  xao.current_cycle = '1'");
            }
            if (!StringUtils.isEmpty(queryBean.getRewardStatus())) { //钟展策说的，奖励考核状态只区分活动
                if (Objects.equals(queryBean.getSubType().getActivityDataType(), "happyBack")) {
                    if (Objects.equals("1", queryBean.getRewardStatus())) {//已达标
                        sql.WHERE(" ad.is_standard = #{queryBean.rewardStatus} ");
                    } else if (Objects.equals("0", queryBean.getRewardStatus()) || Objects.equals("2", queryBean.getRewardStatus())) {
                        sql.WHERE(" ad.is_standard = '0' ");
                        if (Objects.equals("0", queryBean.getRewardStatus())) {//考核中
                            sql.WHERE(" ( ( ad.end_cumulate_time < ad.min_overdue_time  and ad.min_overdue_time is not null ) or ( ad.end_cumulate_time < ad.overdue_time and ad.min_overdue_time is  null  ) or  ad.end_cumulate_time is null  )");
                        } else {
                            sql.WHERE(" ( ( ad.end_cumulate_time >=  ad.min_overdue_time  and ad.min_overdue_time is not null ) or (ad.end_cumulate_time >= ad.overdue_time and ad.min_overdue_time is  null  ) )");
                        }
                    }

                } else if (Objects.equals(queryBean.getSubType().getActivityDataType(), "newHappyGive")) {
                    sql.WHERE(" xao.activity_target_status = #{queryBean.rewardStatus} ");
                }
            }
            if (!StringUtils.isEmpty(queryBean.getDeductionStatus())) {//钟展策说的，扣款考核状态只区分活动

                if (Objects.equals(queryBean.getSubType().getActivityDataType(), "happyBack")) {
                    if (Objects.equals("0", queryBean.getDeductionStatus())) {
                        sql.WHERE("  ad.overdue_time >  now()  " );
                    } if (Objects.equals("1", queryBean.getDeductionStatus())) { //需扣款
                        sql.WHERE(" ad.overdue_time <= now() and  ad.cumulate_amount_minus > ad.cumulate_trans_min_amount ");
                    } if (Objects.equals("2", queryBean.getDeductionStatus())) { //无需扣款
                        sql.WHERE(" ad.overdue_time <= now() and  ad.cumulate_amount_minus <= ad.cumulate_trans_min_amount ");
                    }

                } else if (Objects.equals(queryBean.getSubType().getActivityDataType(), "newHappyGive")) {
                    if(Objects.equals("0",queryBean.getDeductionStatus())){ //考核中
                        sql.WHERE(" xao.deduction_account_status = #{queryBean.rewardStatus} ");
                    }else if(Objects.equals("1",queryBean.getDeductionStatus())){//需扣款
                        sql.WHERE(" xao.deduction_account_status not in ('0','4') ");
                    }else if(Objects.equals("2",queryBean.getDeductionStatus())){//无需扣款
                        sql.WHERE(" xao.deduction_account_status = '4'");
                    }
                }
            }
            sql.ORDER_BY(" ad.active_time desc ");
            return sql.toString();
        }

        public SQL actMerchantWhere(SQL sql ,ActivityAndDataQueryBean queryBean){
            sql.WHERE(" ad.status != '1'");
            if (!StringUtils.isEmpty(queryBean.getStartTime())) {
                sql.WHERE(" ad.active_time >= #{queryBean.startTime}");
            }
            if (!StringUtils.isEmpty(queryBean.getEndTime())) {
                sql.WHERE(" ad.active_time < #{queryBean.endTime}");
            }

            if (Objects.equals("ALL", queryBean.getQueryScope().getScopeCode())) {
                sql.WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
            }
            if (Objects.equals("OFFICAL", queryBean.getQueryScope().getScopeCode())) {
                sql.WHERE(" ad.agent_node  = #{queryBean.agentNode} ");
            }
            if (Objects.equals("CHILDREN", queryBean.getQueryScope().getScopeCode())) {
                sql.WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                sql.WHERE(" ad.agent_node  != #{queryBean.agentNode} ");
            }

            if (!StringUtils.isEmpty(queryBean.getPhone())) {
                sql.WHERE(" mi.mobilephone = #{queryBean.phone}");
            }
            if (!StringUtils.isEmpty(queryBean.getMerchantNo())) {
                sql.WHERE(" ad.merchant_no = #{queryBean.merchantNo}");
            }
            if (queryBean.getSubType() != null) {
                sql.WHERE(" aht.sub_type = #{queryBean.subType.activitySubType}");
            }
            if (!StringUtils.isEmpty(queryBean.getActivityTypeNo())) {
                sql.WHERE(" ad.activity_type_no = #{queryBean.activityTypeNo}");
            }
            return sql;
        }

        public String happyBackCountDetail(Map<String,Object> params){
            final String activityDataType = StringUtils.filterNull(params.get("activityDataType"));

            SQL sql = new SQL(){{
                if("fullRewardAmount".equals(activityDataType)){
                    SELECT("  case when ad.is_standard = '1' then '已达标' " +
                            " when ad.is_standard = '0'  and  ( ( ad.end_cumulate_time < ad.min_overdue_time  and ad.min_overdue_time is not null ) " +
                            " or ( ad.end_cumulate_time < ad.overdue_time and ad.min_overdue_time is  null  )  or ad.end_cumulate_time is null ) then '考核中' " +
                            " when ( ( ad.end_cumulate_time >=  ad.min_overdue_time  and ad.min_overdue_time is not null ) or (ad.end_cumulate_time >= ad.overdue_time and ad.min_overdue_time is  null  ) ) then '未达标' " +
                            " END activityDataStatus,ad.merchant_no merchantNo, mi.merchant_name merchantName, cumulate_amount_add reachStandAmount, cumulate_trans_amount transAmount, DATE_FORMAT(IFNULL(min_overdue_time,overdue_time),'%Y-%m-%d %H:%i:%s')  activityEndTime ,'2' transType ");
                }
                if("deductionAmount".equals(activityDataType)){
                    SELECT("  case when ad.overdue_time >  now() then '考核中' " +
                            " when ad.overdue_time <= now() and  ad.cumulate_amount_minus > ad.cumulate_trans_min_amount then '需扣款' " +
                            " when ad.overdue_time <= now() and  ad.cumulate_amount_minus <= ad.cumulate_trans_min_amount  then '无需扣款' " +
                            " END activityDataStatus,ad.merchant_no merchantNo, mi.merchant_name merchantName, cumulate_amount_minus reachStandAmount, (" +
                            " select sum(cto.trans_amount) from collective_trans_order cto  " +
                            "             where cto.merchant_no =  #{merchantNo}  and cto.pay_method = '1' and cto.trans_status = 'SUCCESS'" +
                            "             and cto.trans_time < DATE_FORMAT(now(),'%Y-%m-%d 00:00:00') " +
                            ") transAmount, DATE_FORMAT(overdue_time,'%Y-%m-%d %H:%i:%s')  activityEndTime ,'2' transType ");
                }

                FROM(" activity_detail ad  LEFT JOIN merchant_info mi on ad.merchant_no = mi.merchant_no  ");
                WHERE( "   ad.merchant_no = #{merchantNo}");
                WHERE( "   ad.status != '1'");

            }};


            return sql.toString();

        }

        public String newMerchantRewardAmountMerCount(Map<String,Object> params){
            String type = StringUtils.filterNull(params.get("type"));
            ActivityAndDataQueryBean activityAndDataQueryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            String sql = new SQL(){
                {

                    SELECT(" count(*) activityMerCount  " );
                    FROM("  xhlf_activity_merchant_order ad left join agent_info ai on ai.agent_no = ad.agent_no ");

                    JOIN( " activity_detail d on ad.active_order = d.active_order and d.merchant_no = ad.merchant_no  ");
                    WHERE(" d.status != '1' ");

                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getStartTime())){
                        WHERE(" ad.active_time >= #{queryBean.startTime}");
                    }
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getEndTime())){
                        WHERE(" ad.active_time < #{queryBean.endTime}");
                    }


                    if(Objects.equals("ALL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                    }
                    if(Objects.equals("OFFICAL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node  = #{queryBean.agentNode} ");
                    }
                    if(Objects.equals("CHILDREN",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                        WHERE(" ai.agent_node  != #{queryBean.agentNode} ");
                    }

                    //统计考核中
                    if(Objects.equals("examine",type)){
                        WHERE(" activity_target_status = '0' " );
                    }
                    //统计已达标
                    if(Objects.equals("reachStandard",type)){
                        WHERE(" activity_target_status = '1' " );
                    }
                    //统计未达标
                    if(Objects.equals("notStandard",type)){
                        WHERE(" activity_target_status = '2' " );
                    }
                }

            }.toString();
            return sql;
        }


        public String newDeductionAmountMerCount(Map<String,Object> params){
            String type = StringUtils.filterNull(params.get("type"));
            ActivityAndDataQueryBean activityAndDataQueryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            String sql = new SQL(){
                {

                    SELECT(" count(*) activityMerCount  " );
                    FROM("  xhlf_activity_order ad left join agent_info ai on ai.agent_no = ad.agent_no ");
                    JOIN( " activity_detail d on ad.active_order = d.active_order and d.merchant_no = ad.merchant_no  ");
                    WHERE(" d.status != '1' ");

                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getStartTime())){
                        WHERE(" ad.active_time >= #{queryBean.startTime}");
                    }
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getEndTime())){
                        WHERE(" ad.active_time < #{queryBean.endTime}");
                    }


                    if(Objects.equals("ALL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                    }
                    if(Objects.equals("OFFICAL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node  = #{queryBean.agentNode} ");
                    }
                    if(Objects.equals("CHILDREN",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                        WHERE(" ai.agent_node  != #{queryBean.agentNode} ");
                    }

                    //不达标扣款合并在第一周记录中 所以统计固定查询第一周期
                    WHERE(" current_cycle = '1'");

                    //统计考核中
                    if(Objects.equals("examine",type)){
                        WHERE(" deduction_account_status = '0'  " );
                    }
                    //统计需扣款
                    if(Objects.equals("notStandard",type)){
                        WHERE(" deduction_account_status not in ( '0','4')  " );
                    }
                    //统计无需扣款
                    if(Objects.equals("reachStandard",type)){
                        WHERE(" deduction_account_status = '4'  " );
                    }
                }

            }.toString();
            return sql;
        }


        public String newRewardAmountMerCount(Map<String,Object> params){
            String type = StringUtils.filterNull(params.get("type"));
            String currentCycle = StringUtils.filterNull(params.get("currentCycle"));
            ActivityAndDataQueryBean activityAndDataQueryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            String sql = new SQL(){
                {

                    SELECT(" count(*) activityMerCount  " );
                    FROM("  xhlf_activity_order ad left join agent_info ai on ai.agent_no = ad.agent_no ");
                    JOIN( " activity_detail d on ad.active_order = d.active_order   and d.merchant_no = ad.merchant_no ");
                    WHERE(" d.status != '1' ");
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getStartTime())){
                        WHERE(" ad.active_time >= #{queryBean.startTime}");
                    }
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getEndTime())){
                        WHERE(" ad.active_time < #{queryBean.endTime}");
                    }

                    if(Objects.equals("ALL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                    }
                    if(Objects.equals("OFFICAL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node  = #{queryBean.agentNode} ");
                    }
                    if(Objects.equals("CHILDREN",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ai.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                        WHERE(" ai.agent_node  != #{queryBean.agentNode} ");
                    }

                    if(!StringUtils.isEmpty(currentCycle)){
                        WHERE(" ad.current_cycle = #{currentCycle}");
                    }
                    //统计考核中
                    if(Objects.equals("notBegin",type)){
                        WHERE(" current_target_status = '0' " );
                    }
                    //统计考核中
                    if(Objects.equals("examine",type)){
                        WHERE(" current_target_status  = '1' " );
                    }
                    //统计已达标
                    if(Objects.equals("reachStandard",type)){
                        WHERE(" current_target_status = '2' " );
                    }
                    //统计未达标
                    if(Objects.equals("notStandard",type)){
                        WHERE(" current_target_status = '3' " );
                    }
                }

            }.toString();
            return sql;
        }


        public String deductionAmountMerCount(Map<String,Object> params){
            String type = StringUtils.filterNull(params.get("type"));
            ActivityAndDataQueryBean activityAndDataQueryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            String sql = new SQL(){
                {

                    SELECT(" count(*) activityMerCount  " );
                    FROM("  activity_detail ad ");
                    JOIN(" activity_hardware_type aht on aht.activity_type_no = ad.activity_type_no");
                    WHERE(" aht.sub_type = '1' ");
                    WHERE(" ad.status != '1' ");
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getStartTime())){
                        WHERE(" ad.create_time >= #{queryBean.startTime}");
                    }
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getEndTime())){
                        WHERE(" ad.create_time < #{queryBean.endTime}");
                    }
                    if(Objects.equals("ALL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                    }
                    if(Objects.equals("OFFICAL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ad.agent_node  = #{queryBean.agentNode} ");
                    }
                    if(Objects.equals("CHILDREN",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                        WHERE(" ad.agent_node  != #{queryBean.agentNode} ");
                    }

                    //统计考核中
                    if(Objects.equals("examine",type)){
                        WHERE("  overdue_time >  now()  " );
                    }
                    //统计需要扣款
                    if(Objects.equals("notStandard",type)){
                        WHERE(" overdue_time <= now() and  cumulate_amount_minus > cumulate_trans_min_amount ");
                    }
                    //统计不需要扣款
                    if(Objects.equals("reachStandard",type)){
                        WHERE(" overdue_time <= now() and  cumulate_amount_minus <= cumulate_trans_min_amount ");
                    }

                }

            }.toString();
            return sql;
        }


        public String fullRewardMerCount(Map<String,Object> params){
            String type = StringUtils.filterNull(params.get("type"));
            ActivityAndDataQueryBean activityAndDataQueryBean = (ActivityAndDataQueryBean) params.get("queryBean");
            String sql = new SQL(){
                {

                    SELECT(" count(*) activityMerCount  " );
                    FROM("  activity_detail ad ");
                    JOIN(" activity_hardware_type aht on aht.activity_type_no = ad.activity_type_no");
                    WHERE(" aht.sub_type = '1' ");
                    WHERE(" ad.status != '1' ");
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getStartTime())){
                        WHERE(" ad.create_time >= #{queryBean.startTime}");
                    }
                    if(!StringUtils.isEmpty(activityAndDataQueryBean.getEndTime())){
                        WHERE(" ad.create_time < #{queryBean.endTime}");
                    }

                    if(Objects.equals("ALL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                    }
                    if(Objects.equals("OFFICAL",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ad.agent_node  = #{queryBean.agentNode} ");
                    }
                    if(Objects.equals("CHILDREN",activityAndDataQueryBean.getQueryScope().getScopeCode())){
                        WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                        WHERE(" ad.agent_node  != #{queryBean.agentNode} ");
                    }

                    //统计考核中
                    if(Objects.equals("examine",type)){
                        WHERE(" ad.is_standard = '0'  ");
                        WHERE(" ( end_cumulate_time is null or ( end_cumulate_time < min_overdue_time  and min_overdue_time is not null ) or ( end_cumulate_time < overdue_time and min_overdue_time is  null  ) )" );
                    }
                    //统计已达标
                    if(Objects.equals("reachStandard",type)){
                        WHERE(" ad.is_standard = '1'  ");
                    }
                    //统计未达标
                    if(Objects.equals("notStandard",type)){
                        WHERE(" ad.is_standard = '0'  ");
                        WHERE(" ( ( end_cumulate_time >=  min_overdue_time  and min_overdue_time is not null ) or ( end_cumulate_time >= overdue_time and min_overdue_time is  null  ) )" );
                    }

                }

            }.toString();
            return sql;
        }

        public String countActivityMerchantBySubType(Map<String,Object> params){

            ActivityAndDataQueryBean activityAndDataQueryBean = (ActivityAndDataQueryBean) params.get("queryBean");

             String sql = new SQL(){
                 {
                     SELECT(" count(*) activityMerCount  " );
                     FROM("  activity_detail ad ");
                     JOIN(" activity_hardware_type aht on aht.activity_type_no = ad.activity_type_no");

                     WHERE(" aht.sub_type = #{queryBean.subType.activitySubType}");
                     WHERE(" status != '1' ");

                     if(!StringUtils.isEmpty(activityAndDataQueryBean.getStartTime())){
                         WHERE(" ad.active_time >= #{queryBean.startTime}");
                     }
                     if(!StringUtils.isEmpty(activityAndDataQueryBean.getEndTime())){
                         WHERE(" ad.active_time <= #{queryBean.endTime}");
                     }


                     if (Objects.equals("ALL",activityAndDataQueryBean.getQueryScope().getScopeCode())) {
                         WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                     }

                     if (Objects.equals("OFFICAL",activityAndDataQueryBean.getQueryScope().getScopeCode())) {
                         WHERE(" ad.agent_node  = #{queryBean.agentNode} ");
                     }
                     if (Objects.equals("CHILDREN",activityAndDataQueryBean.getQueryScope().getScopeCode())) {
                         WHERE(" ad.agent_node like CONCAT( #{queryBean.agentNode}, '%' )  ");
                         WHERE(" ad.agent_node  != #{queryBean.agentNode} ");
                     }




                 }

             }.toString();
             return sql;

        }

    }
}
