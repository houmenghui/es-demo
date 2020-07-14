package com.esdemo.modules.controller;

import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.ExplainMarkEnum;
import com.esdemo.frame.enums.OrderTransStatus;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.utils.DateUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.IndexSwaggerNotes;
import com.esdemo.modules.bean.EsSearchBean;
import com.esdemo.modules.bean.Tuple;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.*;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.esdemo.frame.enums.EsNpospField.ORDER_TYPE;
import static com.esdemo.frame.enums.EsNpospJoinType.MERCHANT;
import static com.esdemo.frame.enums.EsNpospJoinType.ORDER;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/index")
@Api(description = "首页数据模块")
@RestController
/*@LoginValid(needLogin = false)
@SignValidate(needSign = false)*/
public class IndexController {

    @Resource
    private OrderEsService orderEsService;
    @Resource
    private MerchantEsService merchantEsService;
    @Resource
    private TransBatchService transBatchService;
    @Resource
    private HpbBatchService hpbBatchService;
    @Resource
    private IncomeService incomeService;
    @Resource
    private AgentInfoService agentInfoService;


    @ApiOperation(value = "问号说明",
            notes = IndexSwaggerNotes.EXPLAIN_OF_MARK)
    @PostMapping("/explainOfMark")
    @KqSwaggerDeveloped
    public ResponseBean explainOfMark(@RequestBody Map<String, String> bodyParams) {
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        String markType = bodyParams.get("markType");
        if(StringUtils.isBlank(markType) || !EnumUtils.isValidEnum(ExplainMarkEnum.class, markType)){
            return ResponseBean.error("此说明信息不存在");
        }
        ExplainMarkEnum explainMarkEnum = EnumUtils.getEnum(ExplainMarkEnum.class, markType);
        Map<String, Object> result = new HashMap<>();
        result.put("context", explainMarkEnum.getContext());
        result.put("buttonText", explainMarkEnum.getButtonText());
        return ResponseBean.success(result);
    }

    @ApiOperation(value = "首页->今日业绩（收入（元）、交易量（元）、交易笔数、新增商户、新增商户交易量（元））",
            notes = IndexSwaggerNotes.LOAD_CURR_DAY_DATA)
    @PostMapping("/loadCurrDayData")
    @KqSwaggerDeveloped
    public ResponseBean loadCurrDayData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        Date now = new Date();
        String beginTimeStr = DateUtil.format(now, "yyyy-MM-dd") + " 00:00:00";
        try {
            String currAgentNode = userInfoBean.getAgentNode();
            String currAgentNo = userInfoBean.getAgentNo();

            //排除首笔交易
            Map<String, List<String>> notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3"}));

            //本日订单交易笔数与金额统计
            EsSearchBean searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).agentNode(currAgentNode).queryScope(QueryScope.ALL)
                    .startCreateTime(beginTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus()).notFields(notFields).build();
            Tuple<Long, BigDecimal> summaryOrderRes = orderEsService.summaryOrderCountAndAmountByTerms(searchBean);

            //本日新增商户统计
            searchBean = EsSearchBean.builder().typeName(MERCHANT.getTypeName()).agentNode(currAgentNode).queryScope(QueryScope.ALL)
                    .startCreateTime(beginTimeStr).build();
            long dayAddMerCount = merchantEsService.summaryMerCountByTerms(searchBean);

            //本日新增商户交易金额统计
            searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).agentNode(currAgentNode).queryScope(QueryScope.ALL)
                    .startCreateTime(beginTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus()).notFields(notFields).build();
            Tuple<Long, BigDecimal> summaryAddMerOrderRes = orderEsService.summaryAddMerOrderCountAndAmountByTerms(searchBean);

            //今日收入(分润收入+活动补贴)
            BigDecimal dayIncome = incomeService.sumTodayTotalIncome(currAgentNo);

            Map<String, Object> result = new HashMap<>();
            result.put("dayIncome", dayIncome);
            result.put("dayOrderAmount", summaryOrderRes.v2());
            result.put("dayOrderCount", summaryOrderRes.v1());
            result.put("dayAddMerCount", dayAddMerCount);
            result.put("dayAddMerOrderAmount", summaryAddMerOrderRes.v2());
            return ResponseBean.success(result);

        } catch (Exception e) {
            log.error("当前登录代理商{}首页->今日业绩下发异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取首页今日业绩失败，请稍候再试");
        }
    }

    @ApiOperation(value = "首页->本月业绩（分润收入（元）、活动补贴收入（元）、交易量（元）、" +
            "交易笔数、新增商户、新增商户交易量（元））", notes = IndexSwaggerNotes.LOAD_CURR_MONTH_DATA)
    @PostMapping("/loadCurrMonthData")
    @KqSwaggerDeveloped
    public ResponseBean loadCurrMonthData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                          @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        Date now = new Date();
        String beginTimeStr = DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd") + " 00:00:00";
        String endTimeStr = DateUtil.format(DateUtils.addDate(now, -1), "yyyy-MM-dd") + " 23:59:59";
        try {
            String currAgentNode = userInfoBean.getAgentNode();
            String currAgentNo = userInfoBean.getAgentNo();
            //查询范围
            String queryScopeCode = bodyParams.get("queryScope");
            QueryScope queryScope = QueryScope.getByScopeCode(queryScopeCode);

            //排除首笔交易
            Map<String, List<String>> notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3"}));

            //本月订单交易笔数与金额统计
            EsSearchBean searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).agentNode(currAgentNode).queryScope(queryScope)
                    .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                    .notFields(notFields).build();
            Tuple<Long, BigDecimal> summaryOrderRes = orderEsService.summaryOrderCountAndAmountByTerms(searchBean);

            //本月新增商户统计
            searchBean = EsSearchBean.builder().typeName(MERCHANT.getTypeName()).agentNode(currAgentNode).queryScope(queryScope)
                    .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).build();
            long monthAddMerCount = merchantEsService.summaryMerCountByTerms(searchBean);

            //本月新增商户交易金额统计
            searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).agentNode(currAgentNode).queryScope(queryScope)
                    .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                    .notFields(notFields).build();
            Tuple<Long, BigDecimal> summaryAddMerOrderRes = orderEsService.summaryAddMerOrderCountAndAmountByTerms(searchBean);

            //本月收入
            Map<String, BigDecimal> monthIncomeMap = incomeService.sumMonthIncome(currAgentNode, now, queryScope);

            Map<String, Object> result = new HashMap<>();
            result.put("monthProfitIncome", monthIncomeMap.get("profitIncome"));
            result.put("monthActivityIncome", monthIncomeMap.get("hpbIncome"));
            result.put("monthOrderAmount", summaryOrderRes.v2());
            result.put("monthOrderCount", summaryOrderRes.v1());
            result.put("monthAddMerCount", monthAddMerCount);
            result.put("monthAddMerOrderAmount", summaryAddMerOrderRes.v2());
            result.put("explain", "注：本月业绩不包含当天的数据");
            return ResponseBean.success(result);

        } catch (Exception e) {
            log.error("当前登录代理商{}首页->本月业绩下发异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取本月业绩失败，请稍候再试");
        }
    }

    @ApiOperation(value = "首页获取待办事项显示状态", notes = IndexSwaggerNotes.GET_TODO_STATUS_DOC)
    @PostMapping("/getTodoStatus")
    @KqSwaggerDeveloped
    public ResponseBean getTodoStatus(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String currAgentNo = userInfoBean.getAgentNo();
        Map<String, Object> result =agentInfoService.getTodoStatus(currAgentNo);
        return ResponseBean.success(result);
    }

}
