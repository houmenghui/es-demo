package com.esdemo.modules.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.enums.OrderTransStatus;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.es.EsLog;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.EsSearchBean;
import com.esdemo.modules.bean.KeyValueBean;
import com.esdemo.modules.bean.Tuple;
import com.esdemo.modules.service.AgentEsService;
import com.esdemo.modules.service.MerchantEsService;
import com.esdemo.modules.service.OrderEsService;
import com.esdemo.modules.service.ThreeAgentService;
import com.esdemo.modules.utils.EsSearchUtils;
import com.esdemo.modules.utils.ThreeAgentUtils;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.esdemo.frame.enums.EsNpospField.*;
import static com.esdemo.frame.enums.EsNpospJoinType.*;

/**
 * @Title：agentApi2
 * @Description：订单业务层实现(ES)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Slf4j
@Service
public class OrderEsServiceImpl implements OrderEsService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private MerchantEsService merchantEsService;
    @Resource
    private AgentEsService agentEsService;
    @Resource
    private ThreeAgentService threeAgentService;

    private static final String SUM_OF_TRANS_AMOUNT = "sumTransAmount";

    /**
     * 统计订单交易笔数与金额
     *
     * @param searchBean
     * @return 笔数和金额
     */
    @CacheData
    @Override
    public Tuple<Long, BigDecimal> summaryOrderCountAndAmountByTerms(EsSearchBean searchBean) {
        //按交易金额进行汇总
        SumAggregationBuilder sumTransAmountBuilder = AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName());
        SearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean)
                .addAggregation(sumTransAmountBuilder).build();
        EsLog.info("统计交易金额", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            try {
                BigDecimal sumOfTransAmount = BigDecimal.ZERO;
                Long countOfOrder = 0L;

                countOfOrder = response.getHits().getTotalHits();
                InternalSum aggreRes = (InternalSum) response.getAggregations().asMap().get(SUM_OF_TRANS_AMOUNT);
                log.info("统计订单交易笔数与金额【参数：{}】，结果{}", JSONUtil.toJsonStr(searchBean), aggreRes);
                sumOfTransAmount = aggreRes == null ? BigDecimal.ZERO : new BigDecimal(aggreRes.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                return new Tuple<>(countOfOrder, sumOfTransAmount);

            } catch (Exception e) {
                log.info("统计订单交易笔数与金额【参数：{}】，结果异常{}", JSONUtil.toJsonStr(searchBean), e);
                return new Tuple<>(0L, BigDecimal.ZERO);
            }
        });
    }

    /**
     * 统计新增商户交易笔数与金额
     *
     * @param searchBean
     * @return
     */
    @CacheData
    @Override
    public Tuple<Long, BigDecimal> summaryAddMerOrderCountAndAmountByTerms(EsSearchBean searchBean) {

        //关联商户父查询
        BoolQueryBuilder parentBuilder = QueryBuilders.boolQuery();
        List<AgentInfo> inAgentInfo = new ArrayList<>();
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setAgentNo(searchBean.getAgentNo());
        agentInfo.setAgentNode(searchBean.getAgentNode());
        inAgentInfo.add(agentInfo);
        EsSearchUtils.setInAgentQueryScope(parentBuilder, searchBean.getQueryScope(), MERCHANT.getTypeName(), inAgentInfo);
        //过滤开始创建时间，结束创建时间
        RangeQueryBuilder createTimeRange = null;
        if (StringUtils.isNotBlank(searchBean.getStartCreateTime())) {
            createTimeRange = QueryBuilders.rangeQuery(CREATE_TIME.getFieldName()).gte(searchBean.getStartCreateTime());
        }
        if (StringUtils.isNotBlank(searchBean.getEndCreateTime())) {
            createTimeRange = (null == createTimeRange ? QueryBuilders.rangeQuery(CREATE_TIME.getFieldName()).lte(searchBean.getEndCreateTime()) : createTimeRange.lte(searchBean.getEndCreateTime()));
        }
        if (null != createTimeRange) {
            parentBuilder.must(createTimeRange);
        }
        //关联商户父查询
        HasParentQueryBuilder parentQuery = JoinQueryBuilders.hasParentQuery(MERCHANT.getTypeName(), parentBuilder, false);
        searchBean.setParentQueryBuilderList(Arrays.asList(parentQuery));
        //按交易金额进行汇总
        SumAggregationBuilder sumTransAmountBuilder = AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName());
        SearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean)
                .addAggregation(sumTransAmountBuilder).build();
        EsLog.info("新增商户交易汇总", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            try {
                BigDecimal sumOfTransAmount = BigDecimal.ZERO;
                Long countOfOrder = 0L;

                countOfOrder = response.getHits().getTotalHits();
                InternalSum aggreRes = (InternalSum) response.getAggregations().asMap().get(SUM_OF_TRANS_AMOUNT);
                log.info("新增商户交易汇总【参数：{}】，结果{}", JSONUtil.toJsonStr(searchBean), aggreRes);
                sumOfTransAmount = aggreRes == null ? BigDecimal.ZERO : new BigDecimal(aggreRes.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                return new Tuple<>(countOfOrder, sumOfTransAmount);

            } catch (Exception e) {
                log.info("新增商户交易汇总【参数：{}】，结果异常{}", JSONUtil.toJsonStr(searchBean), e);
                return new Tuple<>(0L, BigDecimal.ZERO);
            }
        });
    }

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
    @Override
    @CacheData
    public List<KeyValueBean> summaryAchievementByTerms(AgentInfo queryAgentInfo, boolean isThreeData, List<String> threeTeamIds, QueryScope queryScope, TimeUnitEnum timeUnit, int pageNo, int pageSize) {

        List<KeyValueBean> resList = new ArrayList<>();
        if (null == queryAgentInfo) {
            return resList;
        }
        Date now = new Date();
        //默认从2019-09-01开始统计
        Date beginDate = DateUtil.parse("2019-09-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        String beginTimeStr = "", endTimeStr = "";
        int beginIndex = (pageNo - 1) * pageSize;
        int loopIndex = 1;
        //按日维度查询
        if (TimeUnitEnum.DAY == timeUnit) {
            String yyyyMMdd;
            //不包含当天
            Date endDate = DateUtils.addDays(now, (beginIndex + 1) * -1);
            Date loopDate = endDate;
            while (!loopDate.before(beginDate) && loopIndex <= pageSize) {
                yyyyMMdd = DateUtil.format(loopDate, "yyyy-MM-dd");
                beginTimeStr = yyyyMMdd + " 00:00:00";
                endTimeStr = yyyyMMdd + " 23:59:59";
                resList.add(new KeyValueBean(yyyyMMdd, implAchievementSummary(queryAgentInfo, isThreeData, true, threeTeamIds, queryScope, beginTimeStr, endTimeStr)));
                loopDate = DateUtils.addDays(loopDate, -1);
                loopIndex++;
            }
        }
        //按月维度查询
        if (TimeUnitEnum.MONTH == timeUnit) {
            String yyyyMM;
            //包含当月不包含当天
            beginDate = DateUtil.endOfMonth(beginDate);
            Date endDate = DateUtil.endOfMonth(DateUtils.addMonths(now, beginIndex * -1));
            if (pageNo == 1) {
                endDate = DateUtils.addDays(now, -1);
            }
            Date loopDate = endDate;

            while (!loopDate.before(beginDate) && loopIndex <= pageSize) {
                yyyyMM = DateUtil.format(loopDate, "yyyy-MM");
                beginTimeStr = yyyyMM + "-01 00:00:00";
                endTimeStr = DateUtil.format(loopDate, "yyyy-MM-dd") + " 23:59:59";

                resList.add(new KeyValueBean(yyyyMM, implAchievementSummary(queryAgentInfo, isThreeData, true, threeTeamIds, queryScope, beginTimeStr, endTimeStr)));

                loopDate = DateUtil.endOfMonth(DateUtils.addMonths(loopDate, -1));
                loopIndex++;
            }
        }
        return resList;
    }

    /**
     * 查询近七日和半年的交易数据
     *
     * @param searchBean ES查询条件
     * @return v1 7日数据
     * v2 半年数据
     */
    @CacheData(type = CacheData.CacheType.ALL_DAY)
    @Override
    public Tuple<List<KeyValueBean>, List<KeyValueBean>> listSevenDayAndHalfYearDataTrend(EsSearchBean searchBean) {
        return EsSearchUtils.listSevenDayAndHalfYearDataTrend(searchBean);
    }

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
    @Override
    @CacheData
    public List<KeyValueBean> threeDataAgentCensus(AgentInfo queryAgentInfo, List<String> threeTeamIds, TimeUnitEnum timeUnit, String timeStr, String orderByType) {
        List<KeyValueBean> resList = new ArrayList<>();
        if (null == queryAgentInfo) {
            return resList;
        }
        List<AgentInfo> directThreeAgentList = threeAgentService.getDirectChildThreeAgent(queryAgentInfo.getAgentNo());
        if (CollectionUtils.isEmpty(directThreeAgentList)) {
            log.info("当前代理商{}的三方代理商数据不存在");
            return resList;
        }
        String beginTimeStr = "", endTimeStr = "", threeAgentNo = "", threeAgentName = "", key = "";
        Date queryDate = null;
        //按日维度查询
        if (TimeUnitEnum.DAY == timeUnit) {
            try {
                queryDate = DateUtils.parseDate(timeStr, "yyyy-MM-dd");
            } catch (Exception e) {
                log.error("查询日期{}不合法", timeStr);
                return resList;
            }
            beginTimeStr = timeStr + " 00:00:00";
            endTimeStr = timeStr + " 23:59:59";
            for (AgentInfo threeAgent : directThreeAgentList) {
                threeAgentNo = StringUtils.filterNull(threeAgent.getAgentNo());
                threeAgentName = StringUtils.filterNull(threeAgent.getAgentName());
                key = threeAgentNo + "_" + threeAgentName;

                resList.add(new KeyValueBean(key, implAchievementSummary(threeAgent, true, true, threeTeamIds, QueryScope.ALL, beginTimeStr, endTimeStr)));
            }
        }

        //按月维度查询
        if (TimeUnitEnum.MONTH == timeUnit) {
            try {
                queryDate = DateUtils.parseDate(timeStr, "yyyy-MM");
            } catch (Exception e) {
                log.error("查询日期{}不合法", timeStr);
                return resList;
            }
            //如果是当前月，只统计到前一天为止
            Date now = new Date();
            int currMonth = DateUtil.month(now);
            int queryMonth = DateUtil.month(queryDate);
            beginTimeStr = DateUtil.format(DateUtil.beginOfMonth(queryDate), "yyyy-MM-dd HH:mm:ss");
            endTimeStr = DateUtil.format(DateUtil.endOfMonth(queryDate), "yyyy-MM-dd HH:mm:ss");
            if (currMonth == queryMonth) {
                Date beforeDay = DateUtils.addDays(now, -1);
                endTimeStr = DateUtil.format(DateUtil.endOfDay(beforeDay), "yyyy-MM-dd HH:mm:ss");
            }
            for (AgentInfo threeAgent : directThreeAgentList) {
                threeAgentNo = StringUtils.filterNull(threeAgent.getAgentNo());
                threeAgentName = StringUtils.filterNull(threeAgent.getAgentName());
                key = threeAgentNo + "_" + threeAgentName;

                resList.add(new KeyValueBean(key, implAchievementSummary(threeAgent, true, true, threeTeamIds, QueryScope.ALL, beginTimeStr, endTimeStr)));
            }
        }
        //排序类型，0：代理商注册日期倒序、1：总交易量由低到高、2：总交易量由高到低
        //默认排序按代理商注册日期倒序，sql查询时候已经排序
        if (StringUtils.isNotBlank(orderByType) && !"0".equalsIgnoreCase(orderByType)) {
            resList.sort(new Comparator<KeyValueBean>() {
                @Override
                public int compare(KeyValueBean o1, KeyValueBean o2) {
                    Map<String, Object> o1ValueMap = (Map<String, Object>) o1.getValue();
                    Map<String, Object> o2ValueMap = (Map<String, Object>) o2.getValue();
                    BigDecimal o1TotalOrderAmount = (BigDecimal) o1ValueMap.get("totalOrderAmount");
                    BigDecimal o2TotalOrderAmount = (BigDecimal) o2ValueMap.get("totalOrderAmount");
                    return "1".equalsIgnoreCase(orderByType) ? o1TotalOrderAmount.compareTo(o2TotalOrderAmount) : o2TotalOrderAmount.compareTo(o1TotalOrderAmount);
                }
            });
        }
        return resList;
    }


    /**
     * 业绩统计实现，供summaryAchievementByTerms调用
     *
     * @param queryAgentInfo
     * @param isThreeData    是否三方数据
     * @param threeTeamIds   三方组织
     * @param queryScope
     * @param beginTimeStr
     * @param endTimeStr
     */
    private Map<String, Object> implAchievementSummary(AgentInfo queryAgentInfo, boolean isThreeData, boolean isContainSelf, List<String> threeTeamIds, QueryScope queryScope,
                                                       String beginTimeStr, String endTimeStr) {

        //返回数据
        Map<String, Object> resMap = new HashMap<>();

        List<AgentInfo> inAgentInfo = new ArrayList<>();
        Map<String, List<String>> inFields = null;
        if (isThreeData) {
            ThreeAgentUtils.getAllThreeChildAgentNos(queryAgentInfo.getAgentNo(), inAgentInfo);
            if (isContainSelf) {
                inAgentInfo.add(queryAgentInfo);
            }
        } else {
            inAgentInfo.add(queryAgentInfo);
        }
        if (CollectionUtils.isEmpty(inAgentInfo)) {
            resMap.put("addMerCount", 0);
            resMap.put("activedMerCount", 0);
            resMap.put("addAgentCount", 0);
            resMap.put("totalOrderCount", 0);
            resMap.put("totalOrderAmount", 0.00);
            resMap.put("posOrderCount", 0);
            resMap.put("posOrderAmount", 0.00);
            resMap.put("noCardOrderCount", 0);
            resMap.put("noCardOrderAmount", 0.00);
            resMap.put("yunOrderCount", 0);
            resMap.put("yunOrderAmount", 0.00);
            return resMap;
        }
        //三方组织查询
        if (!CollectionUtils.isEmpty(threeTeamIds)) {
            inFields = new HashMap<>();
            inFields.put(TEAM_ID.getFieldName(), threeTeamIds);
        }
        EsSearchBean searchBean = null;

        //新增商户数量
        searchBean = EsSearchBean.builder().typeName(MERCHANT.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).inFields(inFields).build();
        searchBean.setTypeName(MERCHANT.getTypeName());
        long addMerCount = merchantEsService.summaryMerCountByTerms(searchBean);
        resMap.put("addMerCount", addMerCount);
        //已激活商户数量
        searchBean = EsSearchBean.builder().typeName(MERCHANT.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).hlfActive("1").inFields(inFields).build();
        searchBean.setTypeName(MERCHANT.getTypeName());
        long activedMerCount = merchantEsService.summaryMerCountByTerms(searchBean);
        resMap.put("activedMerCount", activedMerCount);
        //新增代理商数量
        searchBean = EsSearchBean.builder().typeName(AGENT.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).inFields(inFields).build();
        if (isThreeData) {
            searchBean.setInFields(null);
        }
        long addAgentCount = agentEsService.summaryAgentCountByTerms(searchBean);
        resMap.put("addAgentCount", addAgentCount);
        //总交易量，排除首笔激活交易
        Map<String, List<String>> notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3"}));
        searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                .notFields(notFields).inFields(inFields).build();
        Tuple<Long, BigDecimal> totalOrderTuple = summaryOrderCountAndAmountByTerms(searchBean);
        resMap.put("totalOrderCount", totalOrderTuple.v1());
        resMap.put("totalOrderAmount", totalOrderTuple.v2());
        //POS刷卡（不包含云闪付）
        notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3", "5"}));
        searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                .payMethod("1").notFields(notFields).inFields(inFields).build();
        Tuple<Long, BigDecimal> posOrderTuple = summaryOrderCountAndAmountByTerms(searchBean);
        resMap.put("posOrderCount", posOrderTuple.v1());
        resMap.put("posOrderAmount", posOrderTuple.v2());
        //无卡
        notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3"}), PAY_METHOD.getFieldName(), Arrays.asList("1"));
        searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                .notFields(notFields).inFields(inFields).build();
        Tuple<Long, BigDecimal> noCardOrderTuple = summaryOrderCountAndAmountByTerms(searchBean);
        resMap.put("noCardOrderCount", noCardOrderTuple.v1());
        resMap.put("noCardOrderAmount", noCardOrderTuple.v2());
        //云闪付，orderType固定为5
        searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).inAgentInfo(inAgentInfo).queryScope(queryScope)
                .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                .orderType("5").inFields(inFields).build();
        Tuple<Long, BigDecimal> yunOrderTuple = summaryOrderCountAndAmountByTerms(searchBean);
        resMap.put("yunOrderCount", yunOrderTuple.v1());
        resMap.put("yunOrderAmount", yunOrderTuple.v2());

        return resMap;
    }
}
