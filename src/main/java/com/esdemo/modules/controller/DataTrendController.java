package com.esdemo.modules.controller;

import cn.hutool.core.map.MapUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.EsNpospJoinType;
import com.esdemo.frame.enums.OrderTransStatus;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.utils.DataBundle;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.DataTrendSwaggerNotes;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.*;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.esdemo.frame.enums.EsNpospField.ORDER_TYPE;

/**
 * @Title：agentApi2
 * @Description：数据趋势
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/data")
@Api(description = "数据模块")
@RestController
/*@LoginValid(needLogin = false)
@SignValidate(needSign = false)*/
public class DataTrendController {

    @Resource
    private OrderEsService orderEsService;
    @Resource
    private OrderService orderService;
    @Resource
    private MerchantEsService merchantEsService;
    @Resource
    private AgentEsService agentEsService;
    @Resource
    private AccessService accessService;
    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private ProfitService profitService;

    @ApiOperation(value = "数据-收入趋势", notes = DataTrendSwaggerNotes.VIEW_INCOME_TREND)
    @PostMapping("/viewIncomeTrend")
    @KqSwaggerDeveloped
    public ResponseBean viewIncomeTrend(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                        @RequestBody(required = false) Map<String, String> bodyParas) {

        String agentNo = userInfoBean.getAgentNo();
        String select_type = bodyParas.get("select_type");
        try {
            //起始时间
            String start_time = null;
            //结束时间
            String end_time = null;
            //获取当前时间
            LocalDateTime now = LocalDateTime.now();
            //初始数据，默认值均为0
            Map initData = new HashMap();
            Period per = null;
            if (Objects.equals("1", select_type)) {
                LocalDateTime d7 = now.minusDays(7);
                LocalDateTime d1 = now.minusDays(1);
                start_time = d7.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
                end_time = d1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));

                per = Period.between(d7.toLocalDate(), d1.toLocalDate());
                int sub = per.getDays() + 1;
                for (int i = 0; i < sub; i++) {
                    initData.put(d7.toLocalDate().plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd")), 0.0);
                }
            }
            if (Objects.equals("2", select_type)) {
                //6月前月首日期
                LocalDateTime m6 = now.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
                //1个月前月末日期
                LocalDateTime m1 = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                start_time = m6.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
                end_time = m1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));

                per = Period.between(m6.toLocalDate(), m1.toLocalDate());
                int sub = per.getMonths() + 1;
                for (int i = 0; i < sub; i++) {
                    initData.put(m6.toLocalDate().plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM")), 0.0);
                }
            }

            DataBundle data = DataBundle.build()
                    .bind("select_type", select_type)
                    .bind("agent_no", agentNo)
                    .bind("start_time", start_time)
                    .bind("end_time", end_time);
            //从库里查询数据
            List<Map<String, Object>> list = profitService.getProfitTendencyGroupByTime(data);
            //集合转字典
            Map list2Map = list.stream().collect(Collectors.toMap(it -> it.get("X"), it -> it.get("Y")));
            //将查询到的数据填充至初始数据中
            initData.putAll(list2Map);
            //将初始数据转为List结构
            list = (List<Map<String, Object>>) initData.keySet().stream()
                    //按键升序排序,其实把initData的实现改成TreeMap就可以免除此排序,此次仅为了应用新功能而应用
                    .sorted(Comparator.comparing(key -> StringUtils.filterNull(key)))
                    .map((key) -> MapUtil.builder().put("X", key).put("Y", initData.get(key)).build())
                    .collect(Collectors.toList());
            return ResponseBean.success(list);

        } catch (Exception e) {
            log.error("代理商{}数据->收入趋势异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取收入趋势失败，请稍候再试");
        }
    }

    @ApiOperation(value = "数据-新增商户趋势、交易量趋势、新增代理商趋势", notes = DataTrendSwaggerNotes.VIEW_DATA_TREND)
    @PostMapping("/viewDataTrend")
    @KqSwaggerDeveloped
    public ResponseBean viewDataTrend(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                      @RequestBody(required = false) Map<String, String> bodyParas) {

        Map<String, Object> mapRes = new LinkedHashMap<>();

        String agentNode = userInfoBean.getAgentNode();
        try {
            String agentNo = bodyParas.get("agentNo");
            String queryScope = bodyParas.get("queryScope");
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(agentNode, agentNo)) {
                    return ResponseBean.error("操作不合法，请规范操作");
                }
                Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo(agentNo);
                if (!CollectionUtils.isEmpty(agentInfo)) {
                    agentNode = StringUtils.filterNull(agentInfo.get("agent_node"));
                }
            } else {
                agentNo = userInfoBean.getAgentNo();
            }
            //获取交易量趋势
            //排除首笔交易
            Map<String, List<String>> notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3"}));
            EsSearchBean searchBean = EsSearchBean.builder().typeName(EsNpospJoinType.ORDER.getTypeName())
                    .queryScope(QueryScope.getByScopeCode(queryScope)).agentNode(agentNode).agentNo(agentNo)
                    .transStatus(OrderTransStatus.SUCCESS.getStatus()).notFields(notFields).build();
            Tuple<List<KeyValueBean>, List<KeyValueBean>> transOrderTrend = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
            Map<String, Object> transOrderTrendMap = new HashMap<>();
            transOrderTrendMap.put("sevenDayTrend", transOrderTrend.v1());
            transOrderTrendMap.put("halfYearTrend", transOrderTrend.v2());
            mapRes.put("transOrderTrend", transOrderTrendMap);

            //获取新增商户趋势
            searchBean = EsSearchBean.builder().typeName(EsNpospJoinType.MERCHANT.getTypeName())
                    .queryScope(QueryScope.getByScopeCode(queryScope)).agentNode(agentNode).agentNo(agentNo).build();
            Tuple<List<KeyValueBean>, List<KeyValueBean>> newlyMerTrend = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
            Map<String, Object> newlyMerTrendMap = new HashMap<>();
            newlyMerTrendMap.put("sevenDayTrend", newlyMerTrend.v1());
            newlyMerTrendMap.put("halfYearTrend", newlyMerTrend.v2());
            mapRes.put("newlyMerTrend", newlyMerTrendMap);

            //获取新增代理商趋势
            searchBean = EsSearchBean.builder().typeName(EsNpospJoinType.AGENT.getTypeName())
                    .queryScope(QueryScope.getByScopeCode(queryScope)).agentNode(agentNode).agentNo(agentNo).build();
            Tuple<List<KeyValueBean>, List<KeyValueBean>> newlyAgentTrend = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
            Map<String, Object> newlyAgentTrendMap = new HashMap<>();
            newlyAgentTrendMap.put("sevenDayTrend", newlyAgentTrend.v1());
            newlyAgentTrendMap.put("halfYearTrend", newlyAgentTrend.v2());
            mapRes.put("newlyAgentTrend", newlyAgentTrendMap);

            return ResponseBean.success(mapRes);
        } catch (Exception e) {
            log.error("代理商{}数据->新增商户趋势、交易量趋势、新增代理商趋势异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取数据趋势失败，请稍候再试");
        }
    }

    @SignValidate(needSign = false)
    @OldSwaggerDeveloped
    @ApiOperation(value = "数据->查询预警信息详情", notes = SwaggerNotes.GET_MERCHANT_EARLY_WARNING_DETAILS)
    @PostMapping("/queryMerchantEarlyWarningDetails/{pageNo}/{pageSize}")
    public ResponseBean getMerchantEarlyWarningDetails(@PathVariable int pageNo,
                                                       @PathVariable int pageSize,
                                                       @RequestBody MerchantSearchBean searchBean,
                                                       @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agentNode = accessService.checkAndGetAgentNode(userInfoBean.getAgentNode(), searchBean.getAgentNo());
        MerchantWarningBean warningBean = merchantEsService.queryMerchantEarlyWaring(searchBean.getWarningId(), userInfoBean.getAgentNo());
        if (warningBean == null) {
            return ResponseBean.success();
        }
        pageNo = pageNo - 1 <= 0 ? 0 : pageNo - 1;
        pageSize = pageSize <= 1 ? 1 : pageSize;
        Tuple<List<MerchantEsResultBean>, Long> warningDetails =
                merchantEsService.getMerchantEarlyWarningDetails(searchBean.getQueryScope(), agentNode, warningBean, PageRequest.of(pageNo, pageSize));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(warningDetails.v1())) {
            warningDetails.v1().forEach(item -> {
                if (StringUtils.isBlank(item.getMerchantName())) {
                    item.setMerchantName(item.getMerchantNo());
                }
                if (StringUtils.isBlank(item.getAgentName())) {
                    item.setAgentName(item.getAgentNo());
                }
                // 手机号打码显示
                item.setMobilePhone(StringUtils.mask4MobilePhone(item.getMobilePhone()));
                boolean isDirectMerchant = StringUtils.equalsIgnoreCase(item.getAgentNo(), userInfoBean.getAgentNo());
                item.setDirectMerchant(isDirectMerchant);
                // 非直营商户,不显示商户号
                if (!isDirectMerchant) {
                    item.setMerchantName("");
                }
            });
        }
        Map<String, Object> result = new HashMap<>();
        result.put("merchantWarning", warningBean);
        result.put("merchantList", warningDetails.v1());
        return ResponseBean.success(result, warningDetails.v2());
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "数据->查询预警商户汇总信息", notes = SwaggerNotes.QUERY_MERCHANT_EARLY_WARNING)
    @PostMapping("/queryMerchantEarlyWarning")
    public ResponseBean queryMerchantEarlyWarning(@RequestBody MerchantSearchBean searchBean,
                                                  @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agentNode = accessService.checkAndGetAgentNode(userInfoBean.getAgentNode(), searchBean.getAgentNo());
        List<MerchantWarningBean> warningBeanList = merchantEsService.queryMerchantEarlyWarning(searchBean.getQueryScope(), agentNode, userInfoBean.getAgentNo());
        return ResponseBean.success(warningBeanList);
    }

//    @ApiOperation(value = "数据->本月新增商户/代理商 累积商户/代理商", notes = SwaggerNotes.QUERY_MERCHANT_AND_AGENT_DATA)
//    @PostMapping("/queryMerchantAndAgentData")
//    public ResponseBean queryMerchantAndAgentData(@RequestBody MerchantSearchBean searchBean,
//                                                  @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
//        String agentNode = accessService.checkAndGetAgentNode(userInfoBean.getAgentNode(), searchBean.getAgentNo());
//        MerchantSumBean currentMonthMerchant = MerchantSearchUtils.queryCurrentMonthData(searchBean.getQueryScope(), agentNode, EsNpospJoinType.MERCHANT);
//        MerchantSumBean currentMonthAgent = MerchantSearchUtils.queryCurrentMonthData(searchBean.getQueryScope(), agentNode, EsNpospJoinType.AGENT);
//        MerchantSumBean allMerchant = MerchantSearchUtils.queryAllMerchant(searchBean.getQueryScope(), agentNode);
//        Long allAgentCount = MerchantSearchUtils.queryAllAgent(searchBean.getQueryScope(), agentNode);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("currentMonthMerchant", currentMonthMerchant);
//        result.put("allMerchant", allMerchant);
//        result.put("currentMonthAgent", currentMonthAgent);
//        result.put("allAgentCount", allAgentCount);
//        return ResponseBean.success(result);
//    }
}
