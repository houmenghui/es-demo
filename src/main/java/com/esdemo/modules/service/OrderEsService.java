package com.esdemo.modules.service;

import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.EsSearchBean;
import com.esdemo.modules.bean.KeyValueBean;
import com.esdemo.modules.bean.Tuple;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Title：agentApi2
 * @Description：订单业务层(ES)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface OrderEsService {

    /**
     * 统计订单交易笔数与金额
     *
     * @param searchBean
     * @return 笔数和金额
     */
    Tuple<Long, BigDecimal> summaryOrderCountAndAmountByTerms(EsSearchBean searchBean);

    /**
     * 统计新增商户交易笔数与金额
     *
     * @param searchBean
     * @return
     */
    Tuple<Long, BigDecimal> summaryAddMerOrderCountAndAmountByTerms(EsSearchBean searchBean);

    /**
     * 业绩统计
     *
     * @param queryAgentInfo
     * @param isThreeData    是否三方数据
     * @param threeTeamIds   三方组织
     * @param queryScope
     * @param timeUnit
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<KeyValueBean> summaryAchievementByTerms(AgentInfo queryAgentInfo, boolean isThreeData, List<String> threeTeamIds, QueryScope queryScope, TimeUnitEnum timeUnit, int pageNo, int pageSize);

    /**
     * 查询近七日和半年的交易数据
     *
     * @param searchBean ES查询条件
     * @return v1 7日数据
     * v2 半年数据
     */
    Tuple<List<KeyValueBean>, List<KeyValueBean>> listSevenDayAndHalfYearDataTrend(EsSearchBean searchBean);

    /**
     * 三方数据代理商汇总
     *
     * @param queryAgentInfo
     * @param threeTeamIds   三方组织
     * @param timeUnit
     * @param timeStr        查询日期（日：2020-04-23、月：2020-04）
     * @param orderByType    排序类型（1：总交易量由高到低、2：总交易量由低到高）
     * @return
     */
    List<KeyValueBean> threeDataAgentCensus(AgentInfo queryAgentInfo, List<String> threeTeamIds, TimeUnitEnum timeUnit, String timeStr, String orderByType);
}
