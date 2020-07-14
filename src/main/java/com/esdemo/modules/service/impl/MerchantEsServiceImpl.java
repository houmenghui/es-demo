package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.FunctionManageConfigEnums;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.fmc.FmConfig058;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.*;
import com.esdemo.frame.utils.es.EsLog;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.dao.*;
import com.esdemo.modules.service.ActivityDataService;
import com.esdemo.modules.service.MerchantEsService;
import com.esdemo.modules.service.SysConfigService;
import com.esdemo.modules.utils.EsSearchUtils;
import com.esdemo.modules.utils.MerchantSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.esdemo.frame.enums.EsNpospField.*;
import static com.esdemo.frame.enums.EsNpospJoinType.MERCHANT;
import static com.esdemo.frame.utils.Constants.*;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 16:00
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Slf4j
@Service
public class MerchantEsServiceImpl implements MerchantEsService {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private MerchantDao merchantDao;
    @Resource
    private AcqMerchantDao acqMerchantDao;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private AgentInfoDao agentInfoDao;
    @Resource
    private SysConfigDao sysConfigDao;

    @Resource
    private ActivityDataService activityDataService;

    @CacheData
    @Override
    public List<MerchantSumBean> merchantSummaryByTeamId(MerchantSummarySearchBean merchantSummarySearchBean, boolean isTodayNewly) {
        String agentNo = merchantSummarySearchBean.getAgentNo();
        String agentNode = merchantSummarySearchBean.getAgentNode();
        if (StringUtils.isBlank(agentNo, agentNode)) {
            return new ArrayList<>();
        }
        // 1. 先查询该代理商代理的组织产品 {teamId : teamName}
        Map<String, MerchantSumBean> teamAndTeamEntryMap = listTeamNameByAgentNo(agentNo);
        // 2. 如果为空,直接返回默认值
        if (MapUtils.isEmpty(teamAndTeamEntryMap)) {
            return new ArrayList<>();
        }
        // 3. 根据代理商查出业务产品信息 {teamId : {bpId : bpName}}
        Map<String, Map<String, String>> temmBpMap = listTeamBpByAgentNo(agentNo);
        // 4. 根据代理查询组织汇总记录 {teamId: MerchantSumBean}
        Map<String, MerchantSumBean> merchantSummaryByTeamId = MerchantSearchUtils.merchantSummaryByTeamId(merchantSummarySearchBean, isTodayNewly);
        // 4. 根据代理查询组织,业务产品汇总记录 {teamId: {bpId:MerchantSumBean}}
        Map<String, Map<String, MerchantSumBean>> mbpSummaryByTeamId = MerchantSearchUtils.mbpSummaryByTeamId(merchantSummarySearchBean, isTodayNewly);

        // 5. 封装返回值
        List<MerchantSumBean> teamMerchantList = new ArrayList<>();
        // 6. 先for循环组织
        for (Map.Entry<String, MerchantSumBean> teamEntry : teamAndTeamEntryMap.entrySet()) {
            MerchantSumBean teamMerchant = merchantSummaryByTeamId.getOrDefault(teamEntry.getKey(), new MerchantSumBean());
            teamMerchant.setTypeId(teamEntry.getKey());
            teamMerchant.setTypeName(teamEntry.getValue().getTypeName());
            Map<String, String> bpMap = Optional.ofNullable(temmBpMap.get(teamEntry.getKey())).orElse(new HashMap<>());
            List<MerchantSumBean> bpMerchantList = new ArrayList<>();
            // 再for循环组织下的业务产品
            for (Map.Entry<String, String> bpEntry : bpMap.entrySet()) {
                MerchantSumBean bpMerchant = mbpSummaryByTeamId
                        .getOrDefault(teamEntry.getKey(), new HashMap<>())
                        .getOrDefault(bpEntry.getKey(), new MerchantSumBean());
                bpMerchant.setTypeId(bpEntry.getKey());
                bpMerchant.setTypeName(bpEntry.getValue());
                bpMerchantList.add(bpMerchant);
            }
            teamMerchant.setChildren(bpMerchantList);
            teamMerchantList.add(teamMerchant);
        }
        return teamMerchantList;
    }

    @Override
    public long countMerchant(MerchantSummarySearchBean merchantSummarySearchBean, boolean isTodayNewly) {
        String agentNode = merchantSummarySearchBean.getAgentNode();
        if (StringUtils.isBlank(agentNode)) {
            return 0;
        }
        BoolQueryBuilder builder = QueryBuilders.boolQuery()
                // 必须是该代理商或者下级的代理商
//                .must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", agentNode)))
                // 查询类型为商户
                .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()));
        MerchantSearchUtils.builderQueryScope4Merchant(merchantSummarySearchBean.getQueryScope(), agentNode, builder);

        if (isTodayNewly) {
            // 创建时间为为大于今天日期
            builder.must(QueryBuilders.rangeQuery(CREATE_TIME.getFieldName())
                    .gte(LocalDateTime.now().format(MerchantSearchUtils.YYYY_MM_DD)));
        }
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(Constants.NPOSP_ES_INDEX)
                .withQuery(builder)
                .build();
        EsLog.info("统计商户数据", searchQuery);
        log.info("统计商户数据,是否统计当天 {}", isTodayNewly);

        return elasticsearchTemplate.count(searchQuery);
    }

    /**
     * 统计商户数量
     *
     * @param searchBean
     * @return
     */
    @Override
    @CacheData
    public long summaryMerCountByTerms(EsSearchBean searchBean) {
        SearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean).build();
        EsLog.info("统计商户数量", searchQuery);
        return elasticsearchTemplate.count(searchQuery);
    }

    @DataSourceSwitch
    @Override
    public Map<String, String> listBpNameByBpIds(List<String> bpIds) {
        if (CollectionUtils.isEmpty(bpIds)) {
            return new HashMap<>();
        }
        List<Map<String, Object>> result = merchantDao.listBpNameByBpIds(bpIds);
        return Optional.ofNullable(result)
                .orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(item -> Objects.toString(item.get("bp_id")),
                        item -> Objects.toString(item.get("bp_name"))));
    }

    /**
     * 根据代理商编号获取业务产品名称
     *
     * @param agentNo 代理商编号
     * @return {teamId : {bpId : bpName}}
     * key->组织id, value->业务产品信息(key->业务产品id, value->业务产品名称)
     */
    private Map<String, Map<String, String>> listTeamBpByAgentNo(String agentNo) {
        List<Map<String, Object>> result = merchantDao.listBusinessProductByAgentNo(agentNo);
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        Optional.ofNullable(result)
                .orElse(new ArrayList<>())
                .forEach(item -> {
                    String teamId = Objects.toString(item.get("team_id"));
                    String bpId = Objects.toString(item.get("bpId"));
                    String bpName = Objects.toString(item.get("bpName"));
                    Map<String, String> teamMap = resultMap.getOrDefault(teamId, new HashMap<>());
                    teamMap.put(bpId, bpName);
                    resultMap.put(teamId, teamMap);
                });
        return resultMap;
    }

    /**
     * 根据代理商编号获取组织名称
     *
     * @param agentNo 代理商编号
     * @return {teamId : teamName}
     * key->组织id, value->{v1: 组织名称, v2: 是否为子组织}
     */
    private Map<String, MerchantSumBean> listTeamNameByAgentNo(String agentNo) {
        Map<String, MerchantSumBean> resultMap = new HashMap<>();
        List<Map<String, Object>> result = merchantDao.listTeamNameByAgentNo(agentNo);
        Optional.ofNullable(result)
                .orElse(new ArrayList<>())
                .forEach(item -> {
                    String teamId = Objects.toString(item.get("team_id"));
                    String teamName = Objects.toString(item.get("team_name"));
                    MerchantSumBean merchantSumBean = new MerchantSumBean();
                    merchantSumBean.setTypeId(teamId);
                    merchantSumBean.setTypeName(teamName);
                    resultMap.put(teamId, merchantSumBean);
                });
        return resultMap;
    }

    @CacheData
    @Override
    public Tuple<List<MerchantSumBean>, Long> statisMerchantByDirectAgent(MerchantSummarySearchBean merchantSummarySearchBean, PageRequest pageRequest, boolean isTodayNewly) {
        return MerchantSearchUtils.merchantSummaryByDirectAgent(merchantSummarySearchBean, pageRequest, isTodayNewly);
    }

    @Override
    public List<Map<String, Object>> listBusinessProductByAgentNo(MerchantSearchBean searchBean, String agentNo) {
        return merchantDao.listBusinessProductByAgentNo(StringUtils.isBlank(searchBean.getAgentNo()) ? agentNo : searchBean.getAgentNo());
    }

    @CacheData
    @Override
    public Tuple<List<MerchantEsResultBean>, Long> listMerchantInfo(MerchantSearchBean searchParams, PageRequest pageRequest, String agentNode) {
        return MerchantSearchUtils.listMerchantInfo(searchParams, pageRequest, agentNode);
    }

    @Override
    public Map<String, List<KeyValueBean>> queryMerchantParams() {
        List<KeyValueBean> merchantTypes = new ArrayList<>();
        merchantTypes.add(new KeyValueBean(QueryScope.ALL.name(), "全部数据", "包含直营及下级所有的数据"));
        merchantTypes.add(new KeyValueBean(QueryScope.OFFICAL.name(), "直营数据", "仅包含直营推广的数据"));
        merchantTypes.add(new KeyValueBean(QueryScope.CHILDREN.name(), "下级数据", "仅包含所有下级的数据"));

        List<KeyValueBean> activityMerchantTypes = new ArrayList<>();
        activityMerchantTypes.add(new KeyValueBean(QueryScope.ALL.name(), "全部商户", "包含直营及下级所有的商户"));
        activityMerchantTypes.add(new KeyValueBean(QueryScope.OFFICAL.name(), "直营商户", "仅包含直营推广的商户"));
        activityMerchantTypes.add(new KeyValueBean(QueryScope.CHILDREN.name(), "下级商户", "仅包含所有下级的商户"));

        String qualityMoney = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_QUALITY_SEARCH_CUR_MONTH_TRANS_MONEY, "50000", Function.identity());

        String activeTransDay = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_ACTIVE_SEARCH_TRANS_DAY, "30", Function.identity());
        String activeOrderNum = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_ACTIVE_SEARCH_TRANS_ORDER_NUM, "2", Function.identity());
        String activeTransMoney = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_ACTIVE_SEARCH_TRANS_MONEY, "10", Function.identity());

        String sleepCreate = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_SLEEP_SEARCH_MERCHANT_CREATE, "60", Function.identity());
        String sleepTrans = sysConfigService.getSysConfigValueByKey(SYS_CONFIG_SLEEP_SEARCH_TRANS_TIME, "60", Function.identity());

        List<KeyValueBean> searchTypes = new ArrayList<>();
        searchTypes.add(new KeyValueBean(MerchantSearchBean.SearchType.ALL.name(), "全部", "包含以下所有类别"));
        searchTypes.add(new KeyValueBean(MerchantSearchBean.SearchType.QUALITY.name(), "优质商户", String.format("本月交易金额>=%s元", qualityMoney)));
        searchTypes.add(new KeyValueBean(MerchantSearchBean.SearchType.ACTIVE.name(), "活跃商户", String.format("近%s天交易笔数>=%s笔且交易金额>=%s元", activeTransDay, activeOrderNum, activeTransMoney)));
        searchTypes.add(new KeyValueBean(MerchantSearchBean.SearchType.UNCERTIFIED.name(), "需认证商户", "身份未认证的商户"));
        searchTypes.add(new KeyValueBean(MerchantSearchBean.SearchType.SLEEP.name(), "休眠商户", String.format("入网>=%s天且连续>%s天无交易", sleepCreate, sleepTrans)));

        List<KeyValueBean> sortTypes = new ArrayList<>();
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.DEFAULT_ORDER.name(), "默认排序"));
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.CUR_MONTH_TRANS_DESC.name(), "本月交易量从高到低"));
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.CUR_MONTH_TRANS_ASC.name(), "本月交易量从低到高"));
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.LAST_MONTH_TRANS_DESC.name(), "上月交易量从高到低"));
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.LAST_MONTH_TRANS_ASC.name(), "上月交易量从低到高"));
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.ALL_TRANS_DESC.name(), "累计交易量从高到低"));
        sortTypes.add(new KeyValueBean(MerchantSearchBean.SortType.ALL_TRANS_ASC.name(), "累计交易量从低到高"));

        List<KeyValueBean> mbpStatus = new ArrayList<>();
        mbpStatus.add(new KeyValueBean("", "全部"));
        mbpStatus.add(new KeyValueBean("1", "待一审"));
        mbpStatus.add(new KeyValueBean("2", "待平台审核"));
        mbpStatus.add(new KeyValueBean("3", "审核失败"));
        mbpStatus.add(new KeyValueBean("4", "正常"));
        mbpStatus.add(new KeyValueBean("5", "已转自动审件"));
        mbpStatus.add(new KeyValueBean("0", "关闭"));

        List<KeyValueBean> riskStatusList = new ArrayList<>();
        riskStatusList.add(new KeyValueBean("", "全部"));
        riskStatusList.add(new KeyValueBean("1", "正常"));
        riskStatusList.add(new KeyValueBean("2", "只进不出"));
        riskStatusList.add(new KeyValueBean("3", "不进不出"));

        List<KeyValueBean> recommendedSourceList = new ArrayList<>();
        recommendedSourceList.add(new KeyValueBean("", "全部"));
        recommendedSourceList.add(new KeyValueBean("0", "正常注册"));
        recommendedSourceList.add(new KeyValueBean("1", "超级推"));
        recommendedSourceList.add(new KeyValueBean("2", "代理商分享"));

        List<KeyValueBean> hlfActiveList = new ArrayList<>();
        hlfActiveList.add(new KeyValueBean("", "全部"));
        hlfActiveList.add(new KeyValueBean("0", "未激活"));
        hlfActiveList.add(new KeyValueBean("1", "已激活"));

//        recommendedSourceList.addAll(Optional.ofNullable(sysDictDao.getDictValues("RECOMMENDED_SOURCES"))
//                .orElse(new ArrayList<>())
//                .stream()
//                .map(item -> new KeyValueBean(Objects.toString(item.get("sys_value")), item.get("sys_name")))
//                .collect(Collectors.toList()));

        List<KeyValueBean> merchantSearch = new ArrayList<>();
        MerchantSearchBean temp = new MerchantSearchBean();
        temp.setSearchType(MerchantSearchBean.SearchType.ALL);
        merchantSearch.add(new KeyValueBean(MerchantSearchBean.SearchType.ALL.name(), MerchantSearchUtils.parseSearchParamBySearchType(temp), "包含以下所有类别"));
        temp.setSearchType(MerchantSearchBean.SearchType.QUALITY);
        merchantSearch.add(new KeyValueBean(MerchantSearchBean.SearchType.QUALITY.name(), MerchantSearchUtils.parseSearchParamBySearchType(temp), String.format("本月交易金额>=%s元", qualityMoney)));
        temp.setSearchType(MerchantSearchBean.SearchType.ACTIVE);
        merchantSearch.add(new KeyValueBean(MerchantSearchBean.SearchType.ACTIVE.name(), MerchantSearchUtils.parseSearchParamBySearchType(temp), String.format("近%s天交易笔数>=%s笔且交易金额>=%s元", activeTransDay, activeOrderNum, activeTransMoney)));
        temp.setSearchType(MerchantSearchBean.SearchType.UNCERTIFIED);
        merchantSearch.add(new KeyValueBean(MerchantSearchBean.SearchType.UNCERTIFIED.name(), MerchantSearchUtils.parseSearchParamBySearchType(temp), "身份未认证的商户"));
        temp.setSearchType(MerchantSearchBean.SearchType.SLEEP);
        merchantSearch.add(new KeyValueBean(MerchantSearchBean.SearchType.SLEEP.name(), MerchantSearchUtils.parseSearchParamBySearchType(temp), String.format("入网>=%s天且连续>%s天无交易", sleepCreate, sleepTrans)));

        Map<String, List<KeyValueBean>> result = new HashMap<>();
        result.put("merchantTypes", merchantTypes);
        result.put("activityMerchantTypes", activityMerchantTypes);
        result.put("searchTypes", searchTypes);
        result.put("sortTypes", sortTypes);
        result.put("mbpStatus", mbpStatus);
        result.put("merchantSearchEcho", merchantSearch);
        result.put("riskStatus", riskStatusList);
        result.put("recommendedSource", recommendedSourceList);
        result.put("hlfActive", hlfActiveList);
        return result;
    }

    @Override
    public MerchantDetailBean getMerchantDetails(String merchantNo, String agentNode) {
        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
        // 如果查不到商户,后续操作就没有必要了
        if (merchantInfo == null) {
            throw new AppException("该商户不存在");
        }
        Map<String, String> merchantStatus = new HashMap<>();
        merchantStatus.put("0", "商户关闭");
        merchantStatus.put("1", "正常");
        merchantStatus.put("2", "冻结");
        merchantInfo.setStatusZh(merchantStatus.get(merchantInfo.getStatus()));
        // 查询的商户不是直营商户,不现实商户名
        if (!StringUtils.equalsIgnoreCase(merchantInfo.getParentNode(), agentNode)) {
            merchantInfo.setMerchantName("");
            // 手机号打码
//            merchantInfo.setMobilephone(StringUtils.mask4MobilePhone(merchantInfo.getMobilephone()));
        }
        List<KeyValueBean> hardwares = merchantDao.listHardwareByMerchantNo(merchantNo, agentNode);

        Tuple<List<MerchantBpBean>, List<MerchantBpBean>> bpResult = MerchantSearchUtils.getMerchantDetail4Bp(merchantNo, agentNode);
        List<String> allBpId = Stream.concat(bpResult.v1().stream(), bpResult.v2().stream())
                .map(MerchantBpBean::getBpId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(allBpId)) {
            Map<String, String> map = listBpNameByBpIds(allBpId);
            List<MerchantBpBean> v1 = bpResult.v1().stream().filter(item -> map.get(item.getBpId()) != null)
                    .peek(item -> item.setBpName(map.get(item.getBpId()))).collect(Collectors.toList());
            List<MerchantBpBean> v2 = bpResult.v2().stream().filter(item -> map.get(item.getBpId()) != null)
                    .peek(item -> item.setBpName(map.get(item.getBpId()))).collect(Collectors.toList());
            bpResult = new Tuple<>(v1, v2);
        }
        MerchantDetailBean merchantDetailBean = MerchantSearchUtils.getMerchantDetails(merchantNo, agentNode);
        merchantDetailBean.setDirectMerchant(StringUtils.equalsIgnoreCase(merchantInfo.getParentNode(), agentNode));
        List<MerchantBpBean> allBpList = new ArrayList<>();
        allBpList.addAll(bpResult.v1());
        allBpList.addAll(bpResult.v2());
        merchantDetailBean.setBpDatas(allBpList);
        merchantDetailBean.setHardwares(hardwares);
        merchantDetailBean.setMerchantInfo(merchantInfo);
        merchantDetailBean.setOpenAgentUpdateBpSwitch(isOpenAgentUpdateBpSwitch(merchantNo));

        ActivityAndDataQueryBean activityAndDataQueryBean = new ActivityAndDataQueryBean();

        activityAndDataQueryBean.setMerchantNo(merchantNo);

        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setAgentNo(merchantInfo.getAgentNo());
        userInfoBean.setAgentNode(merchantInfo.getParentNode());
        activityAndDataQueryBean.setUserInfoBean(userInfoBean);

        merchantDetailBean.setActivityDataDetails(activityDataService.merActivityDataDetail(activityAndDataQueryBean));
//     快钱没有一户一码特约商户
//        /******************一户一码特约商户需求新增逻辑*******************/
//        //是否显示申请特约商户按钮
//        merchantDetailBean.setShowApplyAcqMerButton(true);
//        //显示收单商户状态
//        merchantDetailBean.setAcqMerStatus(null);
//        merchantDetailBean.setAcq_into_no(null);
//        //查询收单商户进件记录
//        Map<String, Object> acqMerInfoMap = acqMerchantDao.queryLatestAcqMerchantInfo(merchantNo);
//        if (acqMerInfoMap != null && !acqMerInfoMap.isEmpty()) {
//            merchantDetailBean.setShowApplyAcqMerButton(false);
//            //`audit_status` int(11) NOT NULL COMMENT '审核状态 1.正常(审核中) 2.审核通过 3 审核不通过',
//            String auditStatus = StringUtils.filterNull(acqMerInfoMap.get("audit_status"));
//            String acq_into_no = StringUtils.filterNull(acqMerInfoMap.get("acq_into_no"));
//            if ("1".equals(auditStatus)) {
//                merchantDetailBean.setAcqMerStatus(AcqMerStatus.ACQ_IN_AUDIT.getStatus());
//            }
//            if ("2".equals(auditStatus)) {
//                merchantDetailBean.setAcqMerStatus(AcqMerStatus.ACQ_MER_SUCCESS.getStatus());
//                //审核通过查询收单商户记录
//                Map<String, Object> acqMerMap = acqMerchantDao.queryAcqMerByGeneralMerNo(merchantNo, null);
//                if (acqMerMap != null && !acqMerMap.isEmpty()) {
//                    //`acq_status` int(1) DEFAULT '1' COMMENT '状态  0关闭  1开通',
//                    String acqStatus = StringUtils.filterNull(acqMerMap.get("acq_status"));
//                    merchantDetailBean.setAcqMerStatus(AcqMerStatus.ACQ_MER_SUCCESS.getStatus());
//                    if ("0".equals(acqStatus)) {
//                        merchantDetailBean.setAcqMerStatus(AcqMerStatus.ACQ_INVALID.getStatus());
//                        //失效可以重新再申请
//                        merchantDetailBean.setShowApplyAcqMerButton(true);
//                    }
//                }
//            }
//            if ("3".equals(auditStatus)) {
//                merchantDetailBean.setAcqMerStatus(AcqMerStatus.ACQ_AUDIT_FAIL.getStatus());
//            }
//            merchantDetailBean.setAcq_into_no(acq_into_no);
//        }
//        if (merchantDetailBean.isShowApplyAcqMerButton()) {
//            //商户状态正常，并且所有业务产品状态都正常才显示“申请特约商户”按钮
//            if (!"1".equals(merchantInfo.getStatus())) {
//                merchantDetailBean.setShowApplyAcqMerButton(false);
//            }
//            if (merchantDetailBean.isShowApplyAcqMerButton()) {
//                String mbpStatus = "";
//                //获取所有业务产品
//                List<Map<String, Object>> mbpList = acqMerchantDao.getMbpList(merchantNo);
//                if (CollectionUtils.isNotEmpty(mbpList)) {
//                    for (Map<String, Object> mbpMap : mbpList) {
//                        mbpStatus = StringUtils.filterNull(mbpMap.get("status"));
//                        if (!"4".equals(mbpStatus)) {
//                            merchantDetailBean.setShowApplyAcqMerButton(false);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        /******************一户一码特约商户需求新增逻辑*******************/

        return merchantDetailBean;
    }

    @Override
    public List<MerchantBpBean> listCanReplaceBpInfo(String merchantNo, String agentNo) {
        List<MerchantBpBean> merchantBpBeanList = merchantDao.listCanReplaceBp(merchantNo);
        if (CollectionUtils.isEmpty(merchantBpBeanList)) {
            return merchantBpBeanList;
        }
        merchantBpBeanList
                .forEach(item -> {
                    List<MerchantBpBean> otherBpInTheSameGroup = merchantDao.listOtherBpInTheSameGroup(item.getBpId(), agentNo);
                    item.setCanReplaceBpList(otherBpInTheSameGroup);
                });
        return merchantBpBeanList;
    }

    BigDecimal selectBpRateByServiceType(String agentNo, String bpId, String serviceType) {
        BigDecimal bigDecimal = merchantDao.selectBpRateByServiceType(agentNo, bpId, serviceType);
        return Optional.ofNullable(bigDecimal).orElse(BigDecimal.ZERO);
    }

    public Map<String, BigDecimal> selectBpsRateByServiceType(String agentNo, List<String> bpList, String serviceType) {
        List<Map<String, Object>> rates = merchantDao.selectBpsRateByServiceType(agentNo, bpList, serviceType);
        Map<String, BigDecimal> result = new HashMap<>();
        if (org.springframework.util.CollectionUtils.isEmpty(rates)) {
            return result;
        }
        for (Map<String, Object> rate : rates) {
            String bpId = Objects.toString(rate.get("bp_id"));
            BigDecimal bigDecimal = new BigDecimal(Objects.toString(rate.get("rate"), "0"));
            result.put(bpId, bigDecimal);
        }
        return result;
    }

    boolean onlyRateReduction(String merchantNo) {
        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
        if (merchantInfo == null) {
            return false;
        }
        AgentInfo agentInfo = agentInfoDao.selectByAgentNo(merchantInfo.getAgentNo());
        String code = FunctionManageConfigEnums.FUNCTION_MANAGE_058.getCode();
        Map<String, Object> functionMap = merchantDao.findFunctionManage(code);
        // 代理商自定义费率管控 开关未打开,可以自由调
        if (functionMap == null || !"1".equals(Objects.toString(functionMap.get("function_switch")))) {
            return false;
        }
        // 代理商控制开关打开
        if ("1".equals(Objects.toString(functionMap.get("agent_control")))) {//代理商为开启
            // 代理商是否被包含(白名单)
            Map<String, Object> autoManage = merchantDao.findActivityIsSwitch(agentInfo.getOneLevelId(), code, agentInfo.getTeamId());
            // 包含则自由调
            if (autoManage != null) {
                return false;
            }
        }
        // 获取组织配置
        String functionManage058 = sysConfigDao.getStringValueByKey(FunctionManageConfigEnums.FUNCTION_MANAGE_058.name());
        FmConfig058 fmConfig058 = (FmConfig058) GsonUtils.fromJson2Bean(functionManage058, FunctionManageConfigEnums.FUNCTION_MANAGE_058.getJsonClass());
        // 组织配置不为空或者配置中的组织包该商户的组织,只能向下调
        if (fmConfig058 != null && fmConfig058.contain(merchantInfo.getTeamId(), merchantInfo.getTeamEntryId())) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public void replaceBusinessProduct(String merchantNo, String oldBpId, String newBpId, String operationAgentNo) {
        if (StringUtils.isBlank(merchantNo) || StringUtils.isBlank(oldBpId) || StringUtils.isBlank(newBpId)) {
            throw new AppException("必传参数为空");
        }
        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);

        if (merchantDao.countMerchantBpInfo(merchantNo, oldBpId) == 0) {
            throw new AppException("原业务产品不存在或没有审核成功,不能更换");
        }
//        快钱没有费率管控
//        if (this.onlyRateReduction(merchantNo)) {
//            BigDecimal oldBpRate = this.selectBpRateByServiceType(merchantInfo.getOneAgentNo(), oldBpId, "4");
//            BigDecimal newBpRate = this.selectBpRateByServiceType(merchantInfo.getOneAgentNo(), newBpId, "4");
//            if (newBpRate.compareTo(oldBpRate) > 0) {
//                throw new AppException("因代理商费率管控,不能更换");
//            }
//        }
        if (merchantDao.countTerminalBpInfo(merchantNo, oldBpId) == 0) {
            throw new AppException("该业务产品的机具已经解绑,不能更换");
        }
        if (merchantDao.countMerchantBpInfo(merchantNo, newBpId) > 0) {
            throw new AppException("您已存在该业务产品,不能更换");
        }
        if (merchantDao.updateMerchantTerminal(merchantNo, oldBpId, newBpId) == 0) {
            throw new AppException("更新机具信息失败");
        }
        if (merchantDao.updateMerchantBusinessProduct(merchantNo, oldBpId, newBpId) == 0) {
            throw new AppException("更新业务产品失败");
        }
        MerchantBusinessProductHistory mbpHis = new MerchantBusinessProductHistory();
        mbpHis.setSourceBpId(oldBpId);
        mbpHis.setNewBpId(newBpId);
        mbpHis.setOperationType("2");// 2更换
        mbpHis.setOperationPersonType("3");// 3代理商
        mbpHis.setCreateTime(new Date());
        mbpHis.setOperationPersonNo(operationAgentNo);// 操作代理商的编号
        mbpHis.setMerchantNo(merchantNo);
        if (merchantDao.insertMerBusProHis(mbpHis) != 1) {
            throw new AppException("写入商户业务产品历史表失败");
        }
        // 删除审核失败进件项
        merchantDao.delectMerBusItem(merchantNo);
        updateMerchantService(merchantInfo, oldBpId, newBpId);
        // 如果是再中付同步过,且同步成功的话,这需要条用中付的修改接口(修改费率)
        if (merchantDao.countZF_ZQAndSyncSuccess(merchantNo, newBpId) >= 1) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    ClientInterface.syncZfMerchantUpdate(merchantNo, newBpId, operationAgentNo);
                } catch (Exception e) {
                    log.error("调用上游同步费率接口失败", e);
                }
            }).start();
        }
    }

    private void updateMerchantService(MerchantInfo merchantInfo, String oldBpId, String newBpId) {
        List<ServiceInfoBean> oldServiceInfoList = merchantDao.listServiceInfoByBpId(oldBpId);
        log.info("oldServiceInfoList -> " + oldServiceInfoList);
        List<ServiceInfoBean> newServiceInfoList = merchantDao.listServiceInfoByBpId(newBpId);
        log.info("newServiceInfoList -> " + newServiceInfoList);
        if (oldServiceInfoList == null || newServiceInfoList == null) {
            throw new AppException("没有找到相应的服务信息");
        }
        if (oldServiceInfoList.size() != newServiceInfoList.size()) {
            throw new AppException("新旧业务产品的服务信息不一一对应.");
        }
        Map<String, ServiceInfoBean> oldServiceInfoMap = oldServiceInfoList.stream()
                .collect(Collectors.toMap(ServiceInfoBean::getServiceId, Function.identity()));
        Map<String, ServiceInfoBean> newServiceInfoMap = newServiceInfoList.stream()
                .collect(Collectors.toMap(ServiceInfoBean::getServiceType, Function.identity()));
        log.info("oldServiceInfoMap -> " + oldServiceInfoMap);
        log.info("newServiceInfoMap -> " + newServiceInfoMap);

        String oneAgentNo = merchantInfo.getOneAgentNo();
        String merchantNo = merchantInfo.getMerchantNo();

        // 删除商户服务费率
        merchantDao.deleteMerRate(oldBpId, merchantNo);
        // 删除商户服务限额
        merchantDao.deleteMerQuota(oldBpId, merchantNo);

        for (Map.Entry<String, ServiceInfoBean> oldServiceEntry : oldServiceInfoMap.entrySet()) {
            String oldServiceId = oldServiceEntry.getKey();
            String oldServiceType = oldServiceEntry.getValue().getServiceType();
            ServiceInfoBean newServiceInfoBean = newServiceInfoMap.get(oldServiceType);
            if (newServiceInfoBean == null || StringUtils.isBlank(newServiceInfoBean.getServiceId())) {
                throw new AppException("旧业务产品的的服务(" + oldServiceId + ")没找到对应的新服务");
            }
            String newServiceId = newServiceInfoBean.getServiceId();
            if (merchantDao.updateMerchantService(merchantNo, oldBpId, newBpId, oldServiceId,
                    newServiceId) != 1) {
                throw new AppException("更新商户服务失败");
            }
            if (StringUtils.equals("0", newServiceInfoBean.getFixedRate())) {
                List<ServiceRate> newServiceRateList = merchantDao.getServiceRateByServiceId(oneAgentNo,
                        newServiceId);
                if (newServiceRateList != null && !newServiceRateList.isEmpty()) {
                    merchantDao.bacthInsertServiceRate(newServiceRateList, merchantNo);
                }
            }
            if (StringUtils.equals("0", newServiceInfoBean.getFixedQuota())) {
                List<ServiceQuota> newServiceQuotaList = merchantDao.getServiceQuotaByServiceId(oneAgentNo,
                        newServiceId);
                if (newServiceQuotaList != null && !newServiceQuotaList.isEmpty()) {
                    merchantDao.bacthInsertServiceQuota(newServiceQuotaList, merchantNo);
                }
            }
        }
    }

    @CacheData
    @Override
    public List<MerchantWarningBean> queryMerchantEarlyWarning(QueryScope queryScope, String agentNode, String loginAgentNo) {
        return Optional.ofNullable(merchantDao.listMerchantWarning(loginAgentNo))
                .orElse(new ArrayList<>())
                .stream()
                .filter(item -> StringUtils.isNotBlank(item.getWarningType()))
                .filter(item -> Arrays.asList("NO_TRAN", "TRAN_SLIDE", "UNCERTIFIED").contains(item.getWarningType().toUpperCase()))
                .map(item -> {
                    Tuple<List<MerchantEsResultBean>, Long> result =
                            getMerchantEarlyWarningDetails(queryScope, agentNode, item, PageRequest.of(0, 1));
                    item.setWaringCount(result.v2());
                    if (StringUtils.isNotBlank(item.getWarningImg())) {
                        String img = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, item.getWarningImg(), DateUtils.addSecond(new Date(), 3600));
                        item.setWarningImg(img);
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }

    @CacheData
    @Override
    public Tuple<List<MerchantEsResultBean>, Long> getMerchantEarlyWarningDetails(QueryScope queryScope, String agentNode, MerchantWarningBean warningBean, PageRequest pageRequest) {
        Tuple<List<MerchantEsResultBean>, Long> result = new Tuple<>(new ArrayList<>(), 0L);
        if (warningBean == null || StringUtils.isBlank(warningBean.getWarningType())) {
            return result;
        }
        switch (warningBean.getWarningType().toUpperCase()) {
            case "NO_TRAN":
                result = MerchantSearchUtils.merchantOfLongTimeNoTrans(queryScope, agentNode, warningBean.getNoTranDay(), pageRequest);
                break;
            case "TRAN_SLIDE":
                String slideRate = Optional.ofNullable(warningBean.getTranSlideRate()).map(BigDecimal::toString).orElse("30");
                result = MerchantSearchUtils.merchantOfTradeSlide(queryScope, agentNode, slideRate);
                List<MerchantEsResultBean> collect = result.v1().stream().skip(pageRequest.getOffset()).limit(pageRequest.getPageSize()).collect(Collectors.toList());
                result = new Tuple<>(collect, result.v2());
                break;
            case "UNCERTIFIED":
                result = MerchantSearchUtils.merchantOfUncertified(queryScope, agentNode, pageRequest);
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public MerchantWarningBean queryMerchantEarlyWaring(String warningId, String loginAgentNo) {
        MerchantWarningBean merchantWarning = merchantDao.getMerchantWarning(warningId, loginAgentNo);
        if (merchantWarning != null && StringUtils.isNotBlank(merchantWarning.getWarningImg())) {
            String img = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, merchantWarning.getWarningImg(), DateUtils.addSecond(new Date(), 3600));
            merchantWarning.setWarningImg(img);
        }
        return merchantWarning;
    }

    @Override
    public List<MerchantEsResultBean> listMerchantByNos(List<String> merchantNos) {
        if (CollectionUtils.isEmpty(merchantNos)) {
            return new ArrayList<>();
        }
        return merchantDao.listMerchantByNos(merchantNos);
    }

    @Override
    public boolean isOpenAgentUpdateBpSwitch(String merchantNo) {
        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
        if (merchantInfo == null) {
            return false;
        }

        AgentInfo agentInfo = agentInfoDao.selectByAgentNo(merchantInfo.getAgentNo());
        if (agentInfo == null) {
            return false;
        }
        String code = FunctionManageConfigEnums.FUNCTION_MANAGE_003.getCode();
        Map<String, Object> functionMap = merchantDao.findFunctionManage(code);
        if (functionMap == null || !"1".equals(Objects.toString(functionMap.get("function_switch")))) {
            return false;
        }
        // 看是否需要自审
        if ("1".equals(Objects.toString(functionMap.get("agent_control")))) {//代理商为开启
            Map<String, Object> autoManage = merchantDao.findActivityIsSwitch(agentInfo.getOneLevelId(), code, agentInfo.getTeamId());
            if (autoManage == null) {
                return false;
            }
        }
        // 获取组织配置
//        String functionManage003 = sysConfigDao.getStringValueByKey(FunctionManageConfigEnums.FUNCTION_MANAGE_003.name());
//        FmConfig003 fmConfig003 = (FmConfig003) GsonUtils.fromJson2Bean(functionManage003, FunctionManageConfigEnums.FUNCTION_MANAGE_003.getJsonClass());
//        // 组织配置不为空或者配置中的组织包该商户的组织,只能向下调
//        if (fmConfig003 != null && fmConfig003.contain(merchantInfo.getTeamId(), merchantInfo.getTeamEntryId())) {
//            return false;
//        }
        return true;
    }
}
