package com.esdemo.modules.utils;

import com.esdemo.frame.enums.EsNpospJoinType;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.es.EsLog;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.MerchantEsService;
import com.esdemo.modules.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.aggregations.ChildrenAggregationBuilder;
import org.elasticsearch.join.aggregations.InternalChildren;
import org.elasticsearch.join.aggregations.JoinAggregationBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.bucketselector.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.esdemo.frame.enums.EsNpospField.*;
import static com.esdemo.frame.enums.EsNpospJoinType.*;
import static com.esdemo.frame.utils.Constants.*;
import static com.esdemo.modules.bean.MerchantSearchBean.SearchType;
import static com.esdemo.modules.bean.MerchantSearchBean.SortType;

/**
 * 商户查询工具类
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-20 10:53
 */
@Component
@Slf4j
public class MerchantSearchUtils {

    private static SysConfigService sysConfigService;
    private static ElasticsearchTemplate elasticsearchTemplate;
    private static MerchantEsService merchantEsService;

    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM");
    public static final DateTimeFormatter YYYY_MM_01_00_00_00 = DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00");
    public static final DateTimeFormatter YYYY_MM_DD_00_00_00 = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
    public static final DateTimeFormatter MM_DD = DateTimeFormatter.ofPattern("MM-dd");
    public static final DateTimeFormatter YYYY_MM_DD_23_59_59 = DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59");

    private static final String TRANS_STTUS_IS_SUCCESS = "SUCCESS";

    private static final String GROUP_BY_TEAM_ID = "group_by_team_id";            // 根据组织id分类
//    private static final String GROUP_BY_TEAM_ENTRY_ID = "group_by_team_entry_id";// 根据子组织id分类
    private static final String MERCHANT_HAS_ACTIVE = "merchant_has_active";        // 商户激活
    private static final String GROUP_BY_MERCHANT = "group_by_merchant";          // 根据商户编号分类
    private static final String GROUP_BY_BP_ID = "group_by_bp_id";                // 根据业务产品分类
    private static final String CHILDREN_OF_ORDER = "children_of_order";          // 查询订单子节点
    private static final String ALL_TIME = "all_time";                            // 所有时间
    private static final String ALL_TIME_TOTAL = "all_time_total";                            // 所有时间交易量
    private static final String CURRENT_MONTH = "current_month";                  // 当月
    private static final String LAST_LAST_MONTH = "last_last_month";              // 上上个月
    private static final String LAST_MONTH = "last_month";                        // 上个月
    private static final String SUM_OF_TRANS_AMOUNT = "sum_of_trans_amount";      // 交易量之和
    private static final String TOTAL_OF_TRANS = "total_of_trans";      // 交易量笔数
    private static final String SEVEN_DAY_AGO = "seven_day_ago";                   // 七日前
    private static final String HALF_YEAR_AGO = "half_year_ago";                  // 半年前
    private static final String TRADE_SLIDE = "trade_slide";                      // 交易下滑
    private static final String FILTER_QUALITY_MERCHANT = "filter_quality_merchant";                      // 优质商户过滤

    /**
     * 商户详情
     *
     * @param merchantNo 商户编号
     * @param agentNode  代理商节点
     */
    public static MerchantDetailBean getMerchantDetails(String merchantNo, String agentNode) {
        if (StringUtils.isBlank(merchantNo) || StringUtils.isBlank(agentNode)) {
            return new MerchantDetailBean();
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                // 查询交易成功的订单数据
                .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), ORDER.getTypeName()))
                .must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                // 根据指定代理商节点下的商户编号查询
                .must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                .must(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo));

        // 汇总当月的交易金额
        FilterAggregationBuilder currentMonthTrans = AggregationBuilders.filter(CURRENT_MONTH,
                QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(LocalDateTime.now().format(YYYY_MM_01_00_00_00)))
                .subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()))
                .subAggregation(AggregationBuilders.count(TOTAL_OF_TRANS).field(ORDER_NO.getFieldName()))
                ;

        // 汇总所有时间的交易金额
        SumAggregationBuilder allTimeTrans = AggregationBuilders.sum(ALL_TIME).field(TRANS_AMOUNT.getFieldName());
        // 汇总所有时间的交易笔数
        ValueCountAggregationBuilder allTimeTotal = AggregationBuilders.count(ALL_TIME_TOTAL).field(ORDER_NO.getFieldName());
        // 汇总七日交易敬意金额
        DateRangeAggregationBuilder sevenDayAgoTrans = buildSevenDayAgoTransAggregation();
        // 汇总半年交易敬意金额
        DateRangeAggregationBuilder halfYearAgoTrans = buildHalfYearAgoTransAggregation();

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(1, 10))
                .addAggregation(currentMonthTrans)
                .addAggregation(allTimeTrans)
                .addAggregation(allTimeTotal)
                .addAggregation(sevenDayAgoTrans)
                .addAggregation(halfYearAgoTrans)
                .build();
        EsLog.info("查询商户详情", searchQuery);
        return elasticsearchTemplate.query(searchQuery, response -> {
            Map<String, Aggregation> aggregationMap = response.getAggregations().getAsMap();
            // 获取当月交易金额
            double currentMonthTransMoney = ((InternalSum) ((InternalFilter) aggregationMap.get(CURRENT_MONTH)).getAggregations().get(SUM_OF_TRANS_AMOUNT)).getValue();
            long currentMonthTransTotal = ((InternalValueCount) ((InternalFilter) aggregationMap.get(CURRENT_MONTH)).getAggregations().get(TOTAL_OF_TRANS)).getValue();
            // 获取所有时间的交易金额
            double allTimeTransMoney = ((InternalSum) aggregationMap.get(ALL_TIME)).getValue();
            long allTimeTransTotal = ((InternalValueCount) aggregationMap.get(ALL_TIME_TOTAL)).getValue();
            // 获取七日交易金额
            InternalDateRange seventDateRange = (InternalDateRange) aggregationMap.get(SEVEN_DAY_AGO);
            List<KeyValueBean> sevenDataList = seventDateRange.getBuckets()
                    .stream()
                    .map(item -> {
                        String sum = rounding(((InternalSum) item.getAggregations().getAsMap().get(SUM_OF_TRANS_AMOUNT)).getValue());
                        return new KeyValueBean(item.getKey(), sum, "");
                    }).collect(Collectors.toList());
            // 获取半年的交易金额
            InternalDateRange halfYearDateRange = (InternalDateRange) aggregationMap.get(HALF_YEAR_AGO);
            List<KeyValueBean> halfYearDataList = halfYearDateRange.getBuckets()
                    .stream()
                    .map(item -> {
                        String sum = rounding(((InternalSum) item.getAggregations().getAsMap().get(SUM_OF_TRANS_AMOUNT)).getValue());
                        return new KeyValueBean(item.getKey(), sum, "");
                    }).collect(Collectors.toList());
            // 封装数据并返回
            MerchantDetailBean merchantDetailBean = new MerchantDetailBean();
            merchantDetailBean.setCurrentTransMoney(rounding(currentMonthTransMoney));
            merchantDetailBean.setCurrentTransTotal(currentMonthTransTotal);
            merchantDetailBean.setAllTransMoney(rounding(allTimeTransMoney));
            merchantDetailBean.setAllTransTotal(allTimeTransTotal);
            merchantDetailBean.setSevenDayDatas(sevenDataList);
            merchantDetailBean.setHalfYearDatas(halfYearDataList);
            return merchantDetailBean;
        });
    }

    /**
     * 查询商户业务产品交易数据
     *
     * @param merchantNo 商户
     * @param agentNode  代理商节点
     * @return v1 已经审核通过的业务产品, v2 还没有审核通过的业务产品
     */
    public static Tuple<List<MerchantBpBean>, List<MerchantBpBean>> getMerchantDetail4Bp(String merchantNo, String agentNode) {
        // 查询商户所拥有的业务产品
        BoolQueryBuilder mbpQueryBuilder = QueryBuilders.boolQuery()
                // 查询商户进件
                .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MBP.getTypeName()))
                // 根据指定代理商节点的商户编号查询
                .must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                .must(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo));

        SearchQuery mbpSearchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(mbpQueryBuilder)
                .withPageable(PageRequest.of(0, 1000))
                .build();
        EsLog.info("查询商户所拥有的业务产品", mbpSearchQuery);

        Tuple<List<MerchantBpBean>, List<MerchantBpBean>> listListTuple = elasticsearchTemplate.query(mbpSearchQuery, response -> {
            List<MerchantBpBean> normalBpId = new ArrayList<>();
            List<MerchantBpBean> unnormalBpId = new ArrayList<>();
            Arrays.stream(response.getHits().getHits())
                    .forEach(item -> {
                        Map<String, Object> sourceAsMap = item.getSourceAsMap();
                        String stuats = Objects.toString(sourceAsMap.get(STATUS.getFieldName()));
                        String bpId = Objects.toString(sourceAsMap.get(BP_ID.getFieldName()));
                        MerchantBpBean merchantBpBean = new MerchantBpBean();
                        merchantBpBean.setBpId(bpId);
                        merchantBpBean.setBpStatus(stuats);
                        merchantBpBean.setTransAmount("0.0");
                        if (StringUtils.equalsIgnoreCase(stuats, "4")) {
                            normalBpId.add(merchantBpBean);
                        } else {
                            unnormalBpId.add(merchantBpBean);
                        }
                    });
            return new Tuple<>(normalBpId, unnormalBpId);
        });

        if (CollectionUtils.isEmpty(listListTuple.v1())) {
            return listListTuple;
        }
        // 查询正常的业务产品的交易额
        List<String> bpList = listListTuple.v1().stream().map(MerchantBpBean::getBpId).collect(Collectors.toList());
        BoolQueryBuilder transQueryBuilder = QueryBuilders.boolQuery()
                // 查询交易成功的订单
                .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), ORDER.getTypeName()))
                .must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                // 指定交易的业务产品
                .must(QueryBuilders.termsQuery(BP_ID.getFieldName(), bpList))
                // 指定代理商节点下的商户编号查询
                .must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                .must(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo));

        // 根据业务产品汇总交易金额
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(GROUP_BY_BP_ID).field(BP_ID.getFieldName()).size(bpList.size())
                .subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()));

        SearchQuery transSearchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(transQueryBuilder)
                .addAggregation(aggregationBuilder)
                .build();
        EsLog.info("查询商户所拥有的业务产品的交易量", transSearchQuery);
        Map<Object, Double> transMap = elasticsearchTemplate.query(transSearchQuery, response -> {
            StringTerms aggregation = (StringTerms) response.getAggregations().getAsMap().get(GROUP_BY_BP_ID);
            return aggregation.getBuckets().stream()
                    .collect(Collectors.toMap((StringTerms.Bucket::getKey),
                            item -> ((InternalSum) item.getAggregations().getAsMap().get(SUM_OF_TRANS_AMOUNT)).getValue()));
        });
        listListTuple.v1().forEach(item -> {
            Double sum = Optional.ofNullable(transMap.get(item.getBpId())).orElse(0.0);
            item.setTransAmount(rounding(sum));
        });
        return listListTuple;
    }

    /**
     * 构建半年交易金额汇总
     */
    private static DateRangeAggregationBuilder buildHalfYearAgoTransAggregation() {
        LocalDateTime now = LocalDateTime.now();
        DateRangeAggregationBuilder builder = AggregationBuilders.dateRange(HALF_YEAR_AGO).field(TRANS_TIME.getFieldName());
        for (int i = 0; i < 6; i++) {
            LocalDateTime localDateTime = now.minusMonths(i + 1L);
            String key = localDateTime.format(YYYY_MM);
            String start = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).format(YYYY_MM_DD_00_00_00);
            String end = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59);
            builder.addRange(key, start, end);
        }
        builder.subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()));
        return builder;
    }

    /**
     * 构建七日交易金额汇总
     */
    private static DateRangeAggregationBuilder buildSevenDayAgoTransAggregation() {
        LocalDateTime now = LocalDateTime.now();
        DateRangeAggregationBuilder builder = AggregationBuilders.dateRange(SEVEN_DAY_AGO).field(TRANS_TIME.getFieldName());
        for (int i = 0; i < 7; i++) {
            LocalDateTime day = now.minusDays(i + 1L);
            builder.addRange(day.format(MM_DD), day.format(YYYY_MM_DD_00_00_00), day.format(YYYY_MM_DD_23_59_59));
        }
        builder.subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()));
        return builder;
    }

    /**
     * 查询商户列表
     *
     * @param searchParams 查询参数
     * @param pageRequest  分页信息
     * @param agentNode    代理商节点
     */
    public static Tuple<List<MerchantEsResultBean>, Long> listMerchantInfo(MerchantSearchBean searchParams,
                                                                           PageRequest pageRequest, String agentNode) {
        // 登陆信息有误的话,则返回默认值
        if (StringUtils.isBlank(agentNode)) {
            return new Tuple<>(new ArrayList<>(), 0L);
        }
        PageRequest page = Optional.ofNullable(pageRequest).orElse(PageRequest.of(0, 10));
        // 解析查询参数
        MerchantSearchBean merchantSearchBean = parseSearchParamBySearchType(searchParams);
        // 构建es查询条件
        QueryBuilder queryBuilder = merchantQueryBuilder(merchantSearchBean, agentNode);
        // 构建es汇总
        AbstractAggregationBuilder aggregationBuilder = merchantAggregation(merchantSearchBean, pageRequest);

        // 构造es查询条件
        SearchQuery query = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                // 构建es查询条件
                .withQuery(queryBuilder)
                // 构造es查询汇总参数
                .addAggregation(aggregationBuilder)
                // 设置es分页信息
                .withPageable(pageRequest)
                // 默认使用商户创建时间倒序排
                .withSort(SortBuilders.fieldSort(CREATE_TIME.getFieldName()).order(SortOrder.DESC))
                .build();
        EsLog.info("查询商户", query);
        // 执行es并解析数据
        return elasticsearchTemplate.query(query, response -> parseListMerchantEsResult(response, merchantSearchBean, page));
    }

    /**
     * 解析"查询商户列表"es返回的数据
     *
     * @param response     es返回的响应体
     * @param searchParams 查询参数-根据排序规则,需要二次查询
     * @param pageRequest  分页信息-根据排序规则,需要在汇总中分页
     */
    private static Tuple<List<MerchantEsResultBean>, Long> parseListMerchantEsResult(SearchResponse response, MerchantSearchBean searchParams, PageRequest pageRequest) {
        if (searchParams.getSearchType() == SearchType.QUALITY) {
            return parseOtherSortResult(response, CURRENT_MONTH, PageRequest.of(pageRequest.getPageNumber(), 500), false, searchParams);
        }
        // 根据排序规则解析数据
        switch (searchParams.getSortType()) {
            case LAST_MONTH_TRANS_DESC:
            case LAST_MONTH_TRANS_ASC:
                return parseOtherSortResult(response, LAST_MONTH, pageRequest, searchParams.getSortType() == SortType.LAST_MONTH_TRANS_ASC, searchParams);
            case CUR_MONTH_TRANS_DESC:
            case CUR_MONTH_TRANS_ASC:
                return parseOtherSortResult(response, CURRENT_MONTH, pageRequest, searchParams.getSortType() == SortType.CUR_MONTH_TRANS_ASC, searchParams);
            case ALL_TRANS_DESC:
            case ALL_TRANS_ASC:
                return parseOtherSortResult(response, ALL_TIME, pageRequest, searchParams.getSortType() == SortType.ALL_TRANS_ASC, searchParams);
            case DEFAULT_ORDER:
            default:
                return parseDefaultSortResult(response);
        }
    }

    /**
     * 默认排序(按创建时间倒序排)的解析规则
     *
     * @param response es返回的响应体
     */
    private static Tuple<List<MerchantEsResultBean>, Long> parseDefaultSortResult(SearchResponse response) {
        long totalHits = response.getHits().getTotalHits();
        List<String> merchantNoList = new ArrayList<>();
        List<MerchantEsResultBean> resultList = new ArrayList<>();

        // 先从结果解析出商户的数据
        Arrays.stream(response.getHits().getHits()).forEach(item -> {
            Map<String, Object> sourceAsMap = item.getSourceAsMap();
            String merchantNo = Objects.toString(sourceAsMap.get(MERCHANT_NO.getFieldName()));
            String mobilePhone = Objects.toString(sourceAsMap.get(MOBILE_PHONE.getFieldName()));
            String merchantName = Objects.toString(sourceAsMap.get(MERCHANT_NAME.getFieldName()));
            String agentNo = Objects.toString(sourceAsMap.get(AGENT_NO.getFieldName()));

            MerchantEsResultBean merchantEsResultBean = new MerchantEsResultBean();
            merchantEsResultBean.setMerchantNo(merchantNo);
            merchantEsResultBean.setMobilePhone(mobilePhone);
            merchantEsResultBean.setMerchantName(merchantName);
            merchantEsResultBean.setTransMoney("0.00");
            merchantEsResultBean.setAgentNo(agentNo);
            resultList.add(merchantEsResultBean);
            merchantNoList.add(merchantNo);
        });
        if (CollectionUtils.isEmpty(merchantNoList)) {
            return new Tuple<>(resultList, totalHits);
        }
        LocalDateTime now = LocalDateTime.now();
        // 再根据解析出来的商户数据,查询对应的当月交易数据
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                // 查询交易订单
                .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), ORDER.getTypeName()))
                // 必须是成功的订单
                .must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                // 交易时间为当月
                .must(QueryBuilders.rangeQuery(TRANS_TIME.getFieldName())
                        .gte(now.format(YYYY_MM_01_00_00_00)))
                // 根据交易商户查询
                .must(QueryBuilders.termsQuery(MERCHANT_NO.getFieldName(), merchantNoList));

        // 根据商户进行汇总
        TermsAggregationBuilder groupByMerchant = AggregationBuilders.terms(GROUP_BY_MERCHANT)
                .field(MERCHANT_NO.getFieldName());

        // 对订单的交易金额进行汇总
        SumAggregationBuilder sumOfTransAmount = AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName());
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(groupByMerchant
                        .size(merchantNoList.size())
                        .subAggregation(sumOfTransAmount))
                .build();
        EsLog.info("对订单的交易金额进行汇总", searchQuery);
        // 二次查询,解析当月商户的交易金额 key为商户编号,value为商户当月交易金额
        Map<String, String> sumMap = elasticsearchTemplate.query(searchQuery, resp -> {
            Aggregation aggregation = resp.getAggregations().getAsMap().get(GROUP_BY_MERCHANT);
            if (aggregation instanceof UnmappedTerms) {
                return null;
            }
            StringTerms stringTerms = (StringTerms) aggregation;
            return stringTerms.getBuckets()
                    .stream()
                    .map(item -> new Tuple<>(Objects.toString(item.getKey()), ((InternalSum) item.getAggregations().get(SUM_OF_TRANS_AMOUNT)).getValue()))
                    .collect(Collectors.toMap(Tuple::v1, item -> rounding(item.v2())));
        });
        if (sumMap == null) {
            return new Tuple<>(resultList, totalHits);
        }
        for (MerchantEsResultBean item : resultList) {
            String sum = Optional.ofNullable(sumMap.get(item.getMerchantNo())).orElse("0.0");
            item.setTransMoney(sum);
        }
        return new Tuple<>(resultList, totalHits);
    }

    /**
     * 对double类型四舍五入
     */
    private static String rounding(double d) {
        return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    /**
     * 解析除默认排序之后的数据
     * 因为其实排序规则,取的数据其实是汇总结果返回的数据
     * 然后从汇总结果得到商户编号,再通过商户编号查商户的一下基本信息
     *
     * @param response    es返回结果
     * @param filterName  交易过滤的名字
     * @param pageRequest 分页信息
     * @return
     */
    private static Tuple<List<MerchantEsResultBean>, Long> parseOtherSortResult(SearchResponse response, String filterName,
                                                                                PageRequest pageRequest, boolean isAsc,
                                                                                MerchantSearchBean searchParams) {
        // 先获取根据商户汇总之后的数据,有可能汇总数据为空
        Aggregation aggregation = response.getAggregations().getAsMap().get(GROUP_BY_MERCHANT);
        // 为空时返回默认数据
        if (aggregation instanceof UnmappedTerms) {
            return new Tuple<>(new ArrayList<>(), 0L);
        }
        // 商户查询结果的总条数
        long totalHits = response.getHits().getTotalHits();

        StringTerms stringTerms = (StringTerms) aggregation;
        if (searchParams.getSearchType() == SearchType.QUALITY) {
            totalHits = stringTerms.getBuckets().size();
        }
        // 查询商户-商户对应查询条件的交易总额
        List<String> merchantNoList = new ArrayList<>();
        Map<String, String> merchantSumMap = new TreeMap<>();
        stringTerms.getBuckets()
                .stream()
                .skip(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .forEach(item -> {
                    String key = Objects.toString(item.getKey());
                    InternalChildren internalChildren = (InternalChildren) item.getAggregations().getAsMap().get(CHILDREN_OF_ORDER);
                    InternalFilter internalFilter = (InternalFilter) internalChildren.getAggregations().getAsMap().get(filterName);
                    InternalSum internalSum = (InternalSum) internalFilter.getAggregations().getAsMap().get(SUM_OF_TRANS_AMOUNT);
                    merchantSumMap.put(key, rounding(internalSum.getValue()));
                    merchantNoList.add(key);
                });

        // 之后二次查询es,获取商户的基本信息
        List<MerchantEsResultBean> merchantEsResultList = queryMerchantByMerchantNo(merchantNoList);
        merchantEsResultList.forEach(item -> item.setTransMoney(Optional.ofNullable(merchantSumMap.get(item.getMerchantNo())).orElse("0.0")));
        merchantEsResultList.sort((item1, item2) -> {
            BigDecimal bigDecimal = new BigDecimal(item1.getTransMoney());
            BigDecimal bigDecima2 = new BigDecimal(item2.getTransMoney());
            return isAsc ? bigDecimal.compareTo(bigDecima2) : bigDecima2.compareTo(bigDecimal);
        });
        return new Tuple<>(merchantEsResultList, totalHits);
    }

    /**
     * 根据商户编号查询商户基本信息
     *
     * @param merchantNoCollection 商户编号列表
     */
    private static List<MerchantEsResultBean> queryMerchantByMerchantNo(Collection<String> merchantNoCollection) {
        if (CollectionUtils.isEmpty(merchantNoCollection)) {
            return new ArrayList<>();
        }
        List<String> merchantNos = new ArrayList<>(merchantNoCollection);
        return merchantEsService.listMerchantByNos(merchantNos);
    }

    /**
     * 根据排序条件汇总
     * 当月交易额升降序:        需要查当月的交易量
     * 上个月交易额升降序:      需要查上个月的交易量
     * 总交易额升降序:          需要查总的交易量
     */
    private static AbstractAggregationBuilder merchantAggregation(MerchantSearchBean result, PageRequest page) {
        LocalDateTime now = LocalDateTime.now();
        // 根据商户汇总
        TermsAggregationBuilder groupByMerchant = AggregationBuilders.terms(GROUP_BY_MERCHANT)
                .field(MERCHANT_NO.getFieldName());
        // 商户根据订单进行汇总
        ChildrenAggregationBuilder childrenOfOrder = JoinAggregationBuilders.children(CHILDREN_OF_ORDER, ORDER.getTypeName());
        // 总交易额升降序 -> 全部交易且交易状态必须为成功
        FilterAggregationBuilder filterOfAllTrans = AggregationBuilders.filter(ALL_TIME,
                QueryBuilders.boolQuery().must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS)));
        // 当月交易额升降序 -> 当月交易且交易状态必须为成功
        FilterAggregationBuilder filterOfCurrentMonthTrans = AggregationBuilders.filter(CURRENT_MONTH,
                QueryBuilders.boolQuery().must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                        .must(QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(now.format(YYYY_MM_01_00_00_00))));
        // 上个月交易额升降序 -> 上个月交易且交易状态为成功的
        FilterAggregationBuilder filterOfLastMonthTrans = AggregationBuilders.filter(LAST_MONTH,
                QueryBuilders.boolQuery().must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                        .must(QueryBuilders.rangeQuery(TRANS_TIME.getFieldName())
                                .gte(now.minusMonths(1).format(YYYY_MM_01_00_00_00))
                                .lte(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59))));
        // 对订单的交易金额进行汇总
        SumAggregationBuilder sumOfTransAmount = AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName());

        // 过滤优质商户的汇总
        Map<String, String> bucketsPathMap = new HashMap<>();
        String currentMonthSumOfTransAmount = "current_month_sum_of_trans_amount";
        bucketsPathMap.put(currentMonthSumOfTransAmount, String.format("%s>%s.%s", CHILDREN_OF_ORDER, CURRENT_MONTH, SUM_OF_TRANS_AMOUNT));
        Script script = new Script(String.format("params.%s >= %s",
                currentMonthSumOfTransAmount, sysConfigService.getSysConfigValueByKey(SYS_CONFIG_QUALITY_SEARCH_CUR_MONTH_TRANS_MONEY, "50000", Function.identity())));
        BucketSelectorPipelineAggregationBuilder filterQualityMerchant = PipelineAggregatorBuilders.bucketSelector(FILTER_QUALITY_MERCHANT, bucketsPathMap, script);

        if (result.getSearchType() == SearchType.QUALITY) {
            return groupByMerchant
                    .size(Integer.MAX_VALUE)
                    .order(BucketOrder.aggregation(String.format("%s>%s>%s",
                            CHILDREN_OF_ORDER, CURRENT_MONTH, SUM_OF_TRANS_AMOUNT), false))
                    .subAggregation(childrenOfOrder
                            .subAggregation(filterOfCurrentMonthTrans
                                    .subAggregation(sumOfTransAmount)))
                    .subAggregation(filterQualityMerchant);
        }

        // 根据排序规则,定制不同的汇总规则
        switch (result.getSortType()) {
            case ALL_TRANS_ASC:
            case ALL_TRANS_DESC:
                return groupByMerchant
                        .size((int) (page.getOffset() + page.getPageSize()))
                        .order(BucketOrder.aggregation(String.format("%s>%s>%s",
                                CHILDREN_OF_ORDER, ALL_TIME, SUM_OF_TRANS_AMOUNT),
                                result.getSortType() == SortType.ALL_TRANS_ASC))
                        .subAggregation(childrenOfOrder
                                .subAggregation(filterOfAllTrans
                                        .subAggregation(sumOfTransAmount)));
            case CUR_MONTH_TRANS_ASC:
            case CUR_MONTH_TRANS_DESC:
                return groupByMerchant
                        .size((int) (page.getOffset() + page.getPageSize()))
                        .order(BucketOrder
                                .aggregation(String.format("%s>%s>%s", CHILDREN_OF_ORDER, CURRENT_MONTH, SUM_OF_TRANS_AMOUNT),
                                        result.getSortType() == SortType.CUR_MONTH_TRANS_ASC))
                        .subAggregation(childrenOfOrder
                                .subAggregation(filterOfCurrentMonthTrans
                                        .subAggregation(sumOfTransAmount)));
            case LAST_MONTH_TRANS_ASC:
            case LAST_MONTH_TRANS_DESC:
                return groupByMerchant
                        .size((int) (page.getOffset() + page.getPageSize()))
                        .order(BucketOrder
                                .aggregation(String.format("%s>%s>%s", CHILDREN_OF_ORDER, LAST_MONTH, SUM_OF_TRANS_AMOUNT),
                                        result.getSortType() == SortType.LAST_MONTH_TRANS_ASC))
                        .subAggregation(childrenOfOrder
                                .subAggregation(filterOfLastMonthTrans
                                        .subAggregation(sumOfTransAmount)));
            case DEFAULT_ORDER:
            default:
                return groupByMerchant;
        }
    }

    /**
     * 构造查询条件
     */
    private static QueryBuilder merchantQueryBuilder(MerchantSearchBean result, String agentNode) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builderQueryScope4Merchant(result.getQueryScope(), agentNode, builder);
        // 查询类型为商户
        builder.must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()));
        //  开通业务产品 or 商户状态 or 未认证商户
        if (StringUtils.isNotBlank(result.getTheOpenBpId()) ||
                StringUtils.isNotBlank(result.getMerchantStatus()) ||
                result.getSearchType() == SearchType.UNCERTIFIED) {
            BoolQueryBuilder mbpBuilder = QueryBuilders.boolQuery();
            if (StringUtils.isNotBlank(result.getTheOpenBpId())) {
                mbpBuilder.must(QueryBuilders.termQuery(BP_ID.getFieldName(), result.getTheOpenBpId()));
            }
            if (StringUtils.isNotBlank(result.getMerchantStatus())) {
                mbpBuilder.must(QueryBuilders.termQuery(STATUS.getFieldName(), result.getMerchantStatus()));
            }
            if (result.getSearchType() == SearchType.UNCERTIFIED) {
                mbpBuilder.must(QueryBuilders.termsQuery(STATUS.getFieldName(), "1", "2", "3"));
            }
            builder.must(JoinQueryBuilders.hasChildQuery(MBP.getTypeName(), mbpBuilder, ScoreMode.None));
        }
        // 所属组织
        if (StringUtils.isNotBlank(result.getTeamId())) {
            builder.must(QueryBuilders.termQuery(TEAM_ID.getFieldName(), result.getTeamId()));
        }
        // 所属子组织    todo4lvsw 这里需要验证
//        if (StringUtils.isNotBlank(result.getTeamEntryId())) {
//            builder.must(QueryBuilders.termQuery(TEAM_ENTRY_ID.getKeyword(), result.getTeamEntryId()));
//        }
        // 欢乐返激活状态
        if (StringUtils.isNotBlank(result.getHlfActive())) {
            builder.must(QueryBuilders.termQuery(HLF_ACTIVE.getFieldName(), result.getHlfActive()));
        }
        // 推广来源
        if (StringUtils.isNotBlank(result.getRecommendedSource())) {
            builder.must(QueryBuilders.termQuery(RECOMMENDED_SOURCE.getFieldName(), result.getRecommendedSource()));
        }
        // 冻结状态
        if (StringUtils.isNotBlank(result.getRiskStatus())) {
            builder.must(QueryBuilders.termQuery(RISK_STATUS.getFieldName(), result.getRiskStatus()));
        }
        if (StringUtils.isNotBlank(result.getSearchKeywork())) {
            builder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchPhraseQuery(MERCHANT_NAME.getFieldName(), result.getSearchKeywork()))
                    .should(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), result.getSearchKeywork()))
                    .should(QueryBuilders.termQuery(MOBILE_PHONE.getFieldName(), result.getSearchKeywork()))
            );
        }
        // 商户名称
        if (StringUtils.isNotBlank(result.getMerchantName())) {
            builder.must(QueryBuilders.matchPhraseQuery(MERCHANT_NAME.getFieldName(), result.getMerchantName()));
        }
        // 商户编号
        if (StringUtils.isNotBlank(result.getMerchantNo())) {
            builder.must(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), result.getMerchantNo()));
        }
        // 未开通的业务产品
        if (StringUtils.isNotBlank(result.getTheUnOpenBpId())) {
            BoolQueryBuilder mbpQueryBuilder = QueryBuilders.boolQuery();
            if (StringUtils.isNotBlank(result.getTheUnOpenBpId())) {
                mbpQueryBuilder.must(QueryBuilders.termQuery(BP_ID.getFieldName(), result.getTheUnOpenBpId()));
            }
            builder.mustNot(JoinQueryBuilders.hasChildQuery(MBP.getTypeName(), mbpQueryBuilder, ScoreMode.None));
        }
        // 手机号
        if (StringUtils.isNotBlank(result.getMobilePhone())) {
            builder.must(QueryBuilders.termQuery(MOBILE_PHONE.getFieldName(), result.getMobilePhone()));
        }
        builderTransQuery(builder, result);
        // 省份
        if (StringUtils.isNotBlank(result.getProvince())) {
            builder.must(QueryBuilders.termQuery(PROVINCE.getKey(), result.getProvince()));
        }
        // 城市
        if (StringUtils.isNotBlank(result.getCity())) {
            builder.must(QueryBuilders.termQuery(CITY.getKey(), result.getCity()));
        }
        // 区域
        if (StringUtils.isNotBlank(result.getDistrict())) {
            builder.must(QueryBuilders.termQuery(DISTRICT.getKey(), result.getDistrict()));
        }
        // 商户开始创建时间
        if (StringUtils.isNotBlank(result.getStartCreateTime()) ||
                StringUtils.isNotBlank(result.getEndCreateTime())) {
            RangeQueryBuilder merchantCreateTime = QueryBuilders.rangeQuery(CREATE_TIME.getFieldName());
            if (StringUtils.isNotBlank(result.getStartCreateTime())) {
                merchantCreateTime.gte(result.getStartCreateTime());
            }
            if (StringUtils.isNotBlank(result.getEndCreateTime())) {
                merchantCreateTime.lte(result.getEndCreateTime());
            }
            builder.must(merchantCreateTime);
        }
        // 是否特约商户，根据是否存在acq_merchant_no字段来判断
//        if (StringUtils.isNotBlank(result.getSpecialMerFlag())) {
//            if (SpecialMerFlag.YES.getFlag().equals(result.getSpecialMerFlag())) {
//                builder.must(QueryBuilders.existsQuery(ACQ_MERCHANT_NO.getFieldName()));
//            } else {
//                builder.mustNot(QueryBuilders.existsQuery(ACQ_MERCHANT_NO.getFieldName()));
//            }
//        }
        return builder;
    }

    /**
     * 为代理商构造查询条件
     *
     * @param queryScope   查询范围
     * @param agentNode    查询代理商节点
     * @param loginAgentNo 登陆代理商编号
     * @param builder      查询条件
     */
    public static void builderQueryScope4Agent(QueryScope queryScope, String agentNode, String loginAgentNo, BoolQueryBuilder builder) {
        // 不包含登陆代理商本身
        builder.mustNot(QueryBuilders.termQuery(AGENT_NO.getFieldName(), loginAgentNo));
        QueryScope temp = Optional.ofNullable(queryScope).orElse(QueryScope.ALL);
        switch (temp) {
            case ALL:
                // 查询跟agentNode链条下的数据
                builder.must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)));
                break;
            case OFFICAL:
                // 查询父节点为登陆代理商编号
                builder.must(QueryBuilders.termQuery(PARENT_ID.getFieldName(), loginAgentNo));
                break;
            case CHILDREN:
                // 查询跟agentNode链条下的数据且父级id不为登陆代理商编号
                builder.must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                        .mustNot(QueryBuilders.termQuery(PARENT_ID.getFieldName(), loginAgentNo));
                break;
        }
    }

    /**
     * 为商户构造查询条件
     *
     * @param queryScope 查询范围
     * @param agentNode  查询代理商节点
     * @param builder    查询条件
     */
    public static void builderQueryScope4Merchant(QueryScope queryScope, String agentNode, BoolQueryBuilder builder) {
        QueryScope temp = Optional.ofNullable(queryScope).orElse(QueryScope.ALL);
        switch (temp) {
            case ALL:
                // 查询跟agentNode链条下的数据
                builder.must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)));
                break;
            case OFFICAL:
                // 查询agentNode节点完全匹配的
                builder.must(QueryBuilders.termQuery(AGENT_NODE.getKey(), agentNode));
                break;
            case CHILDREN:
                // 查询跟agentNode链条下的数据且不是完全匹配
                builder.must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                        .mustNot(QueryBuilders.termQuery(AGENT_NODE.getKey(), agentNode));
                break;
        }
    }

    /**
     * 构造跟交易相关的基本的查询条件
     */
    private static void builderTransQuery(BoolQueryBuilder builder, MerchantSearchBean result) {
        // 如果跟交易相关的条件都为空,则跳过
        if (StringUtils.isBlank(result.getStartTransTime()) &&
                StringUtils.isBlank(result.getEndTransTime()) &&
                StringUtils.isBlank(result.getMinOrderNum()) &&
                StringUtils.isBlank(result.getMaxOrderNum()) &&
                StringUtils.isBlank(result.getMinTransMoney()) &&
                StringUtils.isBlank(result.getMaxTransMoney())
        ) {
            return;
        }
        BoolQueryBuilder transBoolBuild = QueryBuilders.boolQuery();
        transBoolBuild.must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), "SUCCESS"));
        // 交易时间有一个不为空
        if (StringUtils.isNotBlank(result.getStartTransTime()) ||
                StringUtils.isNotBlank(result.getEndTransTime())) {
            RangeQueryBuilder transTimeQueryBuilder = QueryBuilders.rangeQuery(TRANS_TIME.getFieldName());
            // 开始交易时间
            if (StringUtils.isNotBlank(result.getStartTransTime())) {
                transTimeQueryBuilder.gte(result.getStartTransTime());
            }
            // 结束交易时间
            if (StringUtils.isNotBlank(result.getEndTransTime())) {
                transTimeQueryBuilder.lte(result.getEndTransTime());
            }
            transBoolBuild.must(transTimeQueryBuilder);
        }
        // 交易金额有一个不为空
        if (StringUtils.isNotBlank(result.getMinTransMoney()) ||
                StringUtils.isNotBlank(result.getMaxTransMoney())) {
            RangeQueryBuilder transAmountQueryBuilder = QueryBuilders.rangeQuery(TRANS_AMOUNT.getFieldName());
            if (StringUtils.isNotBlank(result.getMinTransMoney())) {
                transAmountQueryBuilder.gte(result.getMinTransMoney());
            }
            if (StringUtils.isNotBlank(result.getMaxTransMoney())) {
                transAmountQueryBuilder.lte(result.getMaxTransMoney());
            }
            transBoolBuild.must(transAmountQueryBuilder);
        }
        // 交易笔数
        Integer minOrderNum = 0;
        try {
            minOrderNum = Integer.valueOf(result.getMinOrderNum());
            if (minOrderNum < 0) {
                minOrderNum = 0;
            }
        } catch (NumberFormatException e) {
        }
        Integer maxOrderNum = Integer.MAX_VALUE;
        try {
            maxOrderNum = Integer.valueOf(result.getMaxOrderNum());
            if (maxOrderNum < 0) {
                maxOrderNum = 0;
            }
        } catch (NumberFormatException e) {
        }
        // 如果是休眠商户查询,则查询条件是不能包含被查询到的订单
        // 如果最大值小于最小值,对调
        if (maxOrderNum < minOrderNum) {
            int temp = minOrderNum;
            minOrderNum = maxOrderNum;
            maxOrderNum = temp;
        }
        // 如果最大订单数为0,则是查不存在的订单的商户,否则就是查存在最大订单数的商户
        if (maxOrderNum == 0) {
            builder.mustNot(JoinQueryBuilders.hasChildQuery(ORDER.getTypeName(), transBoolBuild, ScoreMode.None).minMaxChildren(1, Integer.MAX_VALUE));
        } else {
            builder.must(JoinQueryBuilders.hasChildQuery(ORDER.getTypeName(), transBoolBuild, ScoreMode.None).minMaxChildren(minOrderNum, maxOrderNum));
        }
//        if (result.getSearchType() == SearchType.SLEEP) {
//            builder.mustNot(JoinQueryBuilders.hasChildQuery(ORDER.getTypeName(), transBoolBuild, ScoreMode.None).minMaxChildren(minOrderNum, maxOrderNum));
//        } else {
//            builder.must(JoinQueryBuilders.hasChildQuery(ORDER.getTypeName(), transBoolBuild, ScoreMode.None).minMaxChildren(minOrderNum, maxOrderNum));
//        }
    }

    /**
     * 根据查询类型解析查询参数
     */
    public static MerchantSearchBean parseSearchParamBySearchType(MerchantSearchBean searchParams) {
        if (searchParams == null) {
            return defaultMerchantSearch();
        }
        searchParams.setSearchType(Optional.ofNullable(searchParams.getSearchType()).orElse(SearchType.QUERY));
        MerchantSearchBean result;
        switch (searchParams.getSearchType()) {
            case QUERY:
                result = buildQueryMerchantSearch(searchParams);
                break;
            case ALL:
                result = buildAllMerchantSearch(searchParams);
                break;
            case QUALITY:
                result = buildQualityMerchantSearch(searchParams);
                break;
            case ACTIVE:
                result = buildActiveMerchantSearch(searchParams);
                break;
            case UNCERTIFIED:
                result = buildUncertifiedMerchantSearch(searchParams);
                break;
            case SLEEP:
                result = buildSleepMerchantSearch(searchParams);
                break;
            default:    // never go here
                result = defaultMerchantSearch();
                break;
        }
        return result;
    }

    /**
     * 构造休眠商户
     * 入网≥X天,连续无交易大于X天
     * 默认 入网≥60天,连续无交易大于60天
     */
    private static MerchantSearchBean buildSleepMerchantSearch(MerchantSearchBean searchParams) {
//        MerchantSearchBean result = new MerchantSearchBean();
        MerchantSearchBean result = buildQueryMerchantSearch(searchParams);
        result.setSearchType(SearchType.SLEEP);
        result.setQueryScope(Optional.ofNullable(searchParams.getQueryScope()).orElse(QueryScope.ALL));
        result.setSortType(Optional.ofNullable(searchParams.getSortType()).orElse(SortType.DEFAULT_ORDER));

        int merchantCreate = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_SLEEP_SEARCH_MERCHANT_CREATE, 60, Integer::valueOf);
        int transTime = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_SLEEP_SEARCH_TRANS_TIME, 60, Integer::valueOf);
        LocalDateTime now = LocalDateTime.now();
        result.setEndCreateTime(now.minusDays(merchantCreate).format(YYYY_MM_DD_00_00_00));
        result.setStartTransTime(now.minusDays(transTime).format(YYYY_MM_DD_00_00_00));
        result.setMaxOrderNum("0");
        return result;
    }

    /**
     * 构造未认证商户
     */
    private static MerchantSearchBean buildUncertifiedMerchantSearch(MerchantSearchBean searchParams) {
//        MerchantSearchBean result = new MerchantSearchBean();
        MerchantSearchBean result = buildQueryMerchantSearch(searchParams);
        result.setSearchType(SearchType.UNCERTIFIED);
        result.setQueryScope(Optional.ofNullable(searchParams.getQueryScope()).orElse(QueryScope.ALL));
        result.setSortType(Optional.ofNullable(searchParams.getSortType()).orElse(SortType.DEFAULT_ORDER));
        return result;
    }

    /**
     * 构造活跃商户查询条件
     * 近x天交易笔数>=x笔,且交易金额>=x元
     * 默认 近30天交易笔数≥2笔并交易金额≥10元
     */
    private static MerchantSearchBean buildActiveMerchantSearch(MerchantSearchBean searchParams) {
//        MerchantSearchBean result = new MerchantSearchBean();
        MerchantSearchBean result = buildQueryMerchantSearch(searchParams);
        result.setSearchType(SearchType.ACTIVE);
        result.setQueryScope(Optional.ofNullable(searchParams.getQueryScope()).orElse(QueryScope.ALL));
        result.setSortType(Optional.ofNullable(searchParams.getSortType()).orElse(SortType.DEFAULT_ORDER));

        int transDay = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_ACTIVE_SEARCH_TRANS_DAY, 30, Integer::valueOf);
        int orderNum = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_ACTIVE_SEARCH_TRANS_ORDER_NUM, 2, Integer::valueOf);
        String transMoney = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_ACTIVE_SEARCH_TRANS_MONEY, "10", Function.identity());
        LocalDate now = LocalDate.now();
        result.setStartTransTime(now.minusDays(transDay).format(YYYY_MM_DD_00_00_00));
        result.setMinOrderNum(orderNum + "");
        result.setMinTransMoney(transMoney);
        return result;
    }

    /**
     * 构造查询优质商户的条件
     * 本月交易金额>=x元
     * 默认: 本月交易金额≥50000元
     */
    private static MerchantSearchBean buildQualityMerchantSearch(MerchantSearchBean searchParams) {
//        MerchantSearchBean result = new MerchantSearchBean();
        MerchantSearchBean result = buildQueryMerchantSearch(searchParams);
        result.setSearchType(SearchType.QUALITY);
        result.setQueryScope(Optional.ofNullable(searchParams.getQueryScope()).orElse(QueryScope.ALL));
        result.setSortType(Optional.ofNullable(searchParams.getSortType()).orElse(SortType.DEFAULT_ORDER));

        LocalDateTime now = LocalDateTime.now();
        result.setStartTransTime(now.format(YYYY_MM_01_00_00_00));
        result.setEndTransTime(now.format(YYYY_MM_DD_HH_MM_SS));
//        result.setMinTransMoney(sysConfigService.getSysConfigValueByKey(SYS_CONFIG_QUALITY_SEARCH_CUR_MONTH_TRANS_MONEY, "50000", Function.identity()));
        return result;
    }

    /**
     * 构造查询全部商户的条件
     */
    private static MerchantSearchBean buildAllMerchantSearch(MerchantSearchBean searchParams) {
//        MerchantSearchBean result = new MerchantSearchBean();
        MerchantSearchBean result = buildQueryMerchantSearch(searchParams);
        result.setSearchType(SearchType.ALL);
        result.setQueryScope(Optional.ofNullable(searchParams.getQueryScope()).orElse(QueryScope.ALL));
        result.setSortType(Optional.ofNullable(searchParams.getSortType()).orElse(SortType.DEFAULT_ORDER));
        return result;
    }

    /**
     * 构造筛选查询的条件
     */
    private static MerchantSearchBean buildQueryMerchantSearch(MerchantSearchBean searchParams) {
        // 反序列化,重新生成一个新的对象
        MerchantSearchBean result = GsonUtils.fromJson2Bean(GsonUtils.toJson(searchParams), MerchantSearchBean.class);
        result.setSearchType(SearchType.QUERY);
        result.setQueryScope(Optional.ofNullable(searchParams.getQueryScope()).orElse(QueryScope.ALL));
        result.setSortType(Optional.ofNullable(searchParams.getSortType()).orElse(SortType.DEFAULT_ORDER));
        return result;
    }

    /**
     * 默认查询条件
     */
    private static MerchantSearchBean defaultMerchantSearch() {
        MerchantSearchBean result = new MerchantSearchBean();
        result.setSearchType(SearchType.ALL);
        result.setQueryScope(QueryScope.ALL);
        result.setSortType(SortType.DEFAULT_ORDER);
        return result;
    }

    /**
     * 汇总所有商户数量/激活数
     *
     * @param queryScope 查询范围
     * @param agentNode  代理商节点
     */
    public static MerchantSumBean queryAllMerchant(QueryScope queryScope, String agentNode) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builderQueryScope4Merchant(queryScope, agentNode, boolQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()));
        FilterAggregationBuilder filter = AggregationBuilders.filter(MERCHANT_HAS_ACTIVE,
                QueryBuilders.termQuery(HLF_ACTIVE.getFieldName(), "1"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(filter)
                .build();
        EsLog.info("查询所有商户信息", searchQuery);
        return getMerchantSumBean(searchQuery);
    }

    private static MerchantSumBean getMerchantSumBean(SearchQuery searchQuery) {
        return elasticsearchTemplate.query(searchQuery, response -> {
            long totalHits = response.getHits().getTotalHits();
            long active = ((InternalFilter) response.getAggregations().getAsMap().get(MERCHANT_HAS_ACTIVE)).getDocCount();
            MerchantSumBean merchantSumBean = new MerchantSumBean();
            merchantSumBean.setTotal(totalHits);
            merchantSumBean.setActiveNumber(active);
            merchantSumBean.setNotActiveNumber(totalHits - active);
            return merchantSumBean;
        });
    }

    /**
     * 查询或说代理商数量
     *
     * @param queryScope 查询范围
     * @param agentNode  代理商节点
     */
    public static long queryAllAgent(QueryScope queryScope, String agentNo, String agentNode) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builderQueryScope4Agent(queryScope, agentNode, agentNo, boolQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), AGENT.getTypeName()));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .build();
        return elasticsearchTemplate.count(searchQuery);
    }

    /**
     * 查询当月数据
     *
     * @param queryScope 查询范围
     * @param agentNo    登陆代理商
     * @param agentNode  查询代理商节点
     * @param type       es join type类型
     * @see com.esdemo.frame.enums.EsNpospJoinType#AGENT
     * @see com.esdemo.frame.enums.EsNpospJoinType#MERCHANT
     */
    public static MerchantSumBean queryCurrentMonthData(QueryScope queryScope, String agentNo, String agentNode, EsNpospJoinType type) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (type == EsNpospJoinType.AGENT) {
            builderQueryScope4Agent(queryScope, agentNode, agentNo, boolQueryBuilder);
        } else {
            builderQueryScope4Merchant(queryScope, agentNode, boolQueryBuilder);
        }
        boolQueryBuilder.must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), type.getTypeName()));
        LocalDateTime now = LocalDateTime.now();
        // 当月数据
        FilterAggregationBuilder currentMonth = AggregationBuilders.filter(CURRENT_MONTH,
                QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName()).gte(now.format(YYYY_MM_01_00_00_00))));
        // 上个月数据
        FilterAggregationBuilder lastMonth = AggregationBuilders.filter(LAST_MONTH,
                QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName())
                        .gte(now.minusMonths(1).format(YYYY_MM_01_00_00_00))
                        .lte(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59))));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(currentMonth)
                .addAggregation(lastMonth)
                .build();
        EsLog.info("查询当月信息", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
            long current = ((InternalFilter) asMap.get(CURRENT_MONTH)).getDocCount();
            long last = ((InternalFilter) asMap.get(LAST_MONTH)).getDocCount();
            MerchantSumBean merchantSumBean = new MerchantSumBean();
            merchantSumBean.setTotal(current);
            if (last == 0 || current == last) {
                merchantSumBean.setRate("--");
            } else {
                merchantSumBean.setRate(rounding((current - last) * 100.0 / last) + "%");
            }
            return merchantSumBean;
        });
    }

    /**
     * 查询7日和半年商户/代理商新增趋势
     *
     * @param queryScope   查询范围
     * @param loginAgentNo 代理商编号
     * @param agentNode    代理商节点
     * @param type         类型
     * @return v1 7日数据
     * v2 半年数据
     * @see com.esdemo.frame.enums.EsNpospJoinType#AGENT
     * @see com.esdemo.frame.enums.EsNpospJoinType#MERCHANT
     */
    public Tuple<List<KeyValueBean>, List<KeyValueBean>> listSevenDayAndHalfYearNewly(QueryScope queryScope, String loginAgentNo, String agentNode, EsNpospJoinType type) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (type == EsNpospJoinType.AGENT) {
            builderQueryScope4Agent(queryScope, agentNode, loginAgentNo, boolQueryBuilder);
        } else {
            builderQueryScope4Merchant(queryScope, agentNode, boolQueryBuilder);
        }
        boolQueryBuilder.must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), type.getTypeName()));

        LocalDateTime now = LocalDateTime.now();
        DateRangeAggregationBuilder seventDays = AggregationBuilders.dateRange(SEVEN_DAY_AGO).field(CREATE_TIME.getFieldName());
        for (int i = 0; i < 7; i++) {
            LocalDateTime day = now.minusDays(i + 1L);
            seventDays.addRange(day.format(MM_DD), day.format(YYYY_MM_DD_00_00_00), day.format(YYYY_MM_DD_23_59_59));
        }
        DateRangeAggregationBuilder halfYear = AggregationBuilders.dateRange(HALF_YEAR_AGO).field(CREATE_TIME.getFieldName());
        for (int i = 0; i < 6; i++) {
            LocalDateTime localDateTime = now.minusMonths(i + 1L);
            String start = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).format(YYYY_MM_DD_00_00_00);
            String end = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59);
            String key = localDateTime.format(YYYY_MM);
            halfYear.addRange(key, start, end);
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(seventDays)
                .addAggregation(halfYear)
                .build();
        EsLog.info("新增商户趋势", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
            InternalDateRange seventDateRange = (InternalDateRange) aggregationMap.get(SEVEN_DAY_AGO);
            List<KeyValueBean> seventDayList = seventDateRange.getBuckets()
                    .stream()
                    .map(item -> new KeyValueBean(item.getKey(), item.getDocCount() + "", ""))
                    .collect(Collectors.toList());

            InternalDateRange halfYearRange = (InternalDateRange) aggregationMap.get(HALF_YEAR_AGO);
            List<KeyValueBean> halfYearList = halfYearRange.getBuckets()
                    .stream()
                    .map(item -> new KeyValueBean(item.getKey(), item.getDocCount() + "", ""))
                    .collect(Collectors.toList());
            return new Tuple<>(seventDayList, halfYearList);
        });
    }

    /**
     * 长时间没交易的商户
     *
     * @param queryScope 查询范围
     * @param agentNode  代理商节点
     * @param days       时间
     * @return
     */
    public static Tuple<List<MerchantEsResultBean>, Long> merchantOfLongTimeNoTrans(QueryScope queryScope, String agentNode, long days, PageRequest page) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builderQueryScope4Merchant(queryScope, agentNode, boolQueryBuilder);

        LocalDateTime now = LocalDateTime.now();
        String theDaysAgo = now.minusDays(days).format(YYYY_MM_DD_HH_MM_SS);

        // 查询商户
        boolQueryBuilder.must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()))
                // 商户必须是指定时间之前创建的
                .must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName()).lte(theDaysAgo))
                // 筛选最后一笔成功交易
                .should(JoinQueryBuilders.hasChildQuery(ORDER.getTypeName(),
                        QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS), ScoreMode.None)
                        .innerHit(new InnerHitBuilder("last_trans_order")
                                .setSize(1)
                                .addSort(SortBuilders.fieldSort(TRANS_TIME.getFieldName()).order(SortOrder.DESC)))
                )
                // 不存在在theDays之后交易成功的订单
                .mustNot(JoinQueryBuilders.hasChildQuery(ORDER.getTypeName(),
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                                .must(QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(theDaysAgo)),
                        ScoreMode.None));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withSort(SortBuilders.fieldSort(MERCHANT_NO.getFieldName()).order(SortOrder.ASC))
                .withQuery(boolQueryBuilder)
                .withPageable(page)
                .build();

        EsLog.info("查询长时间没交易的商户", searchQuery);
        Tuple<Map<String, Long>, Long> result = elasticsearchTemplate.query(searchQuery, response -> {
            long totalHits = response.getHits().getTotalHits();
            Map<String, Long> merchantMap = Arrays.stream(response.getHits().getHits())
                    .map(item -> {
                        SearchHits lastTransOrder = item.getInnerHits().get("last_trans_order");
                        String lastTransTime = Arrays.stream(lastTransOrder.getHits())
                                .findFirst()
                                .map(innerHit -> Objects.toString(innerHit.getSourceAsMap().get(TRANS_TIME.getFieldName())))
                                .orElse(Objects.toString(item.getSourceAsMap().get(CREATE_TIME.getFieldName())));
                        LocalDateTime localDateTime = LocalDateTime.parse(lastTransTime, YYYY_MM_DD_HH_MM_SS);
                        String merchantNo = Objects.toString(item.getSourceAsMap().get(MERCHANT_NO.getFieldName()));
                        long between = Duration.between(localDateTime, now).toDays();
                        return new Tuple<>(merchantNo, between);
                    })
                    .collect(Collectors.toMap(Tuple::v1, Tuple::v2));
            return new Tuple<>(merchantMap, totalHits);
        });
        List<MerchantEsResultBean> merchantEsResultBeans = queryMerchantByMerchantNo(result.v1().keySet());
        merchantEsResultBeans.forEach(item -> item.setNoTransDays(Optional.ofNullable(result.v1().get(item.getMerchantNo())).orElse(0L)));
        return new Tuple<>(merchantEsResultBeans, result.v2());
    }

    /**
     * 未认证商户
     *
     * @param queryScope  查询范围
     * @param agentNode   代理商节点
     * @param pageRequest 分页信息
     */
    public static Tuple<List<MerchantEsResultBean>, Long> merchantOfUncertified(QueryScope queryScope, String agentNode, PageRequest pageRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builderQueryScope4Merchant(queryScope, agentNode, boolQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()))
                .must(JoinQueryBuilders.hasChildQuery(MBP.getTypeName(),
                        QueryBuilders.termsQuery(STATUS.getFieldName(), "1", "2", "3"), ScoreMode.None));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withSort(SortBuilders.fieldSort(MERCHANT_NO.getFieldName()).order(SortOrder.ASC))
                .withPageable(pageRequest)
                .withQuery(boolQueryBuilder)
                .build();
        EsLog.info("未认证商户", searchQuery);
        Tuple<List<String>, Long> result = elasticsearchTemplate.query(searchQuery, response -> {
            long totalHits = response.getHits().getTotalHits();
            List<String> merchantList = Arrays.stream(response.getHits().getHits())
                    .map(item -> Objects.toString(item.getSourceAsMap().get(MERCHANT_NO.getFieldName())))
                    .collect(Collectors.toList());
            return new Tuple<>(merchantList, totalHits);
        });
        return new Tuple<>(queryMerchantByMerchantNo(result.v1()), result.v2());
    }

    /**
     * 交易下滑商户
     *
     * @param queryScope 查询范围
     * @param agentNode  代理商节点
     * @param percentage 下滑百分比
     */
    public static Tuple<List<MerchantEsResultBean>, Long> merchantOfTradeSlide(QueryScope queryScope, String agentNode, String percentage) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builderQueryScope4Merchant(queryScope, agentNode, boolQueryBuilder);
        LocalDateTime now = LocalDateTime.now();
        // 上上个月的第一天和最后一天
        String lastLastStart = now.minusMonths(2).with(TemporalAdjusters.firstDayOfMonth()).format(YYYY_MM_01_00_00_00);
        String lastLastEnd = now.minusMonths(2).with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59);
        // 上个月的第一天和最后一天
        String lastStart = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).format(YYYY_MM_01_00_00_00);
        String lastEnd = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59);
        // 查询上上个月必须有交易,且交易成功的订单
        boolQueryBuilder.must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), ORDER.getTypeName()))
                .must(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), TRANS_STTUS_IS_SUCCESS))
                .must(QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(lastLastStart).lte(lastEnd));

        Map<String, String> bucketsPathMap = new HashMap<>();
        String lastLastSumKey = String.format("%s_%s", LAST_LAST_MONTH, SUM_OF_TRANS_AMOUNT);
        String lastLastSumValue = String.format("%s.%s", LAST_LAST_MONTH, SUM_OF_TRANS_AMOUNT);
        String lastSumKey = String.format("%s_%s", LAST_MONTH, SUM_OF_TRANS_AMOUNT);
        String lastSumValue = String.format("%s.%s", LAST_MONTH, SUM_OF_TRANS_AMOUNT);
        bucketsPathMap.put(lastLastSumKey, lastLastSumValue);
        bucketsPathMap.put(lastSumKey, lastSumValue);
        Script script = new Script(String.format("params.%s > 0 && (params.%s - params.%s) / params.%s >= %s / 100",
                lastLastSumKey, lastLastSumKey, lastSumKey, lastLastSumKey, percentage));


        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(GROUP_BY_MERCHANT).field(MERCHANT_NO.getFieldName())
                .size(sysConfigService.getSysConfigValueByKey(SYS_CONFIG_MAX_TRANS_SLIDE, 1000, Integer::valueOf))
                .order(BucketOrder.key(true))
                .subAggregation(AggregationBuilders.filter(LAST_LAST_MONTH,
                        QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(lastLastStart).lte(lastLastEnd))
                        .subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()))
                )
                .subAggregation(AggregationBuilders.filter(LAST_MONTH,
                        QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(lastStart).lte(lastEnd))
                        .subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()))
                )
                .subAggregation(PipelineAggregatorBuilders.bucketSelector(TRADE_SLIDE, bucketsPathMap, script));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(aggregationBuilder)
                .build();
        EsLog.info("交易下滑商户查询", searchQuery);

        Map<String, String> merchantRateMap = elasticsearchTemplate.query(searchQuery, response -> {
            Aggregation aggregation = response.getAggregations().get(GROUP_BY_MERCHANT);
            if (aggregation instanceof UnmappedTerms) {
                return new HashMap<>();
            }
            StringTerms groupByMerchantAgg = (StringTerms) aggregation;
            return groupByMerchantAgg.getBuckets()
                    .stream()
                    .map(item -> {
                        MerchantEsResultBean merchantEsResultBean = new MerchantEsResultBean();
                        merchantEsResultBean.setMerchantNo(item.getKeyAsString());
                        double lastLastSum = ((InternalSum) ((InternalFilter) item.getAggregations().get(LAST_LAST_MONTH)).getAggregations().get(SUM_OF_TRANS_AMOUNT)).getValue();
                        double lastSum = ((InternalSum) ((InternalFilter) item.getAggregations().get(LAST_MONTH)).getAggregations().get(SUM_OF_TRANS_AMOUNT)).getValue();
                        merchantEsResultBean.setRate(rounding((lastSum - lastLastSum) / lastLastSum * 100));
                        return merchantEsResultBean;
                    }).collect(Collectors.toMap(MerchantEsResultBean::getMerchantNo, MerchantEsResultBean::getRate));
        });
        List<MerchantEsResultBean> merchantEsResultBeans = queryMerchantByMerchantNo(merchantRateMap.keySet());
        merchantEsResultBeans.forEach(item -> item.setRate(Optional.ofNullable(merchantRateMap.get(item.getMerchantNo())).orElse("--")));
        return new Tuple<>(merchantEsResultBeans, (long) merchantEsResultBeans.size());
    }

    /**
     * 根据组织id分类汇总商户(总数/激活数)
     *
     * @param merchantSummarySearchBean    查询条件
     * @param isTodayNewly 是否为当日新增商户
     * @return {teamId: MerchantSumBean}
     * key->组织id, value 根据组织id汇总的商户信息
     */
    public static Map<String, MerchantSumBean> merchantSummaryByTeamId(MerchantSummarySearchBean merchantSummarySearchBean, boolean isTodayNewly) {
        String agentNode = merchantSummarySearchBean.getAgentNode();
        if (StringUtils.isBlank(agentNode) || merchantSummarySearchBean.getQueryScope() == null) {
            return new HashMap<>();
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()));
        builderQueryScope4Merchant(merchantSummarySearchBean.getQueryScope(), agentNode, boolQueryBuilder);

        if (isTodayNewly) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName())
                    .gte(LocalDateTime.now().format(YYYY_MM_DD)));
        }

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(GROUP_BY_TEAM_ID).field(TEAM_ID.getFieldName())
                .size(1000)
                // 还是按照原来的组织统计激活数
                .subAggregation(AggregationBuilders.filter(MERCHANT_HAS_ACTIVE,
                        QueryBuilders.termQuery(HLF_ACTIVE.getFieldName(), "1")));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(aggregationBuilder)
                .build();
        EsLog.info("根据组织id分类汇总商户", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
            Aggregation aggregation = asMap.get(GROUP_BY_TEAM_ID);
            if (aggregation instanceof UnmappedTerms) {
                return new HashMap<>();
            }
            StringTerms stringTerms = (StringTerms) aggregation;
            List<MerchantSumBean> collect = stringTerms.getBuckets()
                    .stream()
                    .flatMap(teamItem -> {
                        Map<String, Aggregation> aggregationMap = teamItem.getAggregations().getAsMap();
                        String teamId = Objects.toString(teamItem.getKey());
                        long total = teamItem.getDocCount();
                        long active = ((InternalFilter) aggregationMap.get(MERCHANT_HAS_ACTIVE)).getDocCount();
                        MerchantSumBean merchantSumBean = new MerchantSumBean();
                        merchantSumBean.setTypeId(teamId);
                        merchantSumBean.setTotal(total);
                        merchantSumBean.setActiveNumber(active);
                        merchantSumBean.setNotActiveNumber(total - active);
                        return Stream.of(merchantSumBean);
                    })
                    .collect(Collectors.toList());
            // 容错处理,防止改数据库,导致主组织和子组织值一样,或者不同主组织下子组织一样
            Map<String, MerchantSumBean> result = new HashMap<>();
            collect.forEach(item -> result.put(item.getTypeId(), item));
            return result;
        });
    }

    /**
     * 根据组织id以及业务产品id分类汇总商户(总数/激活数)
     *
     * @param merchantSummarySearchBean  查询条件
     * @param isTodayNewly 是否为当日新增商户
     * @return {teamId: {bpId:MerchantSumBean}}
     * key-> 组织id, value-> 该组织id再根据业务产品分类汇总
     * key -> 业务产品id, value 根据业务产品id汇总的商户信息
     */
    public static Map<String, Map<String, MerchantSumBean>> mbpSummaryByTeamId(MerchantSummarySearchBean merchantSummarySearchBean, boolean isTodayNewly) {
        String agentNode = merchantSummarySearchBean.getAgentNode();
        if (StringUtils.isBlank(agentNode)) {
            return new HashMap<>();
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), MBP.getTypeName()));
        builderQueryScope4Merchant(merchantSummarySearchBean.getQueryScope(), agentNode, boolQueryBuilder);

        if (isTodayNewly) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName())
                    .gte(LocalDateTime.now().format(YYYY_MM_DD)));
        }

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(GROUP_BY_TEAM_ID).field(TEAM_ID.getFieldName())
                .size(1000)
                // 还是原来按主组织的统计
                .subAggregation(AggregationBuilders
                        .terms(GROUP_BY_BP_ID).field(BP_ID.getFieldName())
                        .size(1000)
                        .subAggregation(AggregationBuilders.filter(MERCHANT_HAS_ACTIVE,
                                QueryBuilders.termQuery(HLF_ACTIVE.getFieldName(), "1"))));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(aggregationBuilder)
                .build();
        EsLog.info("根据组织id分类汇总商户进件", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
            Aggregation groupByTeamId = asMap.get(GROUP_BY_TEAM_ID);
            if (groupByTeamId instanceof UnmappedTerms) {
                return new HashMap<>();
            }
            List<Tuple<String, Map<String, MerchantSumBean>>> collect = ((StringTerms) groupByTeamId).getBuckets()
                    .stream()
                    .flatMap(teamItem -> {
                        String teamId = Objects.toString(teamItem.getKey());
                        Map<String, Aggregation> teamAggregationMap = teamItem.getAggregations().getAsMap();
                        Aggregation teamGroupByBpId = teamAggregationMap.get(GROUP_BY_BP_ID);
                        if (teamGroupByBpId instanceof UnmappedTerms) {
                            return Stream.of(new Tuple<String, Map<String, MerchantSumBean>>(teamId, new HashMap<>()));
                        }
                        Map<String, MerchantSumBean> teamCollect = ((StringTerms) teamGroupByBpId).getBuckets()
                                .stream()
                                .map(temp -> {
                                    String bpId = Objects.toString(temp.getKey());
                                    long total = temp.getDocCount();
                                    long active = ((InternalFilter) temp.getAggregations().getAsMap().get(MERCHANT_HAS_ACTIVE)).getDocCount();
                                    MerchantSumBean merchantSumBean = new MerchantSumBean();
                                    merchantSumBean.setTypeId(bpId);
                                    merchantSumBean.setNotActiveNumber(total - active);
                                    merchantSumBean.setTotal(total);
                                    merchantSumBean.setActiveNumber(active);
                                    return merchantSumBean;
                                }).collect(Collectors.toMap(MerchantSumBean::getTypeId, Function.identity()));
                        return Stream.of(new Tuple<>(teamId, teamCollect));
                    })
                    .collect(Collectors.toList());
            // 容错处理,防止改数据库,导致主组织和子组织值一样,或者不同主组织下子组织一样
            Map<String, Map<String, MerchantSumBean>> result = new HashMap<>();
            collect.forEach(item -> result.put(item.v1(), item.v2()));
            return result;
        });
    }

    /**
     * 判断es聚合响应是否为空
     */
    private static boolean isEmptyBucket(Aggregation aggregation) {
        if (aggregation == null) {
            return true;
        }
        if (aggregation instanceof UnmappedTerms) {
            return true;
        }
        // 过滤掉 "" 之后桶个数为0的,也认为是空桶
        if (aggregation instanceof StringTerms) {
            return ((StringTerms) aggregation).getBuckets()
                    .stream()
                    .filter(item -> StringUtils.isNotBlank(Objects.toString(item.getKey(), "")))
                    .count() == 0;
        }
        return false;
    }

    /**
     * 根据代理商父级代理商,对直接下级代理商的商户进行分类汇总
     *
     * @param parentId     父级代理商
     * @param pageRequest  分页信息
     * @param isTodayNewly 是否是今日新增商户
     */
    public static Tuple<List<MerchantSumBean>, Long> merchantSummaryByDirectAgent(MerchantSummarySearchBean merchantSummarySearchBean, PageRequest pageRequest, boolean isTodayNewly) {
        String agentNo = merchantSummarySearchBean.getAgentNo();
        String agentNode = merchantSummarySearchBean.getAgentNode();
        if (StringUtils.isBlank(agentNo, agentNode)) {
            return new Tuple<>(new ArrayList<>(), 0L);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                // 查询代理商信息
                .must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), AGENT.getTypeName()));

        builderQueryScope4Agent(merchantSummarySearchBean.getQueryScope(), agentNode, agentNo, boolQueryBuilder);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build();
        EsLog.info("直接下级代理商的商户汇总", searchQuery);
        log.info("是否统计今天 {}", isTodayNewly);

        return elasticsearchTemplate.query(searchQuery, response -> {
            List<MerchantSumBean> collect = Arrays.stream(response.getHits().getHits())
                    .map(item -> {
                        Map<String, Object> sourceAsMap = item.getSourceAsMap();
                        String agentNodeField = Objects.toString(sourceAsMap.get(AGENT_NODE.getFieldName()));
                        String agentName = Objects.toString(sourceAsMap.get(AGENT_NAME.getFieldName()));
                        String agentNoField = Objects.toString(sourceAsMap.get(AGENT_NO.getFieldName()));
                        MerchantSumBean merchantSumBean = statisMerchantByAgentNode(agentNodeField, isTodayNewly);
                        merchantSumBean.setTypeName(agentName);
                        merchantSumBean.setTypeId(agentNoField);
                        merchantSumBean.setAgentNode(agentNodeField);
                        return merchantSumBean;
                    }).collect(Collectors.toList());
            long totalHits = response.getHits().getTotalHits();
            return new Tuple<>(collect, totalHits);
        });
    }


    /**
     * 根据代理商节点,汇总商户信息
     *
     * @param agentNode    代理商节点
     * @param isTodayNewly 是否为今日新增商户
     */
    private static MerchantSumBean statisMerchantByAgentNode(String agentNode, boolean isTodayNewly) {
        if (StringUtils.isBlank(agentNode)) {
            return new MerchantSumBean();
        }
        // 1. 构造查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                // 必须是该代理商直属或者下级的商户
                .must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                // 查询类型为欢乐返激活和商户进件
                .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()));
        if (isTodayNewly) {
            // 创建时间为为大于今天日期
            boolQueryBuilder.must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName())
                    .gte(LocalDateTime.now().format(MerchantSearchUtils.YYYY_MM_DD)));
        }
        FilterAggregationBuilder filter = AggregationBuilders.filter(MERCHANT_HAS_ACTIVE, QueryBuilders.termQuery(HLF_ACTIVE.getFieldName(), "1"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(NPOSP_ES_INDEX)
                .withQuery(boolQueryBuilder)
                .addAggregation(filter)
                .build();
        EsLog.info("statisMerchantByAgentNode", searchQuery);
        log.info("是否统计今天 {}", isTodayNewly);

        return getMerchantSumBean(searchQuery);
    }

    @Resource
    public void setSysConfigService(SysConfigService sysConfigService) {
        MerchantSearchUtils.sysConfigService = sysConfigService;
    }

    @Resource
    public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
        MerchantSearchUtils.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Resource
    public void setMerchantEsService(MerchantEsService merchantEsService) {
        MerchantSearchUtils.merchantEsService = merchantEsService;
    }
}
