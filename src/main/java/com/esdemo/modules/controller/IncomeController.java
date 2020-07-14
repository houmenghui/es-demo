package com.esdemo.modules.controller;

import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.IncomeSwaggerNotes;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/income")
@Api(description = "收入模块")
@RestController
/*@SignValidate(needSign = false)
@LoginValid(needLogin = false)*/
public class IncomeController {

    @Resource
    private IncomeService incomeService;
    @Resource
    private AccessService accessService;
    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private TransBatchService transBatchService;
    @Resource
    private HpbBatchService hpbBatchService;

    @ApiOperation(value = "收入明细", notes = IncomeSwaggerNotes.INCOME_DETAIL)
    @PostMapping("/incomeDetail/{pageNo}/{pageSize}")
    @KqSwaggerDeveloped
    public ResponseBean incomeDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
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
        String timeUnitCode = bodyParams.get("timeUnit");
        try {
            if (StringUtils.isBlank(timeUnitCode) || !Arrays.asList(TimeUnitEnum.DAY.getUnit(), TimeUnitEnum.MONTH.getUnit()).contains(timeUnitCode)) {
                return ResponseBean.error("查询参数不合法");
            }
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

            return ResponseBean.success(incomeService.summaryIncomeByTerms(queryAgentNode, queryAgentNo, timeUnit, pageNo, pageSize));

        } catch (Exception e) {
            log.error("查询代理商{}按{}维度查询收入明细异常{}", queryAgentNo, timeUnitCode, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "数据-我的收入", notes = IncomeSwaggerNotes.MY_INCOME_CENSUS)
    @PostMapping("/myIncomeCensus")
    @KqSwaggerDeveloped
    public ResponseBean myIncomeCensus(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {

        Map<String, Object> res = new HashMap<>();
        BigDecimal totalProfitIncome = BigDecimal.ZERO;
        BigDecimal totalHpbIncome = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;

        String queryAgentNo = userInfoBean.getAgentNo();
        String queryAgentNode = userInfoBean.getAgentNode();
        Date now = new Date();
        try {
            //这里统计的是所有收入，所以当前月之前的从月统计表获取，当前月的收入从日统计表获取

            //1、先获取之前月统计
            Date beforeMonth = DateUtils.addMonths(now, -1);
            String beforeMonthEndTimeStr = DateUtil.format(DateUtil.endOfMonth(beforeMonth), "yyyy-MM-dd") + " 23:59:59";
            List<Map<String, Object>> monthProfitRes = transBatchService.summaryProfitIncomeByTerms(TimeUnitEnum.MONTH, queryAgentNode, null, beforeMonthEndTimeStr);
            List<Map<String, Object>> monthHpbRes = hpbBatchService.summaryHpbIncomeByTerms(TimeUnitEnum.MONTH, queryAgentNode, null, beforeMonthEndTimeStr);
            //逐月累加
            if (!CollectionUtils.isEmpty(monthProfitRes)) {
                for(Map<String, Object> profitMap : monthProfitRes){
                    String profitStr = StringUtils.filterNull(profitMap.get("total_money"));
                    totalProfitIncome = totalProfitIncome.add(StringUtils.isBlank(profitStr) ? BigDecimal.ZERO : new BigDecimal(profitStr));
                }
            }
            if (!CollectionUtils.isEmpty(monthHpbRes)) {
                for(Map<String, Object> hpbMap : monthHpbRes){
                    String hpbStr = StringUtils.filterNull(hpbMap.get("total_money"));
                    totalHpbIncome = totalHpbIncome.add(StringUtils.isBlank(hpbStr) ? BigDecimal.ZERO : new BigDecimal(hpbStr));
                }
            }

            //2、再获取当前月日统计
            String currMonthBeginTimeStr = DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd") + " 00:00:00";
            List<Map<String, Object>> dayProfitRes = transBatchService.summaryProfitIncomeByTerms(TimeUnitEnum.DAY, queryAgentNode, currMonthBeginTimeStr, null);
            List<Map<String, Object>> dayHpbRes = hpbBatchService.summaryHpbIncomeByTerms(TimeUnitEnum.DAY, queryAgentNode, currMonthBeginTimeStr, null);
            //逐日累加
            if (!CollectionUtils.isEmpty(dayProfitRes)) {
                for(Map<String, Object> profitMap : dayProfitRes){
                    String profitStr = StringUtils.filterNull(profitMap.get("total_money"));
                    totalProfitIncome = totalProfitIncome.add(StringUtils.isBlank(profitStr) ? BigDecimal.ZERO : new BigDecimal(profitStr));
                }
            }
            if (!CollectionUtils.isEmpty(dayHpbRes)) {
                for(Map<String, Object> hpbMap : dayHpbRes){
                    String hpbStr = StringUtils.filterNull(hpbMap.get("total_money"));
                    totalHpbIncome = totalHpbIncome.add(StringUtils.isBlank(hpbStr) ? BigDecimal.ZERO : new BigDecimal(hpbStr));
                }
            }

            totalIncome = totalProfitIncome.add(totalHpbIncome);
            res.put("totalProfitIncome", totalProfitIncome);
            res.put("totalHpbIncome", totalHpbIncome);
            res.put("totalIncome", totalIncome);
            return ResponseBean.success(res);

        } catch (Exception e) {
            log.error("查询代理商{}数据-我的收入异常{}", queryAgentNo, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }
}