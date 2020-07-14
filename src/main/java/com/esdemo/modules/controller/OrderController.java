package com.esdemo.modules.controller;

import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.OrderTransStatus;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.OrderSwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.AccessService;
import com.esdemo.modules.service.AcqMerchantService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.OrderEsService;
import com.esdemo.modules.utils.MerchantSearchUtils;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.esdemo.frame.enums.EsNpospField.ORDER_TYPE;
import static com.esdemo.frame.enums.EsNpospJoinType.ORDER;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/order")
@Api(description = "交易模块")
@RestController
/*@SignValidate(needSign = false)
@LoginValid(needLogin = false)*/
public class OrderController {

    @Resource
    private OrderEsService orderEsService;
    @Resource
    private AccessService accessService;
    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private AcqMerchantService acqMerchantService;

    @ApiOperation(value = "业绩明细", notes = OrderSwaggerNotes.ACHIEVEMENT_DETAIL)
    @PostMapping("/achievementDetail/{pageNo}/{pageSize}")
    @KqSwaggerDeveloped
    public ResponseBean achievementDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                          @PathVariable(required = false) int pageNo,
                                          @PathVariable(required = false) int pageSize,
                                          @RequestBody(required = false) Map<String, String> bodyParams) {

        Map<String, Object> res = new HashMap<>();
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;

        String queryAgentNo = "", queryAgentNode = "";
        String currAgentNode = userInfoBean.getAgentNode();
        String currAgentNo = userInfoBean.getAgentNo();

        String agentNo = bodyParams.get("agentNo");
        String queryScopeCode = bodyParams.get("queryScope");
        String timeUnitCode = bodyParams.get("timeUnit");
        try {
            if (StringUtils.isBlank(timeUnitCode) || !Arrays.asList(TimeUnitEnum.DAY.getUnit(), TimeUnitEnum.MONTH.getUnit()).contains(timeUnitCode)) {
                return ResponseBean.error("查询参数不合法");
            }
            QueryScope queryScope = StringUtils.isBlank(queryScopeCode) ? QueryScope.ALL : QueryScope.getByScopeCode(queryScopeCode);
            TimeUnitEnum timeUnit = TimeUnitEnum.getByTimeUnitCode(timeUnitCode);
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(currAgentNode, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                AgentInfo queryAgentInfo = agentInfoService.queryAgentInfo(agentNo);
                if (null == queryAgentInfo) {
                    return ResponseBean.error("查询代理商信息不存在");
                }
                queryAgentNo = agentNo;
                queryAgentNode = queryAgentInfo.getAgentNode();
            } else {
                queryAgentNo = currAgentNo;
                queryAgentNode = currAgentNode;
            }
            AgentInfo queryAgentInfo = new AgentInfo();
            queryAgentInfo.setAgentNode(queryAgentNode);
            queryAgentInfo.setAgentNo(queryAgentNo);

            return ResponseBean.success(orderEsService.summaryAchievementByTerms(queryAgentInfo, false, null, queryScope, timeUnit, pageNo, pageSize));

        } catch (Exception e) {
            log.error("查询代理商{}按{}维度查询业绩明细异常{}", queryAgentNo, timeUnitCode, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "数据-数据统计", notes = OrderSwaggerNotes.DATA_CENSUS)
    @PostMapping("/dataCensus")
    @KqSwaggerDeveloped
    public ResponseBean dataCensus(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                   @RequestBody(required = false) Map<String, String> bodyParams) {

        Map<String, Object> res = new HashMap<>();
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());

        String queryAgentNo = "", queryAgentNode = "";
        String currAgentNode = userInfoBean.getAgentNode();
        String currAgentNo = userInfoBean.getAgentNo();

        String agentNo = bodyParams.get("agentNo");
        String queryScopeCode = bodyParams.get("queryScope");
        try {
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(currAgentNode, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                AgentInfo queryAgentInfo = agentInfoService.queryAgentInfo(agentNo);
                if (null == queryAgentInfo) {
                    return ResponseBean.error("查询代理商信息不存在");
                }
                queryAgentNo = agentNo;
                queryAgentNode = queryAgentInfo.getAgentNode();
            } else {
                queryAgentNo = currAgentNo;
                queryAgentNode = currAgentNode;
            }

            //查询范围
            QueryScope queryScope = QueryScope.getByScopeCode(queryScopeCode);

            //排除首笔交易
            Map<String, List<String>> notFields = ImmutableMap.of(ORDER_TYPE.getFieldName(), Arrays.asList(new String[]{"2", "3"}));

            //近6月交易量及笔数（不包含当天往前推180天的数据）
            Date now = new Date();
            Date beginDay = DateUtils.addDays(now, -180);
            Date endDay = DateUtils.addDays(now, -1);
            String beginTimeStr = DateUtil.format(beginDay, "yyyy-MM-dd") + " 00:00:00";
            String endTimeStr = DateUtil.format(endDay, "yyyy-MM-dd") + " 23:59:59";
            EsSearchBean searchBean = EsSearchBean.builder().typeName(ORDER.getTypeName()).agentNode(queryAgentNode).queryScope(queryScope)
                    .startCreateTime(beginTimeStr).endCreateTime(endTimeStr).transStatus(OrderTransStatus.SUCCESS.getStatus())
                    .notFields(notFields).build();
            Tuple<Long, BigDecimal> summaryOrderRes = orderEsService.summaryOrderCountAndAmountByTerms(searchBean);

            //累计商户
            MerchantSumBean allMerchant = MerchantSearchUtils.queryAllMerchant(queryScope, queryAgentNode);
            Long totalMerCount = null == allMerchant ? 0 : allMerchant.getTotal();

            //累计商代理商
            Long totalAgentCount = MerchantSearchUtils.queryAllAgent(queryScope, queryAgentNo, queryAgentNode);

            //机具总数、已激活数量
            Long totalTerminalCount = acqMerchantService.censusTerminalCount(queryAgentNode, queryScope, false);
            Long activeTerminalCount = acqMerchantService.censusTerminalCount(queryAgentNode, queryScope, true);

            Map<String, Object> result = new HashMap<>();
            result.put("orderAmount", summaryOrderRes.v2());
            result.put("orderCount", summaryOrderRes.v1());
            result.put("totalMerCount", totalMerCount);
            result.put("totalAgentCount", totalAgentCount);
            result.put("totalTerminalCount", totalTerminalCount);
            result.put("activeTerminalCount", activeTerminalCount);
            return ResponseBean.success(result);

        } catch (Exception e) {
            log.error("查询代理商{}数据-数据统计异常{}", queryAgentNo, e);
            return ResponseBean.error("获取数据统计失败，请稍候再试");
        }
    }
}